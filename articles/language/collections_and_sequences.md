---
title: "Collections and Sequences in Clojure"
layout: article
---

This guide covers:

 * Collections in Clojure
 * Sequences in Clojure
 * Core collection types
 * Key operations on collections and sequences
 * Other topics related to collections and sequences

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/guides).

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.5.


## Overview

Clojure provides a number of powerful abstractions including *collections* and *sequences*.
When working with Clojure,
many operations are expressed as a series of operations on collections or sequences.

Most of Clojure's core library treats collections and sequences the same way, although
sometimes a distinction has to be made (e.g. with lazy infinite sequences).

`clojure.core` provides many fundamental operations on collections, such as: `map`, `filter`,
`remove`, `take`, and `drop`. Basic operations on collections and sequences are combined
 to implement more complex operations.

### Clojure Collections are Immutable (Persistent)

Clojure collections are *immutable* (*persistent*). The term "persistent data structures" has
nothing to do with durably storing them on disk. What it means is that collections are
mutated (updated) by producing new collections. To quote Wikipedia:

> In computing, a persistent data structure is a data structure that always preserves
> the previous version of itself when it is modified. Such data structures are effectively
> immutable, as their operations do not (visibly) update the structure in-place, but instead
> always yield a new updated structure.

Clojure's persistent data structures are implemented as trees and [*tries*](https://en.wikipedia.org/wiki/Hash_array_mapped_trie) and
have O(log<sub>32</sub> *n*) access complexity where *n* is the number of elements.


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

Commas can be used in map literals (Clojure compiler treats the comma as whitespace):

``` clojure
{:language "Clojure", :creator "Rich Hickey"}
```

`clojure.core/sorted-map` and `clojure.core/array-map` produce ordered maps:

``` clojure
(sorted-map :language "Clojure" :creator "Rich Hickey") ; ⇒ {:creator "Rich Hickey", :language "Clojure"}

(array-map :language "Clojure" :creator "Rich Hickey")  ; ⇒ {:creator "Rich Hickey", :language "Clojure"}
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

Maps in Clojure can be used as functions on their keys. See the [Functions guide](/articles/language/functions.html#maps_as_functions)
for more information.

#### Keywords As Functions

Keywords in Clojure can be used as functions on maps. See the [Functions guide](/articles/language/functions.html#keywords_as_functions)
for more information.


### Vectors

Vectors are collections that offer efficient random access (by index). They are typically instantiated with
literals:

``` clojure
[1 2 3 4]

