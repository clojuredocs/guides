---
title: "Overview of clojure.core"
layout: article
---

## About this guide

This guide covers:

 * Key functions of `clojure.core`
 * Key macros of `clojure.core`
 * Key vars of `clojure.core`
 * Essential special forms

This guide is **by no means comprehensive** and does not try to explain each function/macro/form in depth. It is an overview,
the goal is to briefly explain the purpose of each item and provide links to other articles with more information.

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.


## Fundamentals

### let

`let` allows binding of locals and defines an explicit scope for those bindings. The bindings are defined as a vector of [symbol value] pairs.

The body of a `let` statement also provides an implicit `do` that allows for multiple statements in the body of `let`.

A basic example:

``` clojure
(let [x 1 y 2] (println x y)) ;; 1 2
```

Let can be nested, and the scope is lexically determined. This means that a binding's value is determined by the nearest binding form for that symbol.

This example basically demonstrates the lexical scoping of the let form.

``` clojure
(let [x 1]
  (println x) ;; prints 1
  (let [x 2]
    (println x))) ;; prints 2
```

Let bindings are immutable and can be destructured.

``` clojure
todo - link to destructuring
```

### def

`def` takes a symbol and an optional init value. If an init value is supplied, the root binding of the var is assigned to that value. Redefining a var with an init value will re-assign the root binding. 

A root binding is a value that is shared across all threads.

The `let` form is the preferred method of creating local bindings. It is strongly suggested to prefer it where possible, and never use `def` within another form.


``` clojure
;; todo - reference to var documentation, basic example
;; todo - metadata
```

### declare

`declare` provides a simple way of creating 'forward declarations'. `declare` defs the supplied symbols with no init values. This allows for referencing of a var before it has been supplied a value.

There are much better methods of value-based dispatch or code architecture in general, but this presents a simple situation forward declarations would be necessary.

``` clojure
(declare func<10 func<20)

;; without declare you will receive an error similar to:
;; "Unable to resolve symbol: func10 in this context"

(defn func<10 [x]
  (cond
   (< x 10) (func10 (inc x))
   (< x 20) (func20 x)
   :else "too far!"))

(defn func<20 [x]
  (cond
   (< x 10) (func10 x)
   (< x 20) "More than 10, less than 20"
   :else "too far!"))
```

No matter which order you put func<10 and func<20 in, there will be a reference to a var that does not yet exist when the compiler does the initial evaluation of top-level forms.

`declare` defines the var with no binding so that the the var exists when it is referenced later in the code.

### defn

`defn` allows for succinct definition of a function and metadata about its argslist and doc-string. `defn` inherently allows for quick documentation of functions that can be retrieved with `doc`. This feature should be used almost universally.

Without `defn`, a var would be directly bound to a function definition and explicit metadata about the doc string and argslits would be added manually.

``` clojure
(def func (fn [x] x))

;; same as:
(defn func [x] x)

;; with metadata added by defn
(def ^{:doc "documentation!"} ^{:arglists '([x])} func (fn [x] x))

;;same as
(defn func "documentation!" [x] x)
```

``` clojure
;; todo - link to doc and metadata
```

### ns

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### if

`if` is the primary method of conditional execution and other conditionals are built upon `if`.

`if` is an expression that takes 2 expressions, and an optional third.  If the return value of the first expression is anything except nil or false, the second expression is evaluated and the result returned..

If a third expression is provided, when the first expression returns nil or false the third expression is evaluated and returned.


``` clojure
user=> (if 0 "second") ;; 0 is a 'true' value. Only false or nil are 'false'
"second"

user=> (if nil "second" "third")
"third"

user=> (if (< 10 9) "second" "third") ;; (< 9 10) returns false
"third"

user=> (if (seq '()) "second") ;; seq returns nil for an empty sequence
nil

user=> (if (nil? (= 1 2)) "second" "third") ;; differentiate between nil and false if needed
"third"
```

### when

`when` provides an implicit do form that is evaluated if an expression returns true, otherwise nil is returned. `when` does not provide an 'else'.

``` clojure
user=> (when (= 1 2) (print "hey") 10)
nil

user=> (when (< 10 11) (print "hey") 10)
hey
10
```

### for

`for` allows for list comprehensions. `for` takes a vector of pairs of [binding collection]. `for` then assigns each sequential value in the collection to the binding form and evaluates them rightmost first. The results are returned in a lazy sequence.

`for` allows for explicit let, when and while through use of ":let []" ":when (expression)" ":while (expression)" in the binding vector.

``` clojure
(for [x [1 2 3] y [4 5 6]] 
  [x y])
  
;; ([1 4] [1 5] [1 6] [2 4] [2 5] [2 6] [3 4] [3 5] [3 6])
```

