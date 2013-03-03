---
title: "Rationale"
layout: article
---

Static typing has well known benefits. For example, statically typed languages catch many common 
programming errors at the earliest time possible: compile time.
Types also serve as an excellent form of (machine checkable) documentation that
almost always augment existing hand-written documentation.

Languages without static type checking (dynamically typed) bring other benefits.
Without the strict rigidity of mandatory static typing, they can provide more flexible and forgiving
idioms that can help in rapid prototyping.
Often the benefits of static type checking are desired as the program grows.

This work adds static type checking (and some of its benefits) to Clojure, a dynamically typed language, 
while still preserving idioms that characterise the language.
It allows static and dynamically typed code to be mixed so the programmer can use whichever
is more appropriate.

(For a detailed treatment, see my Honours Dissertation, [A Practical Optional Type System for Clojure](https://github.com/downloads/frenchy64/papers/ambrose-honours.pdf))

