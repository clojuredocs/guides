---
title: "Overview of clojure.core, the standard Clojure library"
layout: article
---

This guide covers:

 * Key functions of `clojure.core`
 * Key macros of `clojure.core`
 * Key vars of `clojure.core`
 * Essential special forms

This guide is **by no means comprehensive** and does not try to explain each function/macro/form in depth. It is an overview,
the goal is to briefly explain the purpose of each item and provide links to other articles with more information.

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/guides).

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.5.


## Binding

<a id="let_desc"></a>
### let

```clojure
(let [bindings*] exprs*)
```

`let` takes a vector of symbol value pairs followed by a variable number of expressions.

`let` allows binding of locals (roughly equivalent to variables in many other languages) and defines an explicit scope for those bindings.

The body of a `let` statement also provides an implicit `do` that allows for multiple statements in the body of `let`.

A basic example:

``` clojure
(let [x 1 y 2]
  (println x y))
;; ⇒ 1 2
```

Let can be nested, and the scope is lexically determined. This means that a binding's value is determined by the nearest binding form for that symbol.

This example basically demonstrates the lexical scoping of the let form.

``` clojure
(let [x 1]
  (println x) ; prints 1
  (let [x 2]
    (println x))) ; prints 2
```

Let bindings are immutable and can be destructured.

TBD: link to the section about destructuring

<a id="def_desc"></a>
### def

```clojure
(def symbol doc-string? init?)
```

`def` takes a symbol and an optional init value.

If an init value is supplied, the root binding of the var is assigned to that value. Redefining a var with an init value will re-assign the root binding.

A root binding is a value that is shared across all threads.

The `let` form is the preferred method of creating local bindings. It is strongly suggested to prefer it where possible, and never use `def` within another form.


``` clojure
;; TBD - reference to var documentation, basic example
;; TBD - metadata
```

<a id="declare_desc"></a>
### declare

```clojure
([& names])
```

`declare` takes a variable number of symbols.

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

<a id="defn_desc"></a>
### defn

```clojure
([name doc-string? attr-map? [params*] prepost-map? body] [name doc-string? attr-map? ([params*] prepost-map? body) + attr-map?])
```

`defn` takes a symbol, an optional doc string, an optional meta-data
map, a vector of arguments and a variable number of expressions.

`defn` is the primary way of defining functions. It allows for
convenient definition of metadata about its argslist and documentation
(docstrings). `defn` inherently allows for quick documentation of
functions that can be retrieved with `doc`. This feature should be
used almost universally.

Without `defn`, a var would be directly bound to a function definition
and explicit metadata about the doc string and argslits would be added
manually.

``` clojure
(def func (fn [x] x))

;; same as:
(defn func [x]
  x)

;; with metadata added by defn
(def ^{:doc "documentation!"} ^{:arglists '([x])} func (fn [x] x))

;;same as
(defn func
  "documentation!"
  [x]
  x)
```

``` clojure
;; TBD - link to doc and metadata
```

## Branching

<a id="if_desc"></a>
### if

```clojure
(if test then else?)
```

`if` takes 2 expressions, and an optional third.

`if` is the primary method of conditional execution and other conditionals are built upon `if`.

If the return value of the first expression is anything except nil or false, the second expression is evaluated and the result returned..

If a third expression is provided, when the first expression returns nil or false the third expression is evaluated and returned.


``` clojure
(if 0 "second") ; 0 is a 'true' value. Only false or nil are 'false'
"second"

(if nil "second" "third")
;; ⇒ "third"

(if (< 10 9) "second" "third") ; (< 9 10) returns false
;; ⇒ "third"

(if (seq '()) "second") ; seq returns nil for an empty sequence
;; ⇒ nil

(if (nil? (= 1 2)) "second" "third") ; differentiate between nil and false if needed
;; ⇒ "third"
```

<a id="when_desc"></a>
### when

```clojure
([test & body])
```

`when` takes 2 expressions.

`when` provides an implicit do form that is evaluated if an expression returns true, otherwise nil is returned. `when` does not provide an 'else'.

``` clojure
(when (= 1 2) (print "hey") 10)
;; ⇒ nil

(when (< 10 11) (print "hey") 10)
;; hey
;; ⇒ 10
```

