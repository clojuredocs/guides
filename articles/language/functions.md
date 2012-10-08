---
title: "Functions in Clojure"
layout: article
---

## About this guide

This guide covers:

 * How to define functions
 * How to invoke functions
 * Multi-arity functions
 * Variadic functions
 * Higher order functions
 * Other topics related to functions

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).


## Overview

Clojure is a functional programming language. Naturally, functions are very important part of Clojure.


## How To Define Functions

Functions are typically defined using the [defn](http://clojuredocs.org/clojure_core/clojure.core/defn) macro:

{% highlight clojure %}
(defn round
  [d precision]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/floor (* d factor)) factor)))
{% endhighlight %}

Type hints sometimes allow the compiler to avoid reflective method calls and/or produce significantly more efficient bytecode.
However, as a rule of thumb, it is usually not necessary to use type hints. Start writing your code without them. The compiler
is also free to ignore provided hints.

Functions can have doc strings (documentation strings) and it is a good idea to document functions that
are part of the public API:

{% highlight clojure %}
(defn round
  "Round down a double to the given precision (number of significant digits)"
  [d precision]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/floor (* d factor)) factor)))
{% endhighlight %}

In Clojure, function arguments may have optional type hints:

{% highlight clojure %}
(defn round
  [^double d ^long precision]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/floor (* d factor)) factor)))
{% endhighlight %}


Functions can also define *preconditions* and *postconditions* that put restrictions on argument values and
the value function returns:

{% highlight clojure %}
(defn round
  "Round down a double to the given precision (number of significant digits)"
  [^double d ^long precision]
  {:pre [(not-nil? d) (not-nil? precision)]}
  (let [factor (Math/pow 10 precision)]
    (/ (Math/floor (* d factor)) factor)))
{% endhighlight %}

In the example above, we use preconditions to check that both arguments are not nil. The `not-nil?` macro (or function) is not
demonstrated in this example and assumed to be implemented elsewhere.


## Anonymous Functions

Anonymous functions are defined using the `fn` special form:

{% highlight clojure %}
(fn [x]
  (* 2 x))
{% endhighlight %}

Anonymous functions can be assigned to locals, passed between functions (higher order functions are covered later in this document)
and returned from functions:

{% highlight clojure %}
(let [f (fn [x]
          (* 2 x))]
  (map f (range 0 10)))
{% endhighlight %}

There is also a reader macro for anonymous functions:

