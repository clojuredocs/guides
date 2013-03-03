---
title: "Macros and Metaprogramming"
layout: article
---

This guide covers:

  * Clojure macros
  * the Clojure compilation process

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on
Github](https://github.com/clojuredocs/cds).



## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.



## ## Before You Read This Guide

This is one of the most hardcore guide of the entire Clojure documentation
project. It describes concepts that are relatively unique to the Lisp family of languages
that Clojure belongs to. Understanding them may take some time for folks without
concurrent programming background. Don't let this learning curve
discourage you.

If some parts are not clear, please ask for clarification [on the
mailing
list](https://groups.google.com/forum/?fromgroups#!forum/clojure) or
[file an issue](https://github.com/clojuredocs/cds/issues) on GitHub.
We will work hard on making this guide easy to follow with edits and
images to illustrate the concepts.


## Overview

Clojure is a dialect of Lisp and while it departs with some features of "traditional" Lisps,
the fundamentals are there. One very powerful feature that comes with it is *macros*,
a way to do metaprogramming using the language itself. This is pretty different from
other languages known for good metaprogramming capabilities (e.g. Ruby) in that
in Clojure, metaprogramming does not mean string generation. Instead, it means
constructing a tree [of S-expressions, or lists]. This enables very powerful
DSLs (domain-specific languages).


## Clojure Reader

Reader is another name for parser. Unlike many other languages, reader in Clojure
can be extended in the language itself. It is also exposed to the language
with `clojure.core/read` and `clojure.core/read-string` functions that
return data structures:

``` clojure
user> (read-string "(if true :truth :false)")
;; here we get back a list that is not evaluated
;= (if true :truth :false)
```

Expressions that can be evaluated are known as *forms*.

### Special Forms

While Clojure reader can be extended in Clojure itself, some parts of what
forms the syntax of Clojure is built into the compiler (and implemented in Java).

Such forms are called *special forms*. They are

 * . (the dot special form)
 * new
 * set!
 * def
 * if
 * do
 * let
 * quote
 * var
 * fn
 * loop, recur
 * throw, try, catch
 * monitor-enter

Other forms are implemented with macros on top of special forms. For example, `and` is
implemented on top of `if`:



### Security Considerations

`*read-eval*` can be disabled via a property when starting the JVM:

```
-Dclojure.read.eval=false
```


## First Taste of Macros

TBD


## Evaluation

TBD


## gen-sym

TBD


## Macro Expansions

`clojure.core/macroexpand` can be used to find out that `and` is a macro implemented on top of
the `if` special form, for example:

``` clojure
user> (macroexpand '(and true false true))
;; formatted for readability
(let* [and__3822__auto__ true]
  (if and__3822__auto__
      (clojure.core/and false true)
      and__3822__auto__))
```

TBD


## Macros vs Metaprogramming In Other Languages

TBD


## Clojure Compilation Process

TBD




## Contributors

todo
