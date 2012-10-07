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

Arities in Clojure can only differ by the number of arguments, not types. This is because Clojure is strongly dynamicall


## Variadic Functions

TBD


## Destructuring of Function Arguments

TBD


## Higher Order Functions

TBD


## Wrapping Up

TBD