["clojure" "scala" "erlang" "f#" "haskell" "ocaml"]
```

Commas can be used to separate vector elements (Clojure compiler treats
the comma as whitespace):

``` clojure
["clojure", "scala", "erlang", "f#", "haskell", "ocaml"]
```

Unlike lists, vectors are not used for function invocation. They are, however, used to make certain
forms (e.g. the list of locals in `let` or parameters in `defn`) stand out visually. This was
an intentional decision in Clojure design.


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

Or you can explicitly use the `list` form:

```clojure
(list 1 2 3 4)
;; ⇒ (1 2 3 4)
```

Commas can be used to separate list elements (Clojure compiler treats
the comma as whitespace):

``` clojure
'("clojure", "scala", "erlang", "f#", "haskell", "ocaml")
```

#### Lists and Metaprogramming in Clojure

Metaprogramming in Clojure (and other Lisp dialects) is different from metaprogramming in, say, Ruby, because
in Ruby metaprogramming is *primarily* about producing strings while in Clojure it is about producing
*data structures* (mostly *lists*). For sophisticated DSLs, producing data structures directly lets
developers avoid a lot of incidental complexity that string generation brings along.

This topic is covered in detail in the [Macros and Metaprogramming](/articles/language/macros.html).


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

#### Sets As Functions

Sets in Clojure can be used as functions on their elements. See the [Functions guide](/articles/language/functions.html#sets_as_functions)
for more information.


#### Set Membership Checks

The most common way of checking if an element is in a set is by using set as a function:

``` clojure
(#{1 2 3 4} 1)  ; ⇒ 1
(#{1 2 3 4} 10) ; ⇒ nil

(if (#{1 2 3 4} 1)
  :hit
  :miss) ; ⇒ :hit
```



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


The sequence abstraction supports several operations:

 * `first`
 * `rest`
 * `next`

and there are two ways to produce a sequence:

 * `seq` produces a sequence over its argument (often a collection)
 * `lazy-seq` creates a *lazy sequence* (that is produced by performing computation)

### seq, cons, list*

`clojure.core/seq` takes a single argument and returns a sequential view over it:

```clojure
(seq [1 2 3])
;; ⇒ (1 2 3)
```

When given an empty collection or sequence, `clojure.core/seq` returns nil:

```clojure
(seq [])
;; ⇒ nil
```

this is commonly used in the following pattern:

```clojure
(if (seq xs)
  (comment "Do something with this sequence")
  (comment "Do something else"))
```

Another function that constructs sequences is `clojure.core/cons`. It prepends values to the head of
the given sequence:

``` clojure
(cons 0 (range 1 3))
;; ⇒ (0 1 2)
```

`clojure.core/list*` does the same for a number of values:

``` clojure
(list* 0 1 (range 2 5))
;; ⇒ (0 1 2 3 4)
```

`clojure.core/cons` and `clojure.core/list*` are primarily used to produce lazy sequences and in metaprogramming (when writing
macros). As far as metaprogramming goes, sequences and lists are the same and it is common to
add items in the beginning of the list (into the *calling position*).

Note that `clojure.core/cons` does not create cons cells and lists in Clojure are not implemented
as linked cons cells (like in many other dialects of Lisp).


### first, rest, next

`clojure.core/first` returns the first item in the sequence. `clojure.core/next` and `clojure.core/rest`
return the rest:

``` clojure
(first (seq [1 2 3 4 5 6]))
;; ⇒ 1

(rest (seq [1 2 3 4 5 6]))
;; ⇒ (2 3 4 5 6)
```

the difference between them is what they return on a single element sequence:

``` clojure
(rest (seq [:one]))
;; ⇒ ()
(next (seq [:one]))
;; ⇒ nil
```




### Lazy Sequences in Clojure

*Lazy sequences* are produced by performing computation or I/O. They can be infinite
or not have exact length (e.g. a sequence of all powers of 2 or an audio stream).

Lazy sequences is an broad topic and covered in the [Laziness](/articles/language/laziness.html) guide.



## Key Operations on Collections and Sequences

Below is an overview of `clojure.core` functions that work on collections and sequences. Most of them
work the same way for all types of collections, however, there are exception to this rule. For example,
functions like `clojure.core/assoc`, `clojure.core/dissoc` and `clojure.core/get-in` only really
make sense in the context of maps and other associative data structures (for example, records).

`clojure.core/conj` adds elements to a collection in the most efficient manner, which depends on
collection implementation details and won't be the same for vectors and lists.

In general, Clojure design emphasizes that operations on collections and sequences should be uniform and
follow the principle of least surprise. In real world projects, however, the difference between
algorithmic complexity and other runtime characteristics of various collection types often cannot
be ignored. Keep this in mind.

You can find more information in the [clojure.core Overview](/articles/language/core_overview.html) and [Clojure cheatsheet](http://clojure.org/cheatsheet).


### count

Returns a count of the number of items in a collection. An argument of nil returns 0.

``` clojure
(count "Hello")
;; ⇒ 5

(count [1 2 3 4 5 6 7])
;; ⇒ 7

(count nil)
;; ⇒ 0
```

Note that count does not return in constant time for all collections. This can be determined with `counted?`.
Keep in mind that lazy sequences must be realized to get a count of the items. This is often not intended and
can cause a variety of otherwise cryptic errors.

``` clojure
(counted? "Hello")
;; ⇒ false

;; will be fully realized when using (count (range 10))
(counted? (range 10))
;; ⇒ false

;; Constant time return of (count)
(counted? [1 2 3 4 5])
;; ⇒ true
```

### conj

`conj` is short for "conjoin". As the name implies, `conj` takes a collection and argument(s) and returns the collection with those arguments added.

Adding items to a collection occurs at different places depending on the concrete type of collection.

List addition occurs at the beginning of the list. This is because accessing the head of the list is a constant time operation, and accessing
the tail requires traversal of the entire list.

```clojure
(conj '(1 2) 3)
;; ⇒ (3 1 2)
```

Vectors have constant time access across the entire data structure. `'conj' thusly appends to the end of a vector.

```clojure
(conj [1 2] 3)
;; ⇒ [1 2 3]
```

Maps do not have guaranteed ordering, so the location that items are added is irrelevant. `conj` requires vectors of [key value] pairs to be
added to the map.

```clojure
(conj {:a 1 :b 2 :c 3} [:d 4])
;; ⇒ {:d 4, :a 1, :c 3, :b 2}

(conj {:cats 1 :dogs 2} [:ants 400] [:giraffes 13])
;; ⇒ {:giraffes 13, :ants 400, :cats 1, :dogs 2}
```

Sets also do not have guaranteed ordering. `conj` returns a set with the item added. As the concept of sets implies, added items will not duplicate equivalent items if they are present in the set.

```clojure
(conj #{1 4} 5)
;; ⇒ #{1 4 5}

(conj #{:a :b :c} :b :c :d :e)
;; ⇒ #{:a :c :b :d :e}
```

### get

`get` returns the value for the specified key in a map or record, index of a vector or value in a set. If the key is not present,
`get` returns nil or a supplied default value.

```clojure
;; val of a key in a map
(get {:a 1 :b 2 :c 3} :b)
;; ⇒ 2

;; index of a vector
(get [10 15 20 25] 2)
;; ⇒ 20

;; in a set, returns the value itself if present
(get #{1 10 100 2 20 200} 1)
;; ⇒ 1

;; returns nil if key is not present
(get {:a 1 :b 2} :c)
;; ⇒ nil

;; vector does not have an _index_ of 4. nil is returned
(get [1 2 3 4] 4)
;; ⇒ nil

(defrecord Hand [index middle ring pinky thumb])
(get (Hand. 3 4 3.5 2 2) :index)
;; ⇒ 3
```

`get` also supports a default return value supplied as the last argument.

```clojure
;; index 4 does not exist. return default value
(get [1 2 3 4] 4 "Not Found")
;; ⇒ "Not Found"

;; key :c does not exist, so return default value of 3
(get {:a 1 :b 2} :c 3)
;; ⇒ 3
```

### assoc

`assoc` takes a key and a value and returns a collection of the same type as the supplied collection with the key mapped to the new value.

`assoc` is similar to `get` in how it works with maps, records or vectors. When applied to a map or record, the same type is returned with the key/value pairs added or modified.  When applied to a vector, a vector is returned with the key acting as an index and the index being replaced by the value.

Since maps and records can not contain multiple equivalent keys, supplying `assoc` with a key/value that exists in the one will cause `assoc` to return modify the key at that value in the result and not duplicate the key.

```clojure
(assoc {:a 1} :b 2)
;; ⇒ {:b 2, :a 1}

(assoc {:a 1 :b 45 :c 3} :b 2)
;; ⇒ {:a 1, :c 3, :b 2}

(defrecord Hand [index middle ring pinky thumb])
(assoc (Hand. 3 4 3.5 2 2) :index 3.75)
;; ⇒ #user.Hand{:index 3.75, :middle 4, :ring 3.5, :pinky 2, :thumb 2}
```
When using `assoc` with a vector, the key is the index and the value is the value to assign to that index in the returned vector.
The key must be <= (count vector) or a "IndexOutOfBoundsException" will occur. 

```clojure
(assoc [1 2 76] 2 3) ; ⇒ [1 2 3]

;; index 5 does not exist. valid indexes for this vector are: 0, 1, 2
(assoc [1 2 3] 5 6) ;; IndexOutOfBoundsException   clojure.lang.PersistentVector.assocN (PersistentVector.java:136)
```
When the key is equal to (count vector) `assoc` will add an item to the vector.

```clojure
(assoc [1 2 3] 3 4) ; ⇒ [1 2 3 4]
```

### dissoc

`dissoc` returns a map with the supplied keys, and subsequently their values, removed. Unlike `assoc`, `dissoc` does not work on vectors. When a record is provided, `dissoc` returns a map. For similar functionality with vectors, see `subvec` and `concat`.

```clojure
(dissoc {:a 1 :b 2 :c 3} :b)
;; ⇒ {:a 1, :c 3}

(dissoc {:a 1 :b 14 :c 390 :d 75 :e 2 :f 51} :b :c :e)
;; ⇒ {:a 1, :f 51, :d 75}

;; note that a map is returned, not a record.
(defrecord Hand [index middle ring pinky ring])
;; always be careful with the bandsaw!
(dissoc (Hand. 3 4 3.5 2 2) :ring)
;; ⇒ {:index 3, :middle 4, :pinky 2, :thumb 2}
```

### first

`first` returns the first item in the collection. `first` returns nil if the argument is empty or is nil.

Note that for collections that do not guarantee order like some maps and sets, the behaviour of `first` should not be relied on.

```clojure
(first (range 10))
;; ⇒ 0

(first [:floor :piano :seagull])
;; ⇒ :floor

(first [])
;; ⇒ nil
```

### rest

`rest` returns a seq of items starting with the second element in the collection. `rest` returns an empty seq if the collection only contains a single item.

`rest` should also not be relied on when using maps and sets unless you are sure ordering is guaranteed.

```clojure
(rest [13 1 16 -4])
;; ⇒ (1 16 -4)

(rest '(:french-fry))
;; ⇒ '()
```

The behaviour of `rest` should be contrasted with `next`. `next` returns nil if the collection only has a single item. This is important when considering "truthiness" of values since an empty seq is "true" but nil is not.

```clojure
(if (rest '("stuff"))
  (print "Does this print?")) ;; yes, it prints.


;; NEVER FINISHES EXECUTION!!!
;; "done" is never reached because (rest x) is always a "true" value
(defn inf
  [x]
  (if (rest x)
    (inf (rest x))
    "done"))
```

### empty?

`empty?` returns true if the collection has no items, or false if it has 1 or more items.

```clojure
(empty? [])
;; ⇒ true

(empty? '(1 2 3))
;; ⇒ false
```

Do not confuse `empty?` with `empty`. This can be a source of great confusion:

```clojure
(if (empty [1 2 3]) ;; empty returns an empty seq, which is true! use empty? here.
  "It's empty"
  "It's not empty")
;; ⇒ "It's empty"
```

### empty

`empty` returns an empty collection of the same type as the collection provided.

```clojure
(empty [1 2 3])
;; ⇒ []

(empty {:a 1 :b 2 :c 3})
;; ⇒ {}
```

### not-empty

`not-empty` returns nil if the collection has no items. If the collection contains items, the collection is returned.

```clojure
(not-empty '(:mice :elephants :children))
;; ⇒ (:mice :elephants :children)

(not-empty '())
;; ⇒ nil
```

### contains?

`contains` returns true if the provided *key* is present in a collection. `contains` is similar to `get` in that vectors treat the key as an index. `contains` will always return false for lists.

```clojure
(contains? {:a 1 :b 2 :c 3} :c)
;; ⇒ true

;; true if index 2 exists
(contains? ["John" "Mary" "Paul"] 2)
;; ⇒ true

;; false if index 5 does not exist
(contains? ["John" "Mary" "Paul"] 5)
;; ⇒ false

;; "Paul" does not exist as an index
(contains? ["John" "Mary" "Paul"] "Paul")
;; ⇒ false

;; lists always return false. Contain won't traverse a collection for a result.
(contains? '(1 2 3) 0)
;; ⇒ false
```

### some

`some` will apply a predicate to each value in a collection until a non-false/nil result is returned then immediately return that result.

Since collections are "true" values, this makes it possible to return the first result itself rather than simply `true`.

```clojure
(some even? [1 2 3 4 5])
;; ⇒ true

;; predicate returns the value rather than simply true
(some #(if (even? %) %) [1 2 3 4 5])
;; ⇒ 2
```

Since maps can be used as functions, you can use a map as a predicate. This will return the value of the first key in the collection that is also in the map.

```clojure
(some {:a 1 :b 5} [:h :k :d :b])
;; ⇒ 5
```

Sets can also be used as functions and will return the first item in the collection that is present in the set.

```clojure
(some #{4} (range 20))
;; ⇒ 4
```

### every?

`every` returns true if the predicate returns true for every item in the collection, otherwise it returns false.

```clojure
(every? even? (range 0 10 2))
;; ⇒ true

;; set can be used to see if collection only contains items in the set.
(every? #{2 3 4} [2 3 4 2 3 4])
;; ⇒ true
```

### map

`map` is used to sequence of values and generate a new sequence of
values.

Essentially, you're creating a *mapping* from an old sequence of values
to a new sequence of values.

```clojure
(def numbers
  (range 1 10))
;; ⇒ (1 2 3 4 5 6 7 8 9)

(map (partial * 2) numbers)
;; ⇒ (2 4 6 8 10 12 14 16 18)

(def scores
  {:clojure 10
   :scala 9
   :jruby 8})

(map #(str "Team " (name (key %)) " has scored " (val %)) scores)
;; ⇒ ("Team scala has scored 9" "Team jruby has scored 8" "Team clojure has scored 10")
```

### reduce

`reduce` takes a sequence of values and a function. It applies that
function repeatedly with the sequence of values to *reduce* it to a
single value.

```clojure
(def numbers
  (range 1 10))
;; ⇒ (1 2 3 4 5 6 7 8 9)

(reduce + numbers)
;; ⇒ 45

(def scores
  {:clojure 10
   :scala 9
   :jruby 8})

(reduce + (vals scores))
;; ⇒ 27

;; Provide an initial value for the calculation
(reduce + 10 (vals scores))
;; ⇒ 37
```

### filter

`filter` returns a lazy sequence of items that return `true` for the provided predicate. Contrast to `remove`.

```clojure
(filter even? (range 10))
;; ⇒ (0 2 4 6 8)

(filter #(if (< (count %) 5) %) ["Paul" "Celery" "Computer" "Rudd" "Tayne"])
;; ⇒ ("Paul" "Rudd")
```

When using sets with `filter`, remember that if nil or false is in the set and in the collection, then the predicate will return itself: `nil`.

In this example, when nil and false are tested with the predicate, the predicate returns nil. This is because if the item is present in the set it is returned. This will cause that item to /not/ be included in the returned lazy-sequence.

```clojure
(filter #{:nothing :something nil} [:nothing :something :things :someone nil false :pigeons])
;; ⇒ (:nothing :something)
```

### remove

`remove` returns a lazy sequence of items that return `false` or `nil` for the provided predicate. Contrast to `filter`.

```clojure
(remove even? (range 10))
;; ⇒ (1 3 5 7 9)

;; relative complement. probably useless?
(remove {:a 1 :b 2} [:h :k :z :b :s])
;; ⇒ (:h :k :z :s)
```

When using sets with `remove`, remember that if nil or false is in the set and in the collection, then the predicate will return itself: `nil`.
This will cause that item to be included in the returned lazy sequence.

In this example, when nil and false are tested with the predicate, the predicate returns nil. This is because if the item is present in the set it is returned.

```clojure
(remove #{:nothing :something nil} [:nothing :something :things :someone nil false :pigeons])
;; ⇒ (:things :someone nil false :pigeons)
```

### iterate

`iterate` takes a function and an initial value, returns the result of
applying the function on that initial value, then applies the function
again on the resultant value, and repeats forever, lazily. Note that the
function *iterates* on the value.

```clojure
(take 5 (iterate inc 1))
;; ⇒ (1 2 3 4 5)

(defn multiply-by-two
  [value]
  (* 2 value))

(take 10 (iterate multiply-by-two 1))
;; ⇒ (1 2 4 8 16 32 64 128 256 512)
```

### get-in

`get-in` is used to *get* a value that is deep *inside* a data
structure.

You have to provide the data structure and a sequence of keys, where a
key is valid at each subsequent level of the nested data structure.

If the sequence of keys does not lead to a valid path, `nil` is
returned.

```clojure
(def family
  {:dad {:shirt 5
         :pants 6
         :shoes 4}
   :mom {:dress {:work 6
                 :casual 7}
         :book 3}
   :son {:toy 5
         :homework 1}})

(get-in family [:dad :shirt])
;; ⇒ 5

(get-in family [:mom :dress])
;; ⇒ {:work 6, :casual 7}

(get-in family [:mom :dress :casual])
;; ⇒ 7

(get-in family [:son :pants])
;; ⇒ nil

(def locations
  [:office :home :school])

(get-in locations [1])
;; ⇒ :home
```

### update-in

`update-in` is used to *update* a value deep inside a structure
*in-place*.

Note that since data structures are immutable, it only returns a
"modified" data structure, it does not actually alter the original
reference.

The "update" function takes the old value and returns a new value which
`update-in` uses in the new modified data structure.

```clojure
(def family
  {:dad {:shirt 5
         :pants 6
         :shoes 4}
   :mom {:dress {:work 6
                 :casual 7}
         :book 3}
   :son {:toy 5
         :homework 1}})

(update-in family [:dad :pants] inc)
;; ⇒ {:son {:toy 5, :homework 1}, :mom {:dress {:work 6, :casual 7}, :book 3}, :dad {:shoes 4, :shirt 5, :pants 7}}
;; Notice that "pants" gets incremented

(def locations
  [:office :home :school])

(update-in locations [2] #(keyword (str "high-" (name %))))
;; ⇒ [:office :home :high-school]
```

### assoc-in

`assoc-in` is used to *associate* a new value deep inside a structure
*in-place*.

Note that since data structures are immutable, it only returns a
"modified" data structure, it does not actually alter the original
reference.

Note the difference between `update-in` and `assoc-in`: `update-in`
takes a function that applies on the old value to return a new value,
whereas `assoc-in` takes a new value as-is.

```clojure
(def family
  {:dad {:shirt 5
         :pants 6
         :shoes 4}
   :mom {:dress {:work 6
                 :casual 7}
         :book 3}
   :son {:toy 5
         :homework 1}})

(assoc-in family [:son :crayon] 3)
;; ⇒ {:son {:toy 5, :crayon 3, :homework 1}, :mom {:dress {:work 6, :casual 7}, :book 3}, :dad {:shoes 4, :shirt 5, :pants 6}}

(def locations
  [:office :home :school])

(assoc-in locations [3] :high-school)
;; ⇒ [:office :home :school :high-school]
```

### keys

`keys` returns a sequence of the keys in a map or record.

```clojure
(keys {1 "one" 2 "two" 3 "three"})
;; ⇒ (1 2 3)

(defrecord Hand [index middle ring pinky thumb])
(keys (Hand. 2 4 3 1 2))
;; ⇒ (:index :middle :ring :pinky :thumb)
```

### vals

`vals` returns a sequence of vals in a map or record.

```clojure
(vals {:meows 20 :barks 2 :moos 5})
;; ⇒ (5 2 20)

(defrecord Hand [index middle ring pinky thumb])
(vals (Hand. 1 2 3 4 5))
;; ⇒ (1 2 3 4 5)
```

### select-keys

`select-keys` is used to extract a subset of a map:

```clojure
(def family
  {:dad {:shirt 5
         :pant 6
         :shoes 4}
   :mom {:dress {:work 6
                 :casual 7}
         :book 3}
   :son {:toy 5
         :homework 1}})

(select-keys family [:dad])
;; ⇒ {:dad {:shoes 4, :shirt 5, :pant 6}}

(select-keys family [:mom :son])
;; ⇒ {:son {:toy 5, :homework 1}, :mom {:dress {:work 6, :casual 7}, :book 3}}
```

### take

`take` returns a lazy sequence of the first `n` items of a collection `coll`.

```clojure
(take 3 [1 3 5 7 9])
;; ⇒ (1 3 5)
(type (take 3 (range)))
;; ⇒ clojure.lang.LazySeq
```
If there are fewer than `n` items in `coll`, all items will be returned.

```clojure
(take 5 [1 2 3])
;; ⇒ (1 2 3)
(take 3 nil)
;; ⇒ ()
```

### drop

`drop` drops `n` items from a collection `coll` and returns a lazy sequence of the rest of it.

```clojure
(drop 3 '(0 1 2 3 4 5 6))
;; ⇒ (3 4 5 6)
(drop 2 [1 2])
;; ⇒ ()
(drop 2 nil)
;; ⇒ ()
```

### take-while

`take-while` returns a lazy sequence of items from a collection as long
as the predicate returns `true` for each item:

```clojure
(take-while #(< % 5) (range))
;; ⇒ (0 1 2 3 4)
```

### drop-while

`drop-while` drops items from a collection as long as the predicate
returns `false` for the item and when the first non-false item is found,
it returns a lazy sequence from that item onwards:

```clojure
(drop-while #(< % 5) (range 10))
;; ⇒ (5 6 7 8 9)
```

## Transients

Clojure data structures are immutable, they do not change. Mutating them produces
a new data structure that internally has structural sharing with the original
one. This makes a whole class of concurrency hazards go away but has some
performance penalty and additional GC pressure.

For cases when raw performance for a piece of code is more important than safety,
Clojure provides mutable versions of vectors and unsorted maps. They are known
as *transients* and should only be used for locals and as an optimization
technique after profiling.

Transients are produced from immutable data structures using the `clojure.core/transient`
function:

``` clojure
(let [m (transient {})]
  (assoc! m :key "value") ;; mutates the transient in place!
  (count m)) ;; ⇒ 1
```

Note that `clojure.core/transient` does not affect nested collections, for
example, values in a map of keywords to vectors.

To mutate transients, use `clojure.core/assoc!`, `clojure.core/dissoc!` and
`clojure.core/conj!`. The exclamation point at the end hints that these
functions work on transients and modify data structures in place, which
is not safe of data structures are shared between threads.

To create an immutable data structure out of a transient, use `clojure.core/persistent!`:

``` clojure
(let [m (transient {})]
        (assoc! m :key "value")
        (persistent! m)) ;; ⇒ {:key "value"}
```

In conclusion: use transients only as an optimization technique and only
after profiling and identifying hot spots in your code. Guessing is the
shortest way we know to blowing the performance.


## Custom Collections and Sequences

It is possible to develop custom collection types in Clojure or Java and have
`clojure.core` functions work on them just like they do on builtin types.

TBD: [How to Contribute](https://github.com/clojuredocs/guides#how-to-contribute)


## Wrapping Up

When working with Clojure, it is common to operate and transform collections and sequences.
Clojure's core library unify operations on collections and sequences where possible.
This extends to Java collections, arrays and iterable objects for seamless interoperability.

Most of the time, whenever you need a function that transforms sequences, chances are, there is
one already that does that in `clojure.core` or you can compose more than one `clojure.core` function
to achieve the same result.


## Contributors

Michael Klishin <michael@defprotocol.org>
Robert Randolph <audiolabs@gmail.com>
satoru <satorulogic@gmail.com>