### for

See: [for](#for_desc)

### doseq

See: [doseq](#doseq_desc)

## Looping

<a id="recur_desc"></a>
### recur

```clojure
(recur exprs*)
```

`recur` allows for self-recursion without consuming stack space proportional to the number of recursive calls made. Due to the jack of tail-call optimization on the JVM currently, this is the only method of recursion that does not consume excess stack space.

`recur` takes a number of arguments identical to the point of recursion. `recur` will evaluate those arguments, rebind them at the point of recursion and resume execution at that point.

The point of recursion is the nearest `fn` or `loop` form determined lexically.

`recur` must be in the tail position of the recursion point expression. The tail position is the point in the expression where a return value would otherwise be determined and.

`recur` does not bind `&` in variadic functions and in these situations an empty seq must be passed by `recur`.

```clojure
(defn count-up
  [result x y]
  (if (= x y)
    result
    (recur (conj result x) (inc x) y)))
;; ⇒ [0 1 2 3 4 5 6 7 8 9]
```

TBD: more examples

<a id="loop_desc"></a>
### loop

```clojure
(loop [bindings*] exprs*)
```

`loop` takes a vector of symbol value pairs followed by a variable number of expressions.

`loop` establishes a recursion point for a `recur` expression inside its body. `loop` provides an implicit `let` for bindings.

The implicit `let` that `loop` provides binds each symbol to the init-expression. `recur` then binds new values when returning the execution point to `loop`.

```clojure
(defn count-up
  [start total]
  (loop [result []
         x start
         y total]
    (if (= x y)
      result
      (recur (conj result x) (inc x) y))))
;; ⇒ [0 1 2 3 4 5 6 7 8 9]
```

TBD: more examples

<a id="trampoline_desc"></a>
### trampoline

```clojure
([f])
([f & args])
```

`trampoline` takes a function and a variable number of arguments to pass to that function.

`trampoline` allows for mutual recursion without consuming stack space proportional to the number of recursive calls made.

If the return value of that function is a function, `trampoline` calls that function with no arguments. If the return value is not a function, `trampoline` simply returns that value.

Since `trampoline` calls the returned functions with no arguments, you must supply an anonymous function that takes no arguments and calls the function you wish to recur to. This is usually done with anonymous function literals ``` #() ```

```clojure
(declare count-up1 count-up2) ;; see `declare` for why this is needed

(defn count-up1
  [result start total]
  (if (= start total)
    result
    #(count-up2 (conj result start) (inc start) total))) ;; returns an anonymous function

(defn count-up2 [result start total]
  (if (= start total)
    result
    #(count-up1 (conj result start) (inc start) total))) ;; returns an anonymous function

(trampoline count-up1 [] 0 10)
;; ⇒ [0 1 2 3 4 5 6 7 8 9]
```

TBD: a trivial example that would not be easily solved with self-recursion

<a id="for_desc"></a>
### for

```clojure
([seq-exprs body-expr])
```

`for` takes a vector of pairs of [binding collection].

`for` allows for list comprehensions. `for`  assigns each sequential value in the collection to the binding form and evaluates them rightmost first. The results are returned in a lazy sequence.

`for` allows for explicit let, when and while through use of ":let []" ":when (expression)" ":while (expression)" in the binding vector.

``` clojure
(for [x [1 2 3] y [4 5 6]]
  [x y])
;; ⇒ ([1 4] [1 5] [1 6] [2 4] [2 5] [2 6] [3 4] [3 5] [3 6])
```

:when only evaluates the body when a true value is returned by the expression provided

``` clojure
(for [x [1 2 3] y [4 5 6]
      :when (and
             (even? x)
             (odd? y))]
  [x y])
;; ⇒ ([2 5])
```

:while evaluates the body until a non-true value is reached. Note that the rightmost collection is fully bound to y before a non-true value of (< x 2) is reached. This demonstrates the order of the comprehension.

``` clojure
(for [x [1 2 3] y [4 5 6]
      :while (< x 2)]
  [x y])
;; ⇒ ([1 4] [1 5] [1 6])
```

<a id="doseq_desc"></a>
### doseq

```clojure
([seq-exprs & body])
```

`doseq` takes a vector of pairs of [binding collection].

`doseq` is similar to `for` except it does not return a sequence of results. `doseq` is generally intended for execution of side-effects in the body, and thusly returns nil.

`doseq` supports the same bindings as for - :let :when :while. For examples of these, see for.

``` clojure
(doseq [x [1 2 3] y [4 5 6]]
  (println [x y]))

;; [1 4][1 5][1 6][2 4][2 5][2 6][3 4][3 5][3 6]
;; ⇒ nil
```


<a id="iterate_desc"></a>
### iterate

```clojure
([f x])
```

`iterate` takes a function and an argument to the function.

A lazy sequence is returned consisting of the argument then each subsequent entry is the function evaluated with the previous entry in the lazy sequence.

```clojure
TBD: Examples
```

TBD: Simple image accompaniment.

<a id="reduce_desc"></a>
### reduce

```clojure
([f coll])
([f val coll])
```

`reduce` takes a function, an optional initial value and a collection.

`reduce` takes the first item of the collection and either the second
item of the collection or the provided initial value, then evaluates
the function with those arguments. The function is then evaluated with
that result and the next item in the collection. This is repeated
until the collection is exhausted and the value of the final function
call is returned.

```clojure
TBD: examples
```

TBD: Simple image accompaniment.

<a id="reductions_desc"></a>
### reductions

```clojure
([f coll])
([f val coll])
```

`reductions` takes a function, an optional initial value and a collection.

`reductions` returns a lazy sequence consisting of the first item in
the collection, or the provided initial value followed by the result
of the function evaluated with the previous result and the next item
in the collection.

```clojure
TBD: examples
```

TBD: Simple image accompaniment.

<a id="map_desc"></a>
### map

```clojure
([f coll])
([f c1 c2])
([f c1 c2 c3])
([f c1 c2 c3 & colls])
```

`map` takes a function and one or more collections.  `map` passes an
item from each collection, in order, to the function and returns a
lazy sequence of the results.

The function provided to `map` must support an arity matching the
number of collections passed. Due to this, when using more than one
collection, map stops processing items when any collection runs out of
items.

```clojure
TBD: Examples
```

TBD: Simple image accompaniment.

## Collection and Sequence Modification

<a id="conj_desc"></a>
### conj

```clojure
([coll x])
([coll x & xs])
```

`conj` takes a collection and a variable number of arguments.

`conj` is short for "conjoin". As the name implies, `conj` returns the
collection with those arguments added.

Adding items to a collection occurs at different places depending on
the concrete type of collection.

List addition occurs at the beginning of the list. This is because
accessing the head of the list is a constant time operation, and
accessing the tail requires traversal of the entire list.

```clojure
(conj '(1 2) 3)
;; ⇒ (3 1 2)
```

Vectors have constant time access across the entire data
structure. `'conj' thusly appends to the end of a vector.

```clojure
(conj [1 2] 3)
;; ⇒ [1 2 3]
```

Maps do not have guaranteed ordering, so the location that items are
added is irrelevant. `conj` requires vectors of [key value] pairs to
be added to the map.

```clojure
(conj {:a 1 :b 2 :c 3} [:d 4])
;; ⇒ {:d 4, :a 1, :c 3, :b 2}

(conj {:cats 1 :dogs 2} [:ants 400] [:giraffes 13])
;; ⇒ {:giraffes 13, :ants 400, :cats 1, :dogs 2}
```

Sets also do not have guaranteed ordering. `conj` returns a set with
the item added. As the concept of sets implies, added items will not
duplicate equivalent items if they are present in the set.

```clojure
(conj #{1 4} 5)
;; ⇒ #{1 4 5}

(conj #{:a :b :c} :b :c :d :e)
;; ⇒ #{:a :c :b :d :e}
```

<a id="empty_desc"></a>
### empty

```clojure
([coll])
```

`empty` takes a collection

`empty` returns an empty collection of the same type as the collection
provided.

```clojure
(empty [1 2 3])
;; ⇒ []

(empty {:a 1 :b 2 :c 3})
;; ⇒ {}
```

<a id="assoc_desc"></a>
### assoc

```clojure
([map key val])
([map key val & kvs])
```

`assoc` takes a key and a value and returns a collection of the same
type as the supplied collection with the key mapped to the new value.

`assoc` is similar to get in how it works with maps, records or
vectors. When applied to a map or record, the same type is returned
with the key/value pairs added or modified.  When applied to a vector,
a vector is returned with the key acting as an index and the index
being replaced by the value.

Since maps and records can not contain multiple equivalent keys,
supplying `assoc` with a key/value that exists in the one will cause
`assoc` to return modify the key at that value in the result and not
duplicate the key.

```clojure
(assoc {:a 1} :b 2)
;; ⇒ {:b 2, :a 1}

(assoc {:a 1 :b 45 :c 3} :b 2)
;; ⇒ {:a 1, :c 3, :b 2}

(defrecord Hand [index middle ring pinky thumb]) (assoc (Hand. 3 4 3.5
2 2) :index 3.75) ;; ⇒ #user.Hand{:index 3.75, :middle 4, :ring 3.5,
:pinky 2, :thumb 2} ``` When using `assoc` with a vector, the key is
the index and the value is the value to assign to that index in the
returned vector.  The key must be <= (count vector) or a
"IndexOutOfBoundsException" will occur. `assoc` can not be used to add
an item to a vector.

```clojure
(assoc [1 2 76] 2 3) ;= [1 2 3]

;; index 5 does not exist. valid indexes for this vector are: 0, 1, 2
(assoc [1 2 3] 5 6) ;; IndexOutOfBoundsException   clojure.lang.PersistentVector.assocN (PersistentVector.java:136)
```

<a id="dissoc_desc"></a>
### dissoc

```clojure
([map])
([map key])
([map key & ks])
```

`dissoc` takes a map and a variable number of keys.

`dissoc` returns a map with the supplied keys, and subsequently their
values, removed. Unlike `assoc`, `dissoc` does not work on
vectors. When a record is provided, `dissoc` returns a map. For
similar functionality with vectors, see `subvec` and `concat`.

```clojure
(dissoc {:a 1 :b 2 :c 3} :b)
;; ⇒ {:a 1, :c 3}

(dissoc {:a 1 :b 14 :c 390 :d 75 :e 2 :f 51} :b :c :e)
;; ⇒ {:a 1, :f 51, :d 75}

;; note that a map is returned, not a record.
(defrecord Hand [index middle ring pinky thumb])
;; always be careful with the bandsaw!
(dissoc (Hand. 3 4 3.5 2 2) :ring)
;; ⇒ {:index 3, :middle 4, :pinky 2, :thumb 2}
```

## Information about a Collection or Sequence

<a id="count_desc"></a>
### count

```clojure
([coll])
```

`count` takes a collection.

Returns a count of the number of items in a collection. An argument of nil returns 0.

``` clojure
(count "Hello")
;; ⇒ 5

(count [1 2 3 4 5 6 7])
;; ⇒ 7

(count nil)
;; ⇒ 0
```

Note that count does not return in constant time for all
collections. This can be determined with `counted?`.  Keep in mind
that lazy sequences must be realized to get a count of the items. This
is often not intended and can cause a variety of otherwise cryptic
errors.

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
<a id="empty?_desc"></a>
### empty?

```clojure
([coll])
```

`empty` takes a collection.

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

<a id="not-empty_desc"></a>
### not-empty

```clojure
([coll])
```

`not-empty` takes a collection.

`not-empty` returns nil if the collection has no items. If the collection contains items, the collection is returned.

```clojure
(not-empty '(:mice :elephants :children))
;; ⇒ (:mice :elephants :children)

(not-empty '())
;; ⇒ nil
```

## Items in a Collection or Sequence

<a id="first_desc"></a>
### first

```clojure
([coll])
```

`first` takes a collection.

`first` returns the first item in the collection. `first` returns nil
if the argument is empty or is nil.

Note that for collections that do not guarantee order like some maps
and sets, the behaviour of `first` should not be relied on.

```clojure
(first (range 10))
;; ⇒ 0

(first [:floor :piano :seagull])
;; ⇒ :floor

(first [])
;; ⇒ nil
```

<a id="rest_desc"></a>
### rest

```clojure
([coll])
```

`rest` takes a collection.

`rest` returns a seq of items starting with the second element in the
collection. `rest` returns an empty seq if the collection only
contains a single item.

`rest` should also not be relied on when using maps and sets unless
you are sure ordering is guaranteed.

```clojure
(rest [13 1 16 -4])
;; ⇒ (1 16 -4)

(rest '(:french-fry))
;; ⇒ '()
```

The behaviour of `rest` should be contrasted with `next`. `next`
returns nil if the collection only has a single item. This is
important when considering "truthiness" of values since an empty seq
is "true" but nil is not.

```clojure
(if (rest '("stuff"))
  (print "Does this print?")) ;; yes, it prints.


;; NEVER FINISHES EXECUTION!
;; "done" is never reached because (rest x) is always a "true" value
(defn inf
  [x]
  (if (rest x)
    (inf (rest x))
    "done"))
```

<a id="get_desc"></a>
### get

```clojure
([map key])
([map key not-found])
```

`get` takes an associative collection, a sequence of keys and an optional default value.

`get` returns the value for the specified key in a map or record,
index of a vector or value in a set. If the key is not present, `get`
returns nil or a supplied default value.

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

<a id="contains?_desc"></a>
### contains?

```clojure
([coll key])
```

`contains?` takes a map and a key.

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

<a id="keys_desc"></a>
### keys

```clojure
([map])
```

`keys` takes a map or record.

`keys` returns a sequence of the keys in a map or record.

```clojure
(keys {1 "one" 2 "two" 3 "three"})
;; ⇒ (1 2 3)

(defrecord Hand [index middle ring pinky thumb])
(keys (Hand. 2 4 3 1 2))
;; ⇒ (:index :middle :ring :pinky :thumb)
```

<a id="vals_desc"></a>
### vals

```clojure
([map])
```

`vals` takes a map or record.

`vals` returns a sequence of vals in a map or record.

```clojure
(vals {:meows 20 :barks 2 :moos 5})
;; ⇒ (5 2 20)

(defrecord Hand [index middle ring pinky thumb])
(vals (Hand. 1 2 3 4 5))
;; ⇒ (1 2 3 4 5)
```

<a id="take_desc"></a>
### take

```clojure
([n coll])
```

`take` takes a number and a collection.

`take` returns a lazy sequence starting with the first value of the collection and n sequential items after that.

If the number of items in the collection is less than the provided number, the entire collection is returned lazily.

```clojure
TBD: example
```

<a id="drop_desc"></a>
### drop

```clojure
([n coll])
```

`drop` takes a number and a collection.

`drop` returns a lazy sequence starting at the nth item of the collection.

```clojure
TBD: example
```

<a id="take-while_desc"></a>
### take-while

```clojure
([pred coll])
```

`take-while` takes a function that accepts a single-argument and a collection.

`take-while` returns a lazy sequence of sequential items until the function returns nil/false value for that item.

```clojure
TBD: example
```

<a id="drop-while_desc"></a>
### drop-while

```clojure
([pred coll])
```

'drop-while` takes a function that accepts a single-argument and a collection.

`drop-while` returns a lazy sequence starting at the first item in the collection that the function returns nil/false.

<a id="filter_desc"></a>
### filter

```clojure
([pred coll])
```

`filter` takes a function that accepts a single argument and a collection.

`filters` returns a lazy sequence of items that return `true` for the provided predicate. Contrast to `remove`

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

<a id="keep_desc"></a>
### keep

`(keep f coll)`

`keep` takes a function that accepts a single argument and a collection.

`keep` returns a lazy sequence of non-nil results of the function applied to each item in the collection in sequence.

```clojure
TBD: examples
```

<a id="remove_desc"></a>
### remove

```clojure
([pred coll])
```

`remove` takes a function that accepts a single argument and a collection.

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


<a id="some_desc"></a>
### some

```clojure
([pred coll])
```

`some` takes a function that accepts a single argument and a collection.

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

<a id="every?_desc"></a>
### every?

```clojure
([pred coll])
```

`every` takes a function that accepts a single argument and a collection.

`every` returns true if the predicate returns true for every item in the collection, otherwise it returns false.

```clojure
(every? even? (range 0 10 2))
;; ⇒ true

;; set can be used to see if collection only contains items in the set.
(every? #{2 3 4} [2 3 4 2 3 4])
;; ⇒ true
```

## Processing Collections and Sequences

<a id="partition_desc"></a>
### partition

```clojure
([n coll])
([n step coll])
([n step pad coll])
```

`partition` takes a number, an optional step, an optional padding collection and a collection. If the padding collection is provided, a step must be provided.

`partition` sequentially takes a provided number of items from the collection in sequence and puts them into lists. This lazy sequence of lists is returned.

If a step is provided, the lists in the returned lazy sequence start at offsets in the provided collection of that number items in the list.

If a padding collection is provided, the last item in the returned lazy sequence will be padded with the padding collection to achieve the desired partitioning size.

If there is no padding collection provided and there is not enough items to fill the last list in the returned lazy sequence, those items will be not used.

```clojure
TBD: example
```

<a id="partition-all_desc"></a>
### partition-all

```clojure
([n coll])
([n step coll])
```

`partition-all` takes a number, an optional step and a collection.

`partition-all` sequentially takes a provided number of items from the collection in sequence and puts them into lists. This lazy sequence of lists is returned.

If a step is provided, the lists in the returned lazy sequence start at offsets in the provided collection of that number items in the list.

If there are not enough items to fill the last list in the returned lazy sequence, the remaining items will be used in the last list.

```clojure
TBD: example
```

### filter
See: [filter](filter_desc)
### remove
See: [remove](remove_desc)
### for
See: [for](for_desc)
### map
See: [map](map_desc)
### remove
See: [remove](remove_desc)
### empty?
See: [empty](empty?_desc)
### not-empty
See: [not-empty](not-empty_desc)

## Function Composition and Application

<a id="juxt_desc"></a>
### juxt

```clojure
([])
([f])
([f g])
([f g h])
([f1 f2 f3 & fs])
```

`juxt` takes a variable number of functions.

`juxt` returns a function that will return a vector consisting of the result of each of those functions to a provided argument.

```clojure
TBD: examples
```

TBD: Simple image accompaniment.

<a id="comp_desc"></a>
### comp

```clojure
([])
([f])
([f g])
([f g h])
([f1 f2 f3 & fs])
```

`comp` takes a variable number of functions.

`comp` returns a function that will return the result of applying the rightmost function to the provided argument, then the second rightmost function to the result of that etc...

```clojure
TBD: examples
```

TBD: Simple image accompaniment.

<a id="fnil_desc"></a>
### fnil

```clojure
([f x])
([f x y])
([f x y z])
```

`fnil` takes a function and one to three arguments.

`fnil` returns a function that replaces any nil arguments with the provided values. `fnil` only supports supports patching 3 arguments, but will pass any arguments beyond that un-patched.

```clojure
(defn say-info [name location hobby]
  (println name "is from" location "and enjoys" hobby))

(def say-info-patched (fnil say-info "Someone" "an unknown location" "Clojure"))

(say-info-patched nil nil nil)
;; ⇒ Someone is from an unknown location and enjoys Clojure

(say-info-patched "Robert" nil "giraffe migrations")
;; ⇒ Robert is from an unknown location and enjoys giraffe migrations
```

<a id="apply_desc"></a>
### apply

```clojure
([f args] [f x args] [f x y args] [f x y z args] [f a b c d & args])
```

`apply` takes a variable number of arguments and a collection.

`apply` effectively unrolls the supplied args and a collection into a list of arguments to the supplied function.

``` clojure
(str ["Hel" "lo"])
;; ⇒ "[\"Hel\" \"lo\"]" ;; not what we want, str is operating on the vector

(apply str ["Hel" "lo"]) ;; same as (str "Hel" "lo")
;; ⇒ "Hello"
```

`apply` prepends any supplied arguments to the form as well.

``` clojure
(map + [[1 2 3] [1 2 3]]) ;; This attempts to add 2 vectors with +
;; ClassCastException   java.lang.Class.cast (Class.java:2990)

(apply map + [[1 2 3] [1 2 3]]) ;; same as (map + [1 2 3] [1 2 3])
;; ⇒ (2 4 6)

(apply + 1 2 3 [4 5 6]) ;; same as  (+ 1 2 3 4 5 6)
;; ⇒ 21
```

Note that apply can not be used with macros.

<a id="-_desc"></a>
### ->

```clojure
([x])
([x form])
([x form & more])
```

`->` takes a value and optionally one or more expressions.

`->` takes the first argument and inserts it as the second item in the next form, or creates a list with the first argument as the second item. The return value of that expression is inserted as the second item in the next form, making a list if necessary.  This continues until all expressions are evaluated and the final value is returned.

```clojure
TBD: example
```

TBD: Simple image accompaniment.

<a id="-_desc"></a>
### ->>

```clojure
([x])
([x form])
([x form & more])
```

`->>` takes a value and optionally one or more expressions.

`->>` takes the first argument and inserts it as the last item in the next form, or creates a list with the first argument as the last item. The return value of that expression is inserted as the last item in the next form, making a list if necessary.  This continues until all expressions are evaluated and the final value is returned.

TBD: Simple image accompaniment.

## Associative Collections

<a id="get-in_desc"></a>
### get-in

```clojure
([m ks] [m ks not-found])
```

`get-in` takes an associative collection, a sequence of keys and an optional default value.

`get-in` takes the first value in the sequence of keys and retrieves the value, then applies each subsequent key to to the most recently returned value and returns the final result. If any key is not present when evaluated then either nil, or a provided default value is returned.

```clojure
TBD: example
```

TBD: Simple image accompaniment.

<a id="update-in_desc"></a>
### update-in

```clojure
([m [k & ks] f & args])
```

`update-in` takes an associative collection, a sequence of keys, a function and optional arguments to supply to that function.

`update-in` takes the first value in the sequence of keys and retrieves the value, then applies each subsequent key to to the most recently returned value. The function and optional arguments are applied to the value and a new nested collection is returned with the key having the result of that function.

`update-in` will create new hash-maps if a key in the sequence of keys does not exist. The returned collection will have a nested structure correlating to the provided sequence along with the result of the function and optional arguments as the value of the final key.

```clojure
TBD: example
```

TBD: Simple image accompaniment.

<a id="assoc-in_desc"></a>
### assoc-in

```clojure
([m [k & ks] v])
```

`assoc-in` takes an associative collection, a sequence of keys and a value.

`assoc-in` takes the first value in the sequence of keys and retrieves the value, then applies each subsequent key to to the most recently returned value. The final key is assigned the provided value and a new nested collection is returned.

`update-in` will create new hash-maps if a key in the sequence of keys does not exist. The returned collection will have a nested structure correlating to the provided sequence along with the provided value as the value of the final key.

```clojure
TBD: example
```

TBD: Simple image accompaniment.

<a id="select-keys_desc"></a>
### select-keys

```clojure
([map keyseq])
```

`select-keys` takes an associative collection and a sequence of keys.

`select-keys` returns a map containing only the entries that have a key which is also present in the sequence of keys.

```clojure
TBD: example
```

### keys
See: [keys](#keys_desc)
### vals
See: [vals](#vals_desc)
### get
See: [get](#get_desc)
### assoc
See: [assoc](#assoc_desc)
### dissoc
See: [dissoc](#dissoc_desc)

## Namespace Functions

<a id="ns_desc"></a>
### ns, require, use, import, refer

Please see the [Namespace guide](/articles/language/namespaces.html)

## Reference Types

<a id="ref_desc"></a>
### ref, atom, var, agent

Please see the [Concurrency and Parallelism Guide](articles/language/concurrency_and_parallelism.html)

<a id="deref_desc"></a>
### deref, swap!, reset!, dosync, alter, commute, binding

Please see the [Concurrency and Parallelism Guide](articles/language/concurrency_and_parallelism.html)

## Contributors

Robert Randolph <audiolabs@gmail.com> (original author)
Michael Klishin <michael@defprotocol.org>
