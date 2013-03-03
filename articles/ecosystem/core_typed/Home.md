---
title: "core.typed User Documentation Home"
layout: article
---

core.typed is an optional type system for Clojure.

## Quickstart

`(clojure.core.typed/ann v t)` gives var `v` the static type `t`.

`(clojure.core.typed/ann-form f t)` ensures form `f` is of the static type `t`.

`(clojure.core.typed/check-ns)` type checks the current namespace.

`(clojure.core.typed/cf t)` type checks the form `t`.

See the [Quick Guide](Quick Guide.html).

## [Rationale](Rationale.html)

Why core.typed exists, what can it do for you?

## Getting Started Guide

If you are new to core.typed, gradual type systems, or even types in general, and want to learn how
core.typed can help verify your programs, start here.

### [Introduction and Motivation](start/Introduction and Motivation.html)

We discuss some theory and design goals of core.typed.

### [Annotations](start/Annotations.html)

Where and how to annotate your code to help core.typed.

### [Types](Types.html)

Syntax and descriptions of core.typed types.

### [Filters](Filters.html)

An overview of filters for occurrence typing.

### Dotted Functions
### Polymorphism, Variance and F-Bounds
### Java Classes, Arrays and Interop
### Multimethods, Datatypes and Protocols

## [Limitations](Limitations.html) - Known issues

## Documentation Contributors

Ambrose Bonnaire-Sergeant (@ambrosebs)

Copyright 2013, Ambrose Bonnaire-Sergeant
