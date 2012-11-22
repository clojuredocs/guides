---
title: "Mathematics with Clojure"
layout: article
---

This cookbook covers working with mathematics in Clojure, using
built-in functions, contrib libraries, and parts of the JDK via
interoperability.

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on
Github](https://github.com/clojuredocs/cds).


## Preliminaries

Some examples herein make use of the
[math.numeric-tower](https://github.com/clojure/math.numeric-tower)
and
[math.combinatorics](https://github.com/clojure/math.combinatorics)
contrib libraries. It's assumed that either you have the following in
your source code's `ns` macro:

``` clojure
(:require [clojure.math.numeric-tower :as math]
          [clojure.math.combinatorics :as combo])
```

or else in the repl you've loaded them like so:

``` clojure
(require '[clojure.math.numeric-tower :as math])
(require '[clojure.math.combinatorics :as combo])
```



## Recipes

### Simple Math

``` clojure
(+ 3 4)    ;=> 7
(- 3 4)    ;=> -1
(* 3 4)    ;=> 12
(/ 3 4)    ;=> 3/4  (an exact ratio)
(/ 3.0 4)  ;=> 0.75

(inc 5)    ;=> 6
(dec 5)    ;=> 4
```

For doing integer division and getting remainders (modulus), see the
docs for
[quot](http://clojuredocs.org/clojure_core/clojure.core/quot),
[rem](http://clojuredocs.org/clojure_core/clojure.core/rem), and
[mod](http://clojuredocs.org/clojure_core/clojure.core/mod).

For exponents, square roots, rounding, ceiling, floor, absolute value,
and greatest/least common multiples, see the [docs for
math.numeric-tower](http://clojure.github.com/math.numeric-tower/).

### Trigonometry

Use what the Java platform provides, for example:

``` clojure
Math/PI       ;=> 3.14159...
(Math/sin x)
(Math/cos x)
(Math/tan x)
```

There are many more functions available, which you can read about in
the [docs for
java.lang.Math](http://docs.oracle.com/javase/7/docs/api/java/lang/Math.html).


### Combinatorics

For combinatoric functions (such as `combinations` and
`permutations`), see the [docs for
math.combinatorics](http://clojure.github.com/math.combinatorics/).
