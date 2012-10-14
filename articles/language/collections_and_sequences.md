---
title: "Collections and sequences in Clojure"
layout: article
---

## About this guide

This guide covers:

 * Collections in Clojure
 * Sequences in Clojure
 * Core collection types
 * Key operations on collections and sequences
 * Other topics related to collections and sequences

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.


## Overview

Clojure has two powerful abstractions: collections and sequences. When working with Clojure,
many operations are expressed as a series of operations on collections or sequences.

Most of Clojure's core library treats collections and sequences the same way, although
sometimes a distinction has to be made (e.g. with lazy infinite sequences).

`clojure.core` provides many fundamental operations on collections: such as `map`, `filter`,
`remove`, `take` and `drop`. Basic operations on collections and sequences are combined
 to implement more complex operations.

### Clojure Collections are Immutable (Persistent)

Clojure collections are *immutable* (*persistent*). The term *persistent data structures* has
nothing to do with durably storing them on disk. What it means is that collections are
mutated (updated) by producing new collections. To quote Wikipedia:

> In computing, a persistent data structure is a data structure that always preserves
> the previous version of itself when it is modified. Such data structures are effectively
> immutable, as their operations do not (visibly) update the structure in-place, but instead
> always yield a new updated structure.

Clojure's persistent data structures are implemented as trees and tries and
have `O(log32 n)` access complexity where `n` is the number of elements.


## The Collection Abstraction

Clojure has a collection abstraction with several key operations supported for
all collection implementations. They are

 * `=`: checks value equality of a collection compared to other collections
 * `count`: returns number of elements in a collection
 * `conj`: adds an item to a collection in the most efficient way
 * `empty`: returns an empty collection of the same type as the argument
 * `seq`: gets a sequence of a collection

These functions work on all core Clojure collection types.


## Core Collection Types

Clojure has several core collection types:

 * Maps (called hashes or dictionaries in some other languages)
 * Vectors
 * Lists
 * Sets

### Maps

Maps associate keys with values. Boths keys and values can be of any type, but
keys must be comparable. There are several implementations of maps with
different guarantees about ordering. Hash maps are typically instantiated with literals:

``` clojure
{:language "Clojure" :creator "Rich Hickey"}
```

Commas can be used in map literals (Clojure compiler treats the as whitespace):

``` clojure
{:language "Clojure", :creator "Rich Hickey"}
```

`clojure.core/sorted-map` and `clojure.core/array-map` produce ordered maps:

``` clojure
(sorted-map :language "Clojure" :creator "Rich Hickey") ;= {:creator "Rich Hickey", :language "Clojure"}

(array-map :language "Clojure" :creator "Rich Hickey")  ;= {:creator "Rich Hickey", :language "Clojure"}
```

Unsurprisingly, map literals must contain an even number of forms (as many keys as values). Otherwise
the code will not compile:

``` clojure
;; fails with java.lang.RuntimeException: Map literal must contain an even number of forms
{:language "Clojure" :creator}
```

In general, the only major difference between Clojure maps and maps/hashes/dictionaries in some other languages
is that Clojure maps are *immutable*. When a Clojure map is modified, the result is a new map that internally
has structural sharing (for efficiency reasons) but semantically is a separate immutable value.


#### Maps As Functions

Maps in Clojure can be used as functions on their keys. See the [Functions guide](http://localhost:4000/articles/language/functions.html#maps_as_functions)
for more information.

#### Keywords As Functions

Keywords in Clojure can be used as functions on maps. See the [Functions guide](http://localhost:4000/articles/language/functions.html#keywords_as_functions)
for more information.


### Vectors

Vectors are collections that offer efficient random access (by index). They are typically instantiated with
literals:

``` clojure
[1 2 3 4]

["clojure" "scala" "erlang" "f#" "haskell" "ocaml"]
```

Commas can be used to separate vector elements (Clojure compiler treats the as whitespace):

``` clojure
["clojure", "scala", "erlang", "f#", "haskell", "ocaml"]
```

TBD


### Lists

Lists in Clojure are singly linked lists. Access or modifications of list head is efficient, random access
is not.

Lists in Clojure are special because they represent code forms, from function calls to macro calls to special forms.
Code is data in Clojure and it is represented primarily as lists:

``` clojure
(empty? [])
```

First item on the list is said to be in the *calling position*.

When used as "just" data structures, lists are typically instantiated with literals with quoting:

``` clojure
'(1 2 3 4)

'("clojure" "scala" "erlang" "f#" "haskell" "ocaml")
```

Commas can be used to separate list elements (Clojure compiler treats the as whitespace):

``` clojure
'("clojure", "scala", "erlang", "f#", "haskell", "ocaml")
```

TBD


### Sets

Sets are collections that offer efficient membership check operation and only allow each element to appear in the collection
once. They are typically instantiated with literals:

``` clojure
#{1 2 3 4}

#{"clojure" "scala" "erlang" "f#" "haskell" "ocaml"}
```

Commas can be used to separate set elements (Clojure compiler treats the as whitespace):

``` clojure
#{"clojure", "scala", "erlang", "f#", "haskell", "ocaml"}
```

TBD

#### Sets As Functions

Sets in Clojure can be used as functions on their keys. See the [Functions guide](http://localhost:4000/articles/language/functions.html#sets_as_functions)
for more information.



## Sequences

The sequence abstraction represents a sequential view of a collection or collection-like
entity (computation result).

`clojure.core/seq` is a function that produces a sequence over the given argument.
Data types that `clojure.core/seq` can produce a sequence over are called *seqable*:

 * Clojure collections
 * Java maps
 * All iterable types (types that implement `java.util.Iterable`)
 * Java collections (`java.util.Set`, `java.util.List`, etc)
 * Java arrays
 * All types that implement `java.lang.CharSequence` interface, including Java strings
 * All types that implement `clojure.lang.Seqable` interface
 * nil


TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Key Operations on Collections and Sequences

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## The Seq Abstraction

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Transients

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Custom Collections and Sequences

It is possible to develop custom collection types in Clojure or Java and have
`clojure.core` functions work on them just like they do on builtin types.

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Wrapping Up

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
