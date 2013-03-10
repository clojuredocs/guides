---
title: "Clojure Macros and Metaprogramming"
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

This guide covers Clojure 1.5.



## Before You Read This Guide

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

The Reader produces data structures (in part that's why "code is data" in homoiconic
languages) that are then evaluated:

 * Literals (e.g., strings, integers, vectors) evaluate to themselves
 * Lists evaluate to invocations (calls) of functions and so on
 * Symbols are resolved to a var value

Expressions that can be evaluated (invoked) are known as *forms*. Forms are:

 * Functions
 * Macros
 * Special forms

### Special Forms

While Clojure reader can be extended in Clojure itself, some parts of what
forms the syntax of Clojure is built into the compiler.

Such forms are called *special forms*. They are

 * . (the dot special form)
 * .. (the double dot special form)
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

``` clojure
user> (macroexpand '(and true false true))
;; formatted for readability
(let* [and__3822__auto__ true]
  (if and__3822__auto__
      (clojure.core/and false true)
      and__3822__auto__))
```



## First Taste of Macros

Some programming languages include an `unless` expression (or statement) that is
the opposite of `if`. Clojure is not one of them but it can be added by using
a macro:

``` clojure
(defmacro unless
  [condition & forms]
  `(if ~(not condition)
     ~@forms))
```

This macro can be used like similarly to the `if` form:

``` clojure
(unless (= 1 2)
  "one does not equal two"
  "one equals two. How come?")
```

Just like the `if` special form, this macro produces an expression that
returns a value:

``` clojure
(unless (= 1 2)
  "one does not equal two"
  "one equals two. How come?")
```

in fact, this is because the macro piggybacks on the `if` form.
To see what the macro expands to, we can use `clojure.core/macroexpand-1`:

``` clojure
(macroexpand-1 '(unless (= 1 2) true false))
;= (if false (do true false))
```

This simplistic macro and the way we expanded it with `macroexpand-1`
demonstrates three features of the Clojure reader that are used when
writing macros:

 * Quote (')
 * Syntax quote (`)
 * Unquote (~)
 * Unquote splicing (~@)

## Quote

Quote supresses evaluation of the form that follows it. In other words,
instread of being treated as an invocation, it will be treated as a list.

Compare:

``` clojure
;; this form is evaluated by calling the clojure.core/+ function
(+ 1 2 3)
;= 6
;; quote supresses evaluation so the + is treated as a regular
;; list element
'(+ 1 2 3)
;= (+ 1 2 3)
```

The syntax quote supresses evaluation of the form that follows it and
all nested forms. It is similar to templating languages where parts
of the template are "fixed" and parts are "inserted" (evaluated).
The syntax quote makes the form that follows it "a template".

## Unquote

Unquote then is how parts of the template are forced to be evaluated
(act similarly to variables in templates in templating languages).

Let's take another look at the same `unless` macro:

``` clojure
(defmacro unless
  [condition & forms]
  `(if ~(not condition)
     ~@forms))
```

and how we invoke it:

``` clojure
(unless (= 1 2)
  "one does not equal two"
  "one equals two. How come?")
```

When the macro is expanded, the condition local in this example has the value
of `(= 1 2)` (a list). We want `unless` to perform boolean evaluation on it,
and that's what unquote (`~`) does as can be seen from macroexpansion:

(macroexpand-1 '(unless (= 1 2) true false))
;= (if false (do true false))

Compare this with what the macro expands to when the unquote is removed:

``` clojure
;; incorrect, missing unquote!
(defmacro unless
  [condition & forms]
  `(if (not condition)
     (do ~@forms)))

(macroexpand-1 '(unless (= 1 2) true false))
;= (if (clojure.core/not user/condition) (do true false))
```

### Implementation Details

The unquote operator is replaced by the reader with a call to a core
Clojure function, `clojure.core/unquote`.

## Unquote-splicing

Some macros take multiple forms. This is common in DSLs, for example.
Each of those forms is often need to be quoted and concatenated.

The unquote-splicing operator (`~@`) is a convenient way to do it:

``` clojure
user> (defmacro unsplice
        [& coll]
        `(do ~@coll))
;= #'user/unsplice

(macroexpand-1 '(unsplice (def a 1) (def b 2)))
;= (do (def a 1) (def b 2))

(unsplice (def a 1) (def b 2))
;= #'user/b

a
;= 1
b
;= 2
```

### Implementation Details

The unquote-splicing operator is replaced by the reader with a call to a core
Clojure function, `clojure.core/unquote-splicing`.


## Evaluation

TBD


## gen-sym

TBD


## Macroexpansions

During macro development, it is important to be able to test the macro
and see what data structures the macro expands to. This can be done
with two functions in the core Clojure library:

 * `clojure.core/macroexpand-1`
 * `clojure.core/macroexpand`
 * `clojure.walk/macroexpand-all`

The difference between the two is that `macroexpand-1` will expand the macro
only once. If the result contains calls to other macros, those won't be expanded.
`macroexpand`, however, will continue expanding all macros until the top level form
is no longer a macro.

Both macroexpansion functions take quoted forms:

``` clojure
(macroexpand '(and true false true))
```

Macro expansion functions can be used to find out that `and` is a macro implemented on top of
the `if` special form, for example:

``` clojure
user> (macroexpand '(and true false true))
;; formatted for readability
(let* [and__3822__auto__ true]
  (if and__3822__auto__
      (clojure.core/and false true)
      and__3822__auto__))
```

### Full Macroexpansion

Neither `macroexpand-1` nor `macroexpand` expand nested
forms. To fully expand macros including those in nested forms, there is `clojure.walk/macroexpand-all`,
which, however, is not part of Clojure core and does not behave exactly the same way
the compiler does.


## Difference Between Quote and Syntax Quote

TBD


## Clojure Compilation Process

TBD


## Security Considerations

`clojure.core/read-string` *can execute arbitrary code* and *must not* be used
on inputs coming from untrusted sources. This behavior is controlled by the `clojure.core/*read-eval*`
var. Starting with Clojure 1.5, the default value of `*read-eval*` is `false`.

`*read-eval*` can be disabled via a property when starting the JVM:

```
-Dclojure.read.eval=false
```

When reading Clojure forms from untrusted sources, use `clojure.edn/read-string`, which is
does not perform arbitrary code execution and is safer. `clojure.edn/read-string` implements
the [EDN format](https://github.com/edn-format/edn), a subset of Clojure syntax for data
structures. `clojure.edn` was introduced in Clojure 1.5.


## Contributors

Michael Klishin <michael@defprotocol.org>, 2013 (original author)
