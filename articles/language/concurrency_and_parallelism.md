---
title: "Concurrency and Parallelism in Clojure"
layout: article
---

## About this guide

This guide covers:

 * Clojure's identity/value separation
 * Clojure reference types and their concurrency semantics: atoms, refs, agents, vars
 * How to use java.util.concurrent from Clojure
 * Other approaches to concurrency available on the JVM
 * Other topics related to concurrency

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.


## Overview

One of Clojure design goals was to make concurrent programming
easier. The thinking is that as modern CPUs add more and more cores
and the number of CPUs is increasing as well, the biggest contributor
to application throughput will come from making use of those
resources.

The key design decision was making Clojure data structures immutable
(persistent) and separating the concepts of *identity* and
*value*. The importance of immutability cannot be over-emphasized:
immutable values can be safely shared between threads, eliminate many
concurrency hazards, and ultimately make it easier for developers to
reason about their programs.

However, a language that only has immutable data structures and no way
to change (mutate) program state is not very useful. The
identity/value separation makes state mutations (e.g. incrementing a
counter or adding an element to a list) possible in ways that
have known guarantees with respect to concurrency. This separation largely
eliminates the need for explicit use of locks, which is possible in Clojure
but typically not necessary.

To put it another way: "changing variables" in Clojure happens
differently from many other languages, in ways that are predictable
from the concurrency perspective and eliminate many concurrency hazards.

Next lets take a closer look to the identity/value separation.


## Identity/Value Separation ("on State And Identity")

In Clojure, *values* are immutable. They never change. For example, a number is a value.
A map `{:language "Clojure"}` is a value. A vector with 3 elements is a value.

When you attempt to modify a value (a data structure), a new value is produced instead. This
is known as *persistent data structures* (the word "persistent" has nothing to do with
storing data on disk). *Identity* is a named entity (e.g. a list of active chat group
members or a counter) that changes over time and at any given moment references a value.

For example, the current value of a counter may be `42`. After incrementing it, the value
is `43` but it is still the same counter, the same identity. This is different from, say, Java
or Ruby, where variables serve as identities that always point to 

TBD: an images to illustrate these concepts


## Clojure Reference Types

Clojure has multiple reference types. Each reference type has its own concurrency semantics.

### vars

TBD

### atoms

Atoms are references that change atomically (changes become immediately visible to all threads,
changes are guaranteed to be synchronized by the JVM). If you come from Java background,
atoms are basically atomic references from `java.util.concurrent` with a functional twist
to them.

TBD

### agents

Agents are references that are updated asynchronously: updates happen at a later, unknown point
in time, in a thread pool.

TBD

### refs

TBD


## java.util.concurrent

TBD


## Other Approaches to Concurrency

TBD


## Wrapping Up

TBD