{% highlight clojure %}
(let [f #(* 2 %)]
  (map f (range 0 10)))
{% endhighlight %}

The `%` in the example above means "the first argument". To refer to more than one argument, use `%1`, `%2` and so on:

{% highlight clojure %}
;; an anonymous function that takes 3 arguments and adds them together
(let [f #(+ %1 %2 %3)]
  (f 1 2 3))
{% endhighlight %}

Please **use this reader macro sparingly**; excessive use may lead to unreadable code.


## How To Invoke Functions

Functions are invoked by placing a function to the leading position (*the calling position*) of a list:

{% highlight clojure %}
(format "Hello, %s" "world")
{% endhighlight %}

This works also if you have a function stored in a local, a var or passed as an argument:

{% highlight clojure %}
(let [f format]
  (f "Hello, %s" "world"))
{% endhighlight %}

Alternatively, you can call a function using [clojure.core/apply](http://clojuredocs.org/clojure_core/clojure.core/apply)

{% highlight clojure %}
(apply format "Hello, %s" ["world"])

(apply format "Hello, %s %s" ["Clojure" "world"])
{% endhighlight %}

`clojure.core/apply` is usually only necessary when calling variadic functions or having the list of arguments passed in
as a collection.


## Multi-arity Functions

Functions in Clojure can have multiple *arities*, or sets of arguments:

{% highlight clojure %}
(defn tax-amount
  ([amount]
     (tax-amount amount 35))
  ([amount rate]
     (Math/round (double (* amount (/ rate 100))))))
{% endhighlight %}

In the example above, the version of the function that takes only one argument (so called *one-arity* or *1-arity* function)
calls another version (*2-arity*) with a default parameter. This is a common use case for multiple arities: to have default
argument values. Clojure is a hosted language and JVM (and JavaScript VMs, for that matter) does not support default argument
values, however, it does support *method overloading* and Clojure takes advantage of this.

Arities in Clojure can only differ by the number of arguments, not types. This is because Clojure is strongly dynamically typed language and type information about
parameters may or may not be available to the compiler.

A larger example:

{% highlight clojure %}
(defn range
  ([]
    (range 0 Double/POSITIVE_INFINITY 1))
  ([end]
    (range 0 end 1))
  ([start end]
    (range start end 1))
  ([start end step]
    (comment Omitted for clarity)))
{% endhighlight %}




## Destructuring of Function Arguments

Sometimes function arguments are data structures: vectors, sequences, maps. To access parts of such
data structure, you may do something like this:

{% highlight clojure %}
(defn currency-of
  [m]
  (let [currency (get m :currency)]
    currency))
{% endhighlight %}

For vector arguments:

{% highlight clojure %}
(defn currency-of
  [pair]
  (let [amount   (first  pair)
        currency (second pair)]
    currency))
{% endhighlight %}

However, this is boilerplate code that has little to do with what the function really does. Clojure
lets developer **destructure** parts of arguments, for both maps and sequences.

### Positional Destructuring

Destructuring over vectors (**positional destructuring**) works like this: you replace the argument
with a vector that has "placeholders" (symbols) in positions you want to bind. For example, if the
argument is known to be a pair and you need second argument, it would look like this:

{% highlight clojure %}
(defn currency-of
  [[amount currency]]
  currency)
{% endhighlight %}

In the example above the first element in the pair is bound to `amount` and the second one is bound to
`currency`. So far so good. However, notice that we do not use the `amount` local. In that case, we can
ignore it by replacing it with an underscore:

{% highlight clojure %}
(defn currency-of
  [[_ currency]]
  currency)
{% endhighlight %}

Destructuring can nest (destructure deeper than one level):

{% highlight clojure %}
(defn first-first
  [[[i _] _]]
  i)
{% endhighlight %}

While this article does not cover `let` and locals, it is worth demonstrating that positional destructuring works
exactly the same way for let bindings:

{% highlight clojure %}
(let [pair         [10 :gbp]
      [_ currency] pair]
  currency)
{% endhighlight %}


### Map Destructuring

Destructuring over maps and records (**map destructuring**) works slightly differently:

{% highlight clojure %}
(defn currency-of
  [{currency :currency}]
  currency)
{% endhighlight %}

In this case example, we want to bind the value for key `:currency` to `currency`. Keys don't have to be
keywords:

{% highlight clojure %}
(defn currency-of
  [{currency "currency"}]
  currency)
{% endhighlight %}

{% highlight clojure %}
(defn currency-of
  [{currency 'currency}]
  currency)
{% endhighlight %}

When destructuring multiple keys at once, it is more convenient to use a slightly different syntax:

{% highlight clojure %}
(defn currency-of
  [{:keys [currency amount]}]
  currency)
{% endhighlight %}

The example above assumes that map keys will be keywords and we are interested in two values: `currency`
and `amount`. The same can be done for strings:

{% highlight clojure %}
(defn currency-of
  [{:strs [currency amount]}]
  currency)
{% endhighlight %}

and symbols:

{% highlight clojure %}
(defn currency-of
  [{:syms [currency amount]}]
  currency)
{% endhighlight %}

In practice, keywords are very commonly used for map keys so destructuring with `{:keys [...]}` is very common
as well.

Map destructuring also lets us specify default values for keys that may be missing:

{% highlight clojure %}
(defn currency-of
  [{:keys [currency amount] :or {currency :gbp}}]
  currency)
{% endhighlight %}

This is very commonly used for implementing functions that take "extra options" (faking named arguments support).


Just like with positional destructuring, map destructuring works exactly the same way for let bindings:

{% highlight clojure %}
(let [money               {:currency :gbp :amount 10}
     {currency :currency} money]
  currency)
{% endhighlight %}


## Variadic Functions

Variadic functions are functions that take varying number of arguments (some arguments are optional). Two examples
of such function in `clojure.core` are `clojure.core/str` and `clojure.core/format`:

{% highlight clojure %}
(str "a" "b")     ;= "ab"
(str "a" "b" "c") ;= "abc"

(format "Hello, %s" "world")              ;= "Hello, world"
(format "Hello, %s %s" "Clojure" "world") ;= "Hello, Clojure world"
{% endhighlight %}

To define a variadic function, prefix optional arguments with an ampersand (`&`):

{% highlight clojure %}
(defn log
  [message & args]
  (comment ...))
{% endhighlight %}

In the example above, one argument is requried and the rest is optional. Variadic functions
are invoked as usual:

{% highlight clojure %}
(defn log
  [message & args]
  (println "args: " args))

(log "message from " "192.0.0.76")
{% endhighlight %}

Running the example above in the REPL produces:

{% highlight clojure %}
user=> (log "message from " "192.0.0.76")
args:  (192.0.0.76)

user=> (log "message from " "192.0.0.76" "service:xyz")
args:  (192.0.0.76 service:xyz)
{% endhighlight %}

As you can see, optional arguments (`args`) are packed into a list.

### Extra Arguments (aka Named Parameters)

Named parameters are achieved through the use of destructuring a variadic function.

Approaching named parameters from the standpoint of destructuring a variadic function allows for more clearly readable function invocations.  This is an example of named parameters:

{% highlight clojure %}
(defn job-info
  [& {:keys [name job income] :or {job "unemployed" income "$0.00"}}]
  (if name
    [name job income]
    (println "No name specified")))
{% endhighlight %}

Using the function looks like this:

{% highlight clojure %}
user=> (job-info :name "Robert" :job "Engineer")
["Robert" "Engineer" "$0.00"]

user=> (job-info :job "Engineer")
No name specified
{% endhighlight %}

Without the use of a variadic argument list, you would have to call the function with a single map argument such as {:name "Robert" :job "Engineer}.

Keyword default values are assigned by use of the :or keyword followed by a map of keywords to their default value. Keywords not present and not given a default will be nil.

## Higher Order Functions

TBD



## Keywords as Functions

In Clojure, keywords can be used as functions. They take a map or record and look themselves up in it:

{% highlight clojure %}
(:age {:age 27 :name "Michael"}) ;= 27
{% endhighlight %}

This is commonly used with higher order functions:

{% highlight clojure %}
(map :age [{:age 45 :name "Joe"} {:age 42 :name "Jill"} {:age 17 :name "Matt"}]) ;= (45 42 17)
{% endhighlight %}

and the `->` macro:

{% highlight clojure %}
(-> [{:age 45 :name "Joe"} {:age 42 :name "Jill"}] first :name) ;= "Joe"
{% endhighlight %}


## Maps as Functions

Clojure maps are also functions that take keys and look up values for them:

{% highlight clojure %}
({:age 42 :name "Joe"} :name)    ;= "Joe"
({:age 42 :name "Joe"} :age)     ;= 42
({:age 42 :name "Joe"} :unknown) ;= nil
{% endhighlight %}

Note that this is **not true** for Clojure records, which are almost identical to maps in other
cases.


## Sets as Functions

{% highlight clojure %}
(#{1 2 3} 1)  ;= 1
(#{1 2 3} 10) ;= 10

(#{:us :au :ru :uk} :uk) ;= :uk
(#{:us :au :ru :uk} :cn) ;= nil
{% endhighlight %}

This is often used to check if a value is in a set:

{% highlight clojure %}
(when (countries :in)
  (comment ...))

(if (countries :in)
  (comment Implement positive case)
  (comment Implement negative case))
{% endhighlight %}

because everything but `false` and `nil` evaluates to `true` in Clojure.


## Wrapping Up

Functions are at the heart of Clojure. They are defined using the `defn` macro, can have multiple arities,
be variadic and support parameter destructuring. Function arguments and return value can optionally be
type hinted.

Functions are first class values and can be passed to other functions (called Higher Order Functions or HOFs).
This is fundamental to functional programming techniques.

Several core data types behave like functions. When used reasonably, this can lead to more concise, readable
code.


## Contributors

Michael Klishin <michael@defprotocol.org>, 2012 (original author)
