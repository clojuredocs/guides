---
title: "Getting Started: Introduction and Motivation"
layout: article
---

core.typed is an optional type system for Clojure. If you are interesting in how core.typed
can help you verify your programs as correct, read on.

## "I use Clojure to avoid types!"

Many programmers use Clojure as relief from popular typed languages such as Java or C#.
Java's verbosity and redundant type annotations help make the move to Clojure feel liberating
and enjoyable: so why go back to types?

core.typed has a different story to tell:
- type checking is optional
  - only use the type system where you need it
- local type inference is used to infer local bindings
  - locals rarely need type annotations
- it can type check (mostly) normal Clojure code
  - the Clojure you know and love!

If Java has driven you away from types, core.typed could be pleasant surprise.
It might even become one of your go-to tools for code verification in Clojure.

## What are types?

This is a good question, especially in the context of a dynamically-typed (DT) language
where we don't have "types".

We use the term "type" to mean static type and "tag" for runtime tags.
Types only exist at compile time and are used by the static type system to model runtime
invariants and properties.

Thinking of compile-time and runtime as distinct phases in terms of types often helps.
The type system uses types to reason about the runtime behaviour of code, which can
also include tag invariants.

There are no types in Clojure, only tags. We can also say that Clojure has exactly one type: `Any` (subtype of all types).
The closest equivalent to types we have
are ad-hoc comments or doc-strings which describe the input/output behaviour
of functions.

For example, the `number?` predicate returns true if its (runtime) argument
has a tag that is a subtype of `java.lang.Number`, otherwise false. In core.typed
we use a type to model these invariants.
The tag of `number?` might be `IFn`, while its type is `[Any -> boolean :filters {:then (is Number 0) :else (! Number 0)}]`.

In summary:
- types only exist at compile time
- tags only exist at runtime

## Why types?

Why use types at all? A static type checker gives earlier and often clearer type errors.

For example, you might observe:
- fewer "Boolean is not an ISeq" errors without line numbers in production
- more "Cannot pass Boolean to second argument of map" with line numbers at compile time in development.

Types, when coupled with an appropriate doc-string, are excellent machine checkable documentation.
They never go out of date, and are often invaluable as a quick reminder of what a function does.

Types are useful when a program grows, especially when there are multiple contributors.
If a contribution passes the type system, we know that it is type correct (type errors are amongst
the most common user errors in programming).

## Great, types are the answer!

Not quite. Types can help verify that a program is basically correct, but not if it does the right thing.
Use as many verification techniques as you can: core.typed works great coupled with unit testing or
generative testing.

Clojure simply is not built with static typing in mind. It is impractical to expect core.typed alone
to prevent as many user errors as say Haskell's type system: core.typed either needs to choose some
subset of Clojure optimised for user error prevention, or attempt to check all Clojure code
while making some compromises (it does the latter).

This might seem discouraging, but in practice core.typed will catch all type errors in your code.
The problem is some Clojure idioms are so flexible it is often impossible to distinguish
between indended and unintended usage.

A small example: `map` accepts either `nil` or a `Seqable` as a second argument. It is perfectly
valid to provide an argument that is always `nil`, but it's probably not what the user intended.

So for best results, couple core.typed with all the usual testing/verification techniques.

## Before we begin

There are some details to keep in mind when using core.typed before you jump in to use it.

Read the [Quick Guide](../Quick Guide.html), and keep a copy handy when you follow along the rest of the tutorial.