:when only evaluates the body when a true value is returned by the expression provided

``` clojure
(for [x [1 2 3] y [4 5 6]
      :when (and
             (even? x)
             (odd? y))]
  [x y])
  
;; ([2 5])
```

:while evaluates the body until a non-true value is reached. Note that the rightmost collection is fully bound to y before a non-true value of (< x 2) is reached. This demonstrates the order of the comprehension.

``` clojure
(for [x [1 2 3] y [4 5 6]
      :while (< x 2)]
  [x y])
  
;; ([1 4] [1 5] [1 6])
```

### doseq

`doseq` is similar to `for` except it does not return a sequence of results. `doseq` is generally intended for execution of side-effects in the body, and thusly returns nil.

`doseq` supports the same bindings as for - :let :when :while. For examples of these, see for.

``` clojure
(doseq [x [1 2 3] y [4 5 6]]
  (println [x y]))
  
;; [1 4][1 5][1 6][2 4][2 5][2 6][3 4][3 5][3 6]
;; nil
```

### apply

`apply` effectively unrolls the supplied args and a collection into a list of arguments to the supplied function.

``` clojure
(str ["Hel" "lo"])
"[\"Hel\" \"lo\"]" ;; not what we want, str is operating on the vector

user> (apply str ["Hel" "lo"]) ;; same as (str "Hel" "lo")
"Hello"
```

`apply` prepends any supplied arguments to the form as well.

``` clojure
(map + [[1 2 3] [1 2 3]]) ;; This attempts to add 2 vectors with +
;; ClassCastException   java.lang.Class.cast (Class.java:2990)

(apply map + [[1 2 3] [1 2 3]]) ;; same as (map + [1 2 3] [1 2 3])
;; (2 4 6)

(apply + 1 2 3 [4 5 6]) ;; same as  (+ 1 2 3 4 5 6)
;; 21
```

Note that apply can not be used with macros.

### require

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### import

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### loop, recur

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)



## Sequences

### count

Returns a count of the number of items in a collection. An argument of nil returns 0.

``` clojure
(count "Hello")
;; 5

(count [1 2 3 4 5 6 7])
;; 7
```

Note that count does not return in constant time for all collections. This can be determined with `counted?`. Keep in mind that zazy sequences must be realized to get a count of the items. This is often not intended and can cause a variety of otherwise cryptic errors.

``` clojure
(counted? "Hello")
;; false

(counted? (range 10) ;; will be fully realized when using (count (range 10))
;; false

(counted? [1 2 3 4 5]) ;; Constant time return of (count)
;; true 
```

### conj

`conj` is short for "conjoin". As the name implies, `conj` takes a collection and argument(s) and returns the collection with those arguments added.

Adding items to a collection occurs at different places depending on the concrete type of collection.

List addition occurs at the beginning of the list. This is because accessing the head of the list is a constant time operation, and accessing the tail requires traversal of the entire list.

```clojure
(conj '(1 2) 3)
;; (3 1 2)
```

Vectors have constant time access across the entire data structure. `'conj' thusly appends to the end of a vector.

```clojure
(conj [1 2] 3)
;; [1 2 3]
```

Maps do not have guaranteed ordering, so the location that items are added is irrelevant. `conj` requires vectors of [key value] pairs to be added to the map.

```clojure
(conj {:a 1 :b 2 :c 3} [:d 4])
;; {:d 4, :a 1, :c 3, :b 2}

