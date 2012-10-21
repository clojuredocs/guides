---
title: "Concurrency and Parallelism in Clojure"
layout: article
---

## About this guide

This guide covers:

 * Clojure's identity/value separation
 * Clojure reference types and their concurrency semantics: atoms, refs, agents, vars
 * Dereferencing, futures and promises
 * How to use java.util.concurrent from Clojure
 * Other approaches to concurrency available on the JVM
 * Other topics related to concurrency

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.


## Before You Read This Guide

This is the most hardcore guide of the entire Clojure documentation
project. It describes concepts that are simple but may seem foreign at first.
These concepts are some of the key points of Clojure
design. Understanding them may take some time for folks without
concurrent programming background. Don't let this learning curve
discourage you.

If some parts are not clear, please ask for clarification [on the
mailing
list](https://groups.google.com/forum/?fromgroups#!forum/clojure) or
[file an issue](https://github.com/clojuredocs/cds/issues) on GitHub.
We will work hard on making this guide easy to follow with edits and
images to illustrate the concepts.


## Introduction and Terminology

Before we get to the Clojure features related to concurrency, lets lay a foundation and briefly
cover some terminology.

<table class="table-striped table-bordered table">
  <thead>
    <tr>
      <th>Term</th>
      <th>Definition This Guide Uses</th>
    </tr>
  </thead>

  <tbody>
    <tr>
      <td>Concurrency</td>
      <td>when multiple are making progress, whether it is via time-slicing or parallelism</td>
    </tr>
    <tr>
      <td>Parallelism</td>
      <td>A condition that arises when at least two threads are executing simultaneously, e.g. on multiple cores or CPUs.</td>
    </tr>
    <tr>
      <td>Shared State</td>
      <td>When multiple threads of execution need to mutate (modify) one or more pieces of state (e.g. variables, identities)</td>
    </tr>
    <tr>
      <td>Mutable Data Structures</td>
      <td>Data structures that, when changed, are updated "in place"</td>
    </tr>
    <tr>
      <td>Immutable Data Structures</td>
      <td>Data structures that, when changed, produce new data structures (copies), possibly with optimizations such as internal structural sharing</td>
    </tr>
    <tr>
      <td>Concurrency Hazards</td>
      <td>Conditions that occur in concurrent programs that prevent program from being correct (behaving the way its authors intended).</td>
    </tr>
    <tr>
      <td>Shared Mutable State</td>
      <td>When shared state is made of mutable data structures. A ripe ground for concurrency hazards.</td>
    </tr>
  </tbody>
</table>

There are many concurrency hazards, some of the most common and well known are:

<table class="table-striped table-bordered table">
  <thead>
    <tr>
      <th>Concurrency Hazard</th>
      <th>Brief Description</th>
    </tr>
  </thead>

  <tbody>
    <tr>
      <td>Race Condition</td>
      <td>A condition when the outcome is dependent on timing or relative ordering of events</td>
    </tr>
    <tr>
      <td>Deadlock</td>
      <td>When two or more threads are waiting on each other to finish or release a shared resource, thus waiting forever and not making any progress</td>
    </tr>
    <tr>
      <td>Live Lock</td>
      <td>When two or more threads are technically performing computation but not doing any useful work (not making progress), for example,
          because they endlessly pass a piece of data to each other but never actually process it</td>
    </tr>
    <tr>
      <td>Starvation</td>
      <td>When a thread is not given regular access to a shared resource and cannot make progress.</td>
    </tr>
  </tbody>
</table>

These hazards are not exclusive to threads and can happen with OS
processes, runtime processes and any other executaion processes. They
are also not specific to a particular runtime or VM (e.g. the JVM) or
programming language. Admittedly, some languages make it significantly
easier to write corrent, safe concurrent programs, but none are
completely immune to concurrency hazards. More often than not,
concurrency hazards are algorithmic problems, languages just encourage
or discourage certain practices and techniques.

*Thread-safe* code is code that is always executed correctly and does
not suffer from concurrency hazards even when executed concurrently
from multiple threads.


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
or Ruby, where variables serve as identities that (typically) point to a mutable value
and modified in place.

TBD: an images to illustrate these concepts

Identities in Clojure can be of several types, known as *reference types*.


## Clojure Reference Types

Clojure has multiple reference types. Each reference type has its own concurrency semantics.

### atoms

Atoms are references that change atomically (changes become immediately visible to all threads,
changes are guaranteed to be synchronized by the JVM). If you come from Java background,
atoms are basically atomic references from `java.util.concurrent` with a functional twist
to them.

Lets jump right in and demonstrate how atoms work with an example. We know that Clojure data
structures are immutable by default. Adding an element to a collection really produces a new
collection. In such case, how does one keep a shared list (say, of active connections to a server
or recently crawled URLs) and mutate it in a thread-safe manner? We will demonstrate how to
accomplish this with an atom.

To create an atom, use the `clojure.core/atom` function. It takes initial atom value as the argument:

``` clojure
(def currently-connected (atom []))
```

The line above makes the atom `currently-connected` an empty vector. To access atom's value, use
`clojure.core/deref` or the `@atom` reader form:

``` clojure
(def currently-connected (atom []))

@currently-connected
;; ⇒ []
(deref currently-connected)
;; ⇒ []
currently-connected
;; ⇒ #<Atom@614b6b5d: []>
```

As the returned values demonstrate, the atom itself is a reference. To access its current value, you
*dereference* it. Dereferencing will be covered in more detail later in this guide. For now, it is
sufficient to say that dereferencing returns the current value of an atom and a few other Clojure
reference types and data structures.

Locals can be atoms, too:

``` clojure
(let [xs (atom [])]
  @xs)
;; ⇒ []
```

Now to the most interesting part: adding elements to the collection. To mutate an atom, use `clojure.core/swap!`
which takes an atom and a function that takes the current value of the atom and must return a new value:

``` clojure
(swap! currently-connected (fn [xs] (conj xs "chatty-joe")))
;; ⇒ ["chatty-joe"]
currently-connected
;; ⇒ #<Atom@614b6b5d: ["chatty-joe"]>
@currently-connected
;; ⇒ ["chatty-joe"]
```

To demonstrate this graphically, initial atom state looks like this:

![Atom state 1](/assets/images/language/concurrency_and_parallelism/atom_state1.png)

and then we mutated it with `swap!`:

![Atom state 2](/assets/images/language/concurrency_and_parallelism/atom_state2.png)

For the readers familiar with the atomic types from the [java.util.concurrent.atomic](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/atomic/package-summary.html) package,
it should sound very familiar. The only difference is that instead of setting a value, atoms are mutated
with a function. This is both because Clojure is a functional language and because with this approach,
`clojure.core/swap!` can *retry the operation* safely. This implies that the function you provide to
`swap!` is *pure* (has no side effects).

Occasionally you will need to mutate the value of an atom the same way you do it with atomic references in Java:
by setting them to a specific value. This is what `clojure.core/reset!` does. It takes an atom and the new value:

``` clojure
@currently-connected
;; ⇒ ["chatty-joe"]
(reset! currently-connected [])
;; ⇒ []
@currently-connected
;; ⇒ []
```

`reset!` may be useful in test suites to reset an atom state between test executions, but it should be
used sparingly in your implementation code. Consider using `swap!` first.

TBD: demonstrate retries under high update rates


#### Summary and Use Cases

Atoms is the most commonly used concurrent feature in Clojure. It covers many cases and lets developers
avoid explicit locking. Atoms cover a lot of use cases and are very fast. It's fair to say that
when you need uncoordinated reference types (e.g. not Software Transactional Memory), the rule of
thumb is, "start with an atom, then see".

It is not uncommon to initialize an atom in a local and then return it from the function and share
a piece of state with other functions and/or threads.


### agents

Agents are references that are updated asynchronously: updates happen at a later, unknown point
in time, in a thread pool.

TBD



### vars

Vars are the reference type you are already familiar with: you define them via the `def` special form:

``` clojure
(def url "http://en.wikipedia.org/wiki/Margarita")
```

Functions defined via `defn` are also stored in vars. Vars can be dynamically scoped. They have
*root bindings* that are initially visible to all threads. When defining a var
with `def`, you define a var that only has root binding, so its value will be the same, no matter
what thread you use it from:

``` clojure
(def url "http://en.wikipedia.org/wiki/Margarita")
;; ⇒ #'user/url
(.start (Thread. (fn []
                   (println (format "url is %s" url)))))
;; outputs "url is http://en.wikipedia.org/wiki/Margarita"
;; ⇒ nil
(.start (Thread. (fn []
                   (println (format "url is %s" url)))))
;; outputs "url is http://en.wikipedia.org/wiki/Margarita"
;; ⇒ nil
```

### Dynamic Scoping. Thread-local Bindings.

To temporarily change var value, we need to make the var dynamic by adding `:dynamic true` to its
metadata and then use `clojure.core/binding`:

``` clojure
(def ^:dynamic *url* "http://en.wikipedia.org/wiki/Margarita")
;; ⇒ #'user/*url*
(println (format "*url* is now %s" *url*))
;; outputs "*url* is now http://en.wikipedia.org/wiki/Margarita"

(binding [*url* "http://en.wikipedia.org/wiki/Cointreau"]
  (println (format "*url* is now %s" *url*)))
;; outputs "*url* is now http://en.wikipedia.org/wiki/Cointreau"
;; ⇒ nil
```

Note that by convention, vars that are supposed to or may be dynamically scoped are named with leading
and trailing `*`, called "earmuffs".

In the example above, `binding` temporarily changed var's current value to a different URL. But that happened
in the same thread as the var was originally defined in. What makes vars interesting from the concurrency
point of view is that their bindings can be *thread-local* (yes, if you are familiar with thread-local variables
in Java or Ruby, it is very similar and serves largely the same purpose). To demonstrate, lets change
the example to spin up 3 threads and alter var value from them:

``` clojure
(def ^:dynamic *url* "http://en.wikipedia.org/wiki/Margarita")
;; ⇒ #'user/*url*
(println (format "*url* is now %s" *url*))
;; outputs "*url* is now http://en.wikipedia.org/wiki/Margarita"
;; ⇒ nil
(.start (Thread. (fn []
          (binding [*url* "http://en.wikipedia.org/wiki/Cointreau"]
            (println (format "*url* is now %s" *url*))))))
;; outputs "*url* is now http://en.wikipedia.org/wiki/Cointreau"
;; ⇒ nil
(.start (Thread. (fn []
                   (binding [*url* "http://en.wikipedia.org/wiki/Guignolet"]
                     (println (format "*url* is now %s" *url*))))))
;; outputs "*url* is now http://en.wikipedia.org/wiki/Guignolet"
;; ⇒ nil
(.start (Thread. (fn []
                   (binding [*url* "http://en.wikipedia.org/wiki/Apéritif"]
                     (println (format "*url* is now %s" *url*))))))
;; outputs "*url* is now http://en.wikipedia.org/wiki/Apéritif"
;; ⇒ nil
(println (format "*url* is now %s" *url*))
;; outputs "*url* is now http://en.wikipedia.org/wiki/Margarita"
;; ⇒ nil
```

As you can see, var scoping in different threads did not modify the var's value in the thread it was
originally defined in (its *root binding*). In real world cases, for example, it means that a multi-threaded
Web crawler can store some crawling state specific to a particular thread in a var and not
modify its initial (global) value.

#### How to Alter Var Root

Sometimes, however, modifying the root binding is necessary. This is done via `clojure.core/alter-var-root`
which takes a var (not its value) and a function that takes the old var value and returns a new one:

``` clojure
*url*
;; ⇒ "http://en.wikipedia.org/wiki/Margarita"
(.start (Thread. (fn []
                   (alter-var-root (var user/*url*) (fn [_] "http://en.wikipedia.org/wiki/Apéritif"))
                   (println (format "*url* is now %s" *url*)))))
;; outputs "*url* is now http://en.wikipedia.org/wiki/Apéritif"
;; ⇒ nil
*url*
;; ⇒ "http://en.wikipedia.org/wiki/Apéritif"
```

`clojure.core/var` is used to locate the var (`user/*url*` in our example executed in the REPL). Note that it
finds the var itself (the reference, the "box"), not its value (what the var evalutes to).

In the example above the function we use to alter var root ignores the current value and simply returns a
predefined string:

``` clojure
(fn [_] "http://en.wikipedia.org/wiki/Apéritif")
```

Such functions are common enough for `clojure.core` to provide a convenience higher-order function called
`clojure.core/constantly`. It takes a value and returns a function that, when executed, ignores all its parameters
and returns that value. So, the function above would be more idiomatically written as

``` clojure
*url*
;; ⇒ "http://en.wikipedia.org/wiki/Margarita"
(.start (Thread. (fn []
                   (alter-var-root (var user/*url*) (constantly "http://en.wikipedia.org/wiki/Apéritif"))
                   (println (format "*url* is now %s" *url*)))))
;; outputs "*url* is now http://en.wikipedia.org/wiki/Apéritif"
;; ⇒ nil
*url*
;; ⇒ "http://en.wikipedia.org/wiki/Apéritif"
```

When is `alter-var-root` used in real world scenarios? Some Clojure data store and API clients stores active connection
in a var, so initial connection requires root binding modification.

#### Summary and Use Cases

To summarize: vars can have dynamic scoping. They have root binding and can have thread-local binding.
As such, vars are good for storing pieces of program state that vary between threads but cannot
be stored in a function local. `alter-var-root` is used to alter root binding of a var. It is done
the functional way: by providing a function that takes the old var value and returns a new one.

To alter var root to a specific known value, use `clojure.core/constantly`.



### refs

TBD


## Dereferencing. Futures and Promises.

TBD


## java.util.concurrent

TBD


## Other Approaches to Concurrency

TBD


## Runtime Parallelism

Clojure was designed to be a hosted language. Its primary target, the JVM, provides runtime parallelism support.
JVM threads map 1:1 to kernel threads. Those will be executed in parallel given that enough cores are available
for to program.

In Clojure, many concurrency features are built on top of JVM threads and thus benefit from runtime parallelism
if the program is running on a multi-core machine.


## Books

Concurrency is a broad topic and it would be silly to think that we can cover it well in just one guide.
To get a better understanding of the subject, one can refer to a few excellent books:

 * [Java Concurrency in Practice](http://www.amazon.com/Java-Concurrency-Practice-Brian-Goetz/dp/0321349601) by Brian Goetz et al. is a true classic.
 * [Programming Concurrency on the JVM](http://pragprog.com/book/vspcon/programming-concurrency-on-the-jvm) demonstrates a range of concurrency features in several JVM languages.


## Wrapping Up

One of Clojure design goals was to make concurrent programming easier.

The key design decision was making Clojure data structures immutable
(persistent) and separating the concepts of *identity* (references) and
*value*. Immutable values eliminate many concurrency hazards, and ultimately make
it easier for developers to reason about their programs.

Atoms are arguably the most commonly used reference type when working with concurrency
(vars are used much more often but not for their concurrency semantics). Software Transactional Memory
is a more specialized feature and has certain limitations (e.g. I/O operations must not be
performed inside transactions). Finally, agents, futures and promises provide an array of
tools for working with asynchronous operations.

Concurrency is a hard fundamental problem. There is no single "best" solution or approach
to it. On the JVM, Clojure offers several concurrency-related features of its own but also
provides easy access to the `java.util.concurrent` primitives and libraries such as [Akka](http://akka.io/)
or [Jetlang](http://code.google.com/p/jetlang/).
