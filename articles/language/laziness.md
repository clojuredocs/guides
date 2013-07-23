---
title: "Laziness in Clojure"
layout: article
---

This guide covers:

  * What are lazy sequences
  * Pitfalls with lazy sequences
  * How to create functions that produce lazy sequences
  * How to force evaluation

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on
Github](https://github.com/clojuredocs/guides).



## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.5.



## Overview

Clojure is not a [lazy language](http://en.wikipedia.org/wiki/Lazy_evaluation).

However, Clojure supports *lazily evaluated sequences*. This means that sequence elements are not
available ahead of time and produced as the result of a computation. The computation
is performed as needed. Evaluation of lazy sequences is known as *realization*.

Lazy sequences can be infinite (e.g. the sequence of Fibonacci numbers, a sequence of
dates with a particular interval between them, and so on). If a lazy sequence is finite,
when its computation is completed, it becomes *fully realized*.

When it is necessary to fully realize a lazy sequence, Clojure provides a way to
*force evaluation* (force realization).


## Benefits of Lazy Sequences

Lazy sequences have two main benefits:

 * They can be infinite
 * Full realization of interim results can be avoided


## Producing Lazy Sequences

Lazy sequences are produced by functions. Such functions either use the `clojure.core/lazy-seq` macro
or other functions that produce lazy sequences.

`clojure.core/lazy-seq` accepts one or more forms that produce a sequence of `nil` (when the sequence
is fully realized) and returns a seqable data structure that invokes the body the first time
the value is needed and then caches the result.

For example, the following function produces a lazy sequence of random UUIDs strings:

``` clojure
(import java.util.UUID)

(defn uuid-seq
  []
  (lazy-seq
   (cons (str (UUID/randomUUID))
         (uuid-seq))))
```

Another example:

``` clojure
(defn fib-seq
  "Returns a lazy sequence of Fibonacci numbers"
  ([]
     (fib-seq 0 1))
  ([a b]
     (lazy-seq
      (cons b (fib-seq b (+ a b))))))
```

Both examples use `clojure.core/cons` which prepends an element to a sequence. The sequence
can in turn be lazy, which both of the examples rely on.

Even though both of these sequences are infinite, taking first N elements from each does
return successfully:

``` clojure
(take 3 (uuid-seq))
;= ("8da1b70e-7d4d-4972-b4af-48ed248c5568" "b0bc5c2c-f5ff-4733-b3ce-b0499a1a0ccc" "26d52a65-cde5-4d57-bf7f-97e3440fb3a5")

(take 10 (fib-seq))
l= (1 1 2 3 5 8 13 21 34 55)

(take 20 (fib-seq))
;= (1 1 2 3 5 8 13 21 34 55 89 144 233 377 610 987 1597 2584 4181 6765)
```

## Realizing Lazy Sequences (Forcing Evaluation)

Lazy sequences can be forcefully realized with `clojure.core/dorun` and
`clojure.core/doall`. The difference between the two is that `dorun`
throws away all results and is supposed to be used for side effects,
while `doall` returns computed values:

``` clojure
(dorun (map inc [1 2 3 4]))
;= nil

(doall (map inc [1 2 3 4]))
;= (2 3 4 5)
```


## Commonly Used Functions That Produce Lazy Sequences

Multiple frequently used `clojure.core` functions return lazy sequences,
most notably:

 * `map`
 * `filter`
 * `remove`
 * `range`
 * `take`
 * `take-while`
 * `drop`
 * `drop-while`

The following example uses several of these functions to return 10 first
even numbers in the range of [0, n):

``` clojure
(take 10 (filter even? (range 0 100)))
;= (0 2 4 6 8 10 12 14 16 18)
```

Several functions in `clojure.core` are designed to produce lazy
sequences:

 * `repeat`
 * `iterate`
 * `cycle`

For example:

``` clojure
(take 3 (repeat "ha"))
;= ("ha" "ha" "ha")

(take 5 (repeat "ha"))
;= ("ha" "ha" "ha" "ha" "ha")

(take 3 (cycle [1 2 3 4 5]))
;= (1 2 3)

(take 10 (cycle [1 2 3 4 5]))
;= (1 2 3 4 5 1 2 3 4 5)

(take 3 (iterate (partial + 1) 1))
;= (1 2 3)

(take 5 (iterate (partial + 1) 1))
;= (1 2 3 4 5)
```


## Lazy Sequences Chunking

There are two fundamental strategies for implementing lazy sequences:

 * Realize elements one-by-one
 * Realize elements in groups (chunks, batches)

In Clojure 1.1+, lazy sequences are *chunked* (realized in chunks).

For example, in the following code

``` clojure
(take 10 (range 1 1000000000000))
```

one-by-one realization would realize one element 10 times. With chunked sequences,
elements are realized ahead of time in chunks (32 elements at a time).

This reduces the number of realizations and, for many common workloads, improves
efficiency of lazy sequences.


## Contributors

Michael Klishin <michael@defprotocol.org>, 2013 (original author)