(conj {:cats 1 :dogs 2} [:ants 400] [:giraffes 13])
;; {:giraffes 13, :ants 400, :cats 1, :dogs 2}
```

Sets also do not have guaranteed ordering. `conj` returns a set with the item added. As the concept of sets implies, added items will not duplicate equivalent items if they are present in the set.

```clojure
(conj #{1 4} 5)
;; #{1 4 5}

(conj #{:a :b :c} :b :c :d :e)
;; #{:a :c :b :d :e}
```

### get

`get` returns the value for the specified key in a map or record, index of a vector or value in a set. If the key is not present, `get` returns nil or a supplied default value.

```clojure
(get {:a 1 :b 2 :c 3} :b) ;; val of a key in a map
;; 2

(get [10 15 20 25] 2) ;; index of a vector
;; 20

(get #{1 10 100 2 20 200} 1) ;; in a set, returns the value itself if present
;; 1

(get {:a 1 :b 2} :c) ;; returns nil if key is not present
;; nil

(get [1 2 3 4] 4) ;; vector does not have an _index_ of 4. nil is returned
;; nil

(defrecord Hand [index middle ring pinky thumb])
(get (Hand. 3 4 3.5 2 2) :index)
;; 3
```

`get` also supports a default return value supplied as the last argument.

```clojure
(get [1 2 3 4] 4 "Not Found") ;; index 4 does not exist. return default value
;; "Not Found"

(get {:a 1 :b 2} :c 3) ;; key :c does not exist, so return default value of 3
;; 3
```

### assoc

`assoc` takes a key and a value and returns a collection of the same type as the supplied collection with the key mapped to the new value.

`assoc` is similar to get in how it works with maps, records or vectors. When applied to a map or record, the same type is returned with the key/value pairs added or modified.  When applied to a vector, a vector is returned with the key acting as an index and the index being replaced by the value.

Since maps and records can not contain multiple equivalent keys, supplying `assoc` with a key/value that exists in the one will cause `assoc` to return modify the key at that value in the result and not duplicate the key.

```clojure
(assoc {:a 1} :b 2)
;; {:b 2, :a 1}

(assoc {:a 1 :b 45 :c 3} :b 2)
;; {:a 1, :c 3, :b 2}

(defrecord Hand [index middle ring pinky thumb])
(assoc (Hand. 3 4 3.5 2 2) :index 3.75)
;; #user.Hand{:index 3.75, :middle 4, :ring 3.5, :pinky 2, :thumb 2}
```

When using `assoc` with a vector, the key is the index and the value is the value to assign to that index in the returned vector. The key must be <= (count vector) or a "IndexOutOfBoundsException" will occur. `assoc` can not be used to add an item to a vector.

```clojure
(assoc [1 2 76] 2 3)
;; [1 2 3]

(assoc [1 2 3] 5 6) ;; index 5 does not exist. valid indexes for this vector are: 0, 1, 2
;; IndexOutOfBoundsException   clojure.lang.PersistentVector.assocN (PersistentVector.java:136)
```

### dissoc

`dissoc` returns a map with the supplied keys, and subsequently their values, removed. Unlike `assoc`, `dissoc` does not work on vectors. When a record is provided, `dissoc` returns a map. For similar functionality with vectors, see `subvec` and `concat`.

```clojure
(dissoc {:a 1 :b 2 :c 3} :b)
;; {:a 1, :c 3}

(dissoc {:a 1 :b 14 :c 390 :d 75 :e 2 :f 51} :b :c :e)
;; {:a 1, :f 51, :d 75}


;; note that a map is returned, not a record.
(defrecord Hand [index middle ring pinky ring])
(dissoc (Hand. 3 4 3.5 2 2) :ring) ;; always be careful with the bandsaw!
;; {:index 3, :middle 4, :pinky 2, :thumb 2}
```

### first

`first` returns the first item in the collection. `first` returns nil if the argument is empty or is nil.

Note that for collections that do not guarantee order like some maps and sets, the behaviour of `first` should not be relied on.

```clojure
(first (range 10))
;; 0

(first [:floor :piano :seagull])
;; :floor

(first [])
;; nil
```

### rest

`rest` returns a seq of items starting with the second element in the collection. `rest` returns an empty seq if the collection only contains a single item.

`rest` should also not be relied on when using maps and sets unless you are sure ordering is guaranteed.

```clojure
(rest [13 1 16 -4])
;; (1 16 -4)

(rest '(:french-fry))
;;()
```

The behaviour of `rest` should be contrasted with `next`. `next` returns nil if the collection only has a single item. This is important when considering "truthiness" of values since an empty seq is "true" but nil is not.

```clojure
(if (rest '("stuff"))
  (print "Does this print?")) ;; yes, it prints.
  
  
;; INFINITE LOOP!!!
;; "done" is never reached because (rest x) is always a "true" value
(defn inf [x]
  (if (rest x)
    (inf (rest x))
    "done"))
```

### empty?

`empty?` returns true if the collection has no items, or false if it has 1 or more items.

```clojure
(empty? [])
;; true

(empty? '(1 2 3))
;; false
```

Be careful of mistypes. This can be a source of great confusion:

```clojure
(if (empty [1 2 3]) ;; empty returns an empty seq, which is true! use empty? here.
  "It's empty"
  "It's not empty")
;; "It's empty"
```

### empty

`empty` returns an empty collection of the same type as the collection provided.

```clojure
(empty [1 2 3])
;; []

(empty {:a 1 :b 2 :c 3})
;; {}
```

### not-empty

`not-empty` returns nil if the collection has no items. If the collection contains items, the collection is returned.

```clojure
(not-empty '(:mice :elephants :children))
;; (:mice :elephants :children)

(not-empty '())
nil
```

### contains?

`contains` returns  true if the provided value is present in a collection. `contains` is similar to `get` in that vectors treat the key as an index. `contains` will always return false for lists.

```clojure
(contains? {:a 1 :b 2 :c 3} :c)
;; true

(contains? ["John" "Mary" "Paul"] 2) ;; true if index 2 exists
;; true

(contains? ["John" "Mary" "Paul"] 5) ;; false if index 5 does not exist
;; false

(contains? ["John" "Mary" "Paul"] "Paul") ;; "Paul" does not exist as an index
;; false

(contains? '(1 2 3) 0) ;; lists always return false. Contain won't traverse a collection for a result.
;; false
```

### some

`some` will apply a predicate to each value in a collection until a non-false/nil result is returned then immediately return that result.

Since collections are "true" values, this makes it possible to return the first result itself rather than simply `true`.

```clojure
(some even? [1 2 3 4 5])
;; true

(some #(if (even? %) %) [1 2 3 4 5]) ;; predicate returns the value rather than simply true
;; 2
```

Since maps can be used as functions, you can use a map as a predicate. This will return the value of the first key in the collection that is also in the map.

```clojure
(some {:a 1 :b 5} [:h :k :d :b])
;; 5
```

Sets can also be used as functions and will return the first item in the collection that is present in the set.

```clojure
(some #{4} (range 20))
;; 4
```

### every?

`every` returns true if the predicate returns true for every item in the collection, otherwise it returns false.

```clojure
(every? even? (range 0 10 2))
;; true

;; set can be used to see if collection only contains items in the set.
(every? #{2 3 4} [2 3 4 2 3 4])
;; true
```

### keys

`key`s returns a sequence of the keys in a map or record.

```clojure
(keys {1 "one" 2 "two" 3 "three"})
;; (1 2 3)

(defrecord Hand [index middle ring pinky thumb])
(keys (Hand. 2 4 3 1 2))
;; (:index :middle :ring :pinky :thumb)
```

### vals

`vals` returns a sequence of vals in a map or record.

```clojure
(vals {:meows 20 :barks 2 :moos 5})
;; (5 2 20)

(defrecord Hand [index middle ring pinky thumb])
(vals (Hand. 1 2 3 4 5))
;; (1 2 3 4 5)
```

### map

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
TODO: Simple image accompaniment.

### iterate

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
TODO: Simple image accompaniment.

### reduce

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
TODO: Simple image accompaniment.

### reductions

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
TODO: Simple image accompaniment.

### filter

`filters` returns a lazy sequence of items that return `true` for the provided predicate. Contrast to `remove`

```clojure
(filter even? (range 10))
;; (0 2 4 6 8)

(filter #(if (< (count %) 5) %) ["Paul" "Celery" "Computer" "Rudd" "Tayne"])
;; ("Paul" "Rudd")
```

When using sets with `filter`, remember that if nil or false is in the set and in the collection, then the predicate will return itself: `nil`.

In this example, when nil and false are tested with the predicate, the predicate returns nil. This is because if the item is present in the set it is returned. This will cause that item to /not/ be included in the returned lazy-sequence.

```clojure
(filter #{:nothing :something nil} [:nothing :something :things :someone nil false :pigeons])
;; (:nothing :something)
```

### remove

`remove` returns a lazy sequence of items that return `false` or `nil` for the provided predicate. Contrast to `filter`.

```clojure
(remove even? (range 10))
;; (1 3 5 7 9)

(remove {:a 1 :b 2} [:h :k :z :b :s]) ;; relative complement. probably useless?
;; (:h :k :z :s)
```

When using sets with `remove`, remember that if nil or false is in the set and in the collection, then the predicate will return itself: `nil`. This will cause that item to be included in the returned lazy-sequence.

In this example, when nil and false are tested with the predicate, the predicate returns nil. This is because if the item is present in the set it is returned.

```clojure
(remove #{:nothing :something nil} [:nothing :something :things :someone nil false :pigeons])
;; (:things :someone nil false :pigeons)
```

### get-in

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
TODO: Simple image accompaniment.

### update-in

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
TODO: Simple image accompaniment.

### assoc-in

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
TODO: Simple image accompaniment.

### select-keys

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### take

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### drop

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### take-while

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### drop-while

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### partition

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### partition-all

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

## Macros

### ->

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
TODO: Simple image accompaniment.

### ->>

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
TODO: Simple image accompaniment.

## Reference Types

### deref

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### atom

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### swap!

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### reset!

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### agent

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### ref

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### dosync

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### alter

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### commute

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)

### binding

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)



## Vars

### *clojure-version*

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
