---
title: "Overview of clojure.core"
layout: article
---

## About this guide

This guide covers:

 * Key functions of `clojure.core`
 * Key macros of `clojure.core`
 * Key vars of `clojure.core`
 * Essential special forms

This guide is **by no means comprehensive** and does not try to explain each function/macro/form in depth. It is an overview,
the goal is to briefly explain the purpose of each item and provide links to other articles with more information.

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).


## Fundamentals

### let

Let allows binding of locals and defines an explicit scope for those bindings. The bindings are defined as a vector of [symbol value] pairs.

The body of a let statement also provides an implicit (do ...) that allows for multiple statements in the body of let.

A basic example:
{% highlight clojure %}
(let [x 1 y 2] (println x y)) ;; 1 2
{% endhighlight %}

Let can be nested, and the scope is lexically determined. This means that a binding's value is determined by the nearest binding form for that symbol.

This example basically demonstrates the lexical scoping of the let form.
{% highlight clojure %}
(let [x 1]
  (println x) ;; prints 1
  (let [x 2]
    (println x))) ;; prints 2
{% endhighlight %}

Let bindings are immutable and can be destructured.

### def

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### declare

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### defn

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### ns

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### if

If is a conditional operator. If evaluates the first expression and returns the value of the second expression provided for any value other than nil or false. The values nil and false will cause the third expression, if provided, to be returned. Nil and false will cause 'if' to return nil if no third expression is provided.

{% highlight clojure %}
user=> (if 0 "second") ;; 0 is a 'true' value
"second"

user=> (if nil "second" "third")
"third"

user=> (if (< 10 9) "second" "third") ;; returns false
"third"

user=> (if (seq '()) "second") ;; seq returns nil for an empty sequence
nil

user=> (if (nil? (= 1 2)) "second" "third") ;; differentiate between nil and false if needed
"third"
{% endhighlight %}

### when

When provides an implicit do form that is evaluated if an expression returns true, otherwise nil is returned. When does not provide an 'else'.

{% highlight clojure %}
user=> (when (= 1 2) (print "hey") 10)
nil

user=> (when (< 10 11) (print "hey") 10)
hey
10
{% endhighlight %}

### for

For allows for list comprehensions. For takes a vector of pairs of [binding collection]. For then assigns each sequential value in the collection to the binding form and evaluates them rightmost first. The results are returned in a lazy sequence.

For allows for explicit let, when and while through use of ":let []" ":when (expression)" ":while (expression)" in the binding vector.

{% highlight clojure %}
(for [x [1 2 3] y [4 5 6]] 
  [x y])
  
;; ([1 4] [1 5] [1 6] [2 4] [2 5] [2 6] [3 4] [3 5] [3 6])
{% endhighlight %}

:when only evaluates the body when a true value is returned by the expression provided

{% highlight clojure %}
(for [x [1 2 3] y [4 5 6]
      :when (and
             (even? x)
             (odd? y))]
  [x y])
  
;; ([2 5])
{% endhighlight %}

:while evaluates the body until a non-true value is reached. Note that the rightmost collection is fully bound to y before a non-true value of (< x 2) is reached. This demonstrates the order of the comprehension.

{% highlight clojure %}
(for [x [1 2 3] y [4 5 6]
      :while (< x 2)]
  [x y])
  
;; ([1 4] [1 5] [1 6])
{% endhighlight %}

{% highlight clojure %}

{% endhighlight %}

### doseq

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### apply

Apply effectively unrolls the supplied args and a collection into a list of arguments to the supplied function.

{% highlight clojure %}
(str ["Hel" "lo"])
"[\"Hel\" \"lo\"]" ;; not what we want, str is operating on the vector

user> (apply str ["Hel" "lo"]) ;; same as (str "Hel" "lo")
"Hello"
{% endhighlight %}

Apply prepends any supplied arguments to the form as well.

{% highlight clojure %}
(map + [[1 2 3] [1 2 3]]) ;; This attempts to add 2 vectors with +
;; ClassCastException   java.lang.Class.cast (Class.java:2990)

(apply map + [[1 2 3] [1 2 3]]) ;; same as (map + [1 2 3] [1 2 3])
;; (2 4 6)

(apply + 1 2 3 [4 5 6]) ;; same as  (+ 1 2 3 4 5 6)
;; 21
{% endhighlight %}

Note that apply can not be used with macros.

### require

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### import

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### loop, recur

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)



## Sequences

### count

Returns a count of the number of items in a collection. Nil returns a value of 0.

{% highlight clojure %}
(count "Hello")
;; 5

(count [1 2 3 4 5 6 7])
;; 7
{% endhighlight %}

Note that count does not return in constant time for all collections. This can be determined with (counted?). Lazy sequences must be realized to get a count of the items.

{% highlight clojure %}
(counted? "Hello")
;; false

(counted? (range 10) ;; will be fully realized when using (count (range 10))
;; false

(counted? [1 2 3 4 5]) ;; Constant time return of (count)
;; true 
{% endhighlight %}

### conj

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### get

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### assoc

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### dissoc

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### first

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### rest

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### empty?

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### empty

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### contains?

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### some

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### keys

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### vals

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### map

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### filter

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### remove

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### every?

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### get-in

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### update-in

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### assoc-in

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### select-keys

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### take

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### drop

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### take-while

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### drop-while

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

## Macros

### -> and ->> (the Threading Macros)

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Reference Types

### deref

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### atom

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### swap!

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### reset!

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### agent

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### ref

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### dosync

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### alter

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### commute

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### binding

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)



## Vars

### *clojure-version*

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
