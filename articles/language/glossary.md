---
title: "Clojure Terminology Guide"
layout: article
---

A glossary of terminology specific to Clojure. Terms
are listed in alphabetical order.



## Terms


### arity

The number of arguments a function takes is its arity.  If it's
written to take a variable number of args, it's referred to as
[variadic](#variadic).

Functions can have multiple arity (for example, a function might have
2 different bodies: one for when 2 args are passed, and another when 3
args are passed).



### binding-form

Could mean one of two things:

 1. the expression you're binding to in a
    [let-binding](#let_binding). It might be a simple name, or it
    might be a data structure used for
    [destructuring](#destructuring).

 2. Clojure provides the `binding` macro, used for setting the
    thread-local value of a dynamic var. The whole expression (form)
    is sometimes referred to as the "binding [form](#form)".


### classpath

The search path used by the JVM to locate classes which are not
part of the Java standard class library. May include jar files.



### comparator

A function that takes two args and compares them.  Returns -1, 0, or 1
depending whether the first arg is less than, equal to or greater than
the second.  The stock comparator that Clojure.core comes with is
`compare`.



### coordinates

The "group-id/artifact-id version-string" identifier used in your
project.clj to indicate a particular dependency.

See also [libspec](#libspec).



### destructuring

The handy trick used in a [let-binding](#let_binding) to "unpack" the
values from a data structure into the locals you're going to use.  See
also [binding-form](#binding-form) and [the destructuring section in
the functions
guide](functions.html#destructuring_of_function_arguments).



### dereference


To get the value of a reference type. You can use the `deref` function
for this, or else some syntactic sugar: `@some-ref-type`.



### entry

A key/value pair in a map. Try `(type (first {:a 1 :b 2}))` and see
that it returns `clojure.lang.MapEntry`.



### form

A valid s-expression. For example: `(+ 1 1)` and `(defn foo [x] (* x
x))`.



### head retention

[Lazy](#lazy) sequences are still [persistent](#persistence). If you
make *another* data structure using one, the original lazy sequence
will be kept around and not garbage-collected. If the lazy sequence in
infinite, and grows very large, it can cause performance problems or
even an out-of-memory error. Accidentally keeping around a lazy
sequence like this is referred to as "head retention".


### idempotent

An operation that when given the same inputs will produce the same
result when called one or more times. An idempotent function may
produce a side effect, such a updating a ref or an atom, but will
only produce the side effect once. An idempotent function is
different than a pure function, in that a pure function will
produce no side effects.



### identity

A logical entity in your program that may change over time --- it may
take on different states at different times, but it still means the
same logical entity. Clojure uses [reference types](#reference_types)
to represent identities.



### implicit do

The bodies of some expressions act like `do` in that you can include
multiple expressions in them, and the expressions will be evaluated in
the order they appear, with the resulting value of the body being the
last expression evaluated. Forms that do this include: `when`,
`when-let`, `fn`, `defn`, `let`, `loop`, and `try`.



### intern

*todo*



### keyword

A Clojure scalar data type whose literal syntax looks `:like` `:this`.
They are like numbers and strings in that they evaluate to themselves,
and are most often seen being used as keys in [hash-maps](#map).

See also [namespaced keyword](#namespaced_keyword)

The term is also used when talking about functions that take "keyword
arguments", for example, something like: `(my-func :speed 42 :mass 2)`
(as opposed to `(my-func {:speed 42 :mass 2})`).



### lazy

Clojure can (and often does) create sequences for you that aren't
fully computed. Upon casual inspection they *look* just like a regular
list, but particular values in them are only computed the moment you
ask for them --- not sooner.

This has the added benefit that you can easily create infinite
sequences that don't consume infinite memory.

Many of the built-in Clojure functions return lazy sequences.

See also [realize](#realize).



### let-binding

AKA, "binding vector", or just "bindings": in a `let` (and expressions
that work like let, for example, `defn`, `loop`, `loop`, & `fn`), the
vector that comes first where you specify lexical bindings.

See also [binding form](#binding_form)



### libspec

*todo*



### map

Either refers to the built in `map` function, or else means "a
hash-map object".



### metadata

An extra map that you can attach to a collection value (or a symbol),
which contains data about the data you're attaching it to. Use `meta`
to see the metadata of a given value.



### namespaced keyword

When you put two colons in front of a keyword's name --- for example
::foo --- it is a so-called "namespaced keyword", and is expanded by
the reader to become :current-namespace/foo.



### persistence

See the [relevant section of the
introduction](../tutorials/introduction.html#values_immutability_and_persistence).



### predicate

A function taking one or more args and returning a boolean (`true` or
`false`). Its name typically ends with a question mark. Some examples:
`nil?`, `zero?`, `string?`.


### reader macro

Syntax that the Clojure reader recognizes as special syntactic sugar,
for example, `#""`, `#{}`, quoting, etc.



### realize

When the next value in a [lazy](#lazy) sequence is accessed for the
first time, and is computed so as to made available, it is said to
have been "realized".



### reference types

Vars, atoms, refs, and agents are all reference types. They are
mutable in the sense that you can change to what value they refer, and
Clojure provides thread-safe mechanisms for doing so.



### reify

*todo*



### rest args

The extra args passed to a [variadic](#variadic) function, for example
if `my-func` were defined like `(defn my-func [a b & more] ...)`, then
called like `(my-func 1 2 3 4 5)`, then 3, 4, & 5 are the "rest args".



### s-expression

*todo*



### state

The [value](#value) that a given [identity](#identity) may have at a
given time.  When you change the state of an identity, you're changing
to which value it refers. Clojure uses values to represent states.



### STM (Software Transactional Memory)

Software Transactional Memory (STM) is a concurrency control method to
coordinate and control access to shared storage as an alternative to
lock-based synchronization. Clojure's STM uses multiversion concurrency
control (MVCC) as an alternative to lock-based transactions, as well as
ensuring changes are made atomically, consistently, and in
isolation. It does this by taking a snapshot of the ref, making the
changes in isolation to the snapshot, and apply the result. If the STM
detects that another transaction has made an update to the ref, the
current transaction will be forced to retry.



### symbol

An identifier that refers to vars or local values.



### threading macros

The thread-first (`->`) and thread-last (`->>`) macros.  "Threading"
refers to how they pass values to each subsequent argument in the
macro, not concurrency.



### thrush

A combinator. Not the same thing as the [thread-first
macro](#threading-macros).  More info at
<http://blog.fogus.me/2010/09/28/thrush-in-clojure-redux/> if you're
curious.


### transaction

*todo*



### type erasure

Java-related: Java generics allow you to specify a type for a
collection.  This way you don't have to cast every object you pull out
of an ArrayList like in the old days. This is a courtesy of the java
compiler. The java runtime doesn't know about generics --- the
compiler does all the checking for you, then the type information is
discarded at runtime. In Clojure, this discarding is referred to as
type erasure.



### value

An immutable object, such as the number 1, the character `\a`, the
string "hello", or the vector `[1 2 3]`. In Clojure, all scalars and
built-in core data structures are values.



### variadic

A function that can take a variable number of arguments.
See also [rest args](#rest_args).
