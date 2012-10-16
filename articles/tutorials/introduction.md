---
title: "Introduction to Clojure"
layout: article
---

## About this tutorial

This guide covers:

 * Clojure language basics
 * expressions, identifiers (locals, vars)
 * `let` forms
 * scalars
 * functions
 * basic data types
 * introduction to immutable data structures
 * overview of Clojure reference types (vars, atoms, agents, refs)
 * looping and recursion
 * basics of Clojure macros

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).



## Overview

This is a brief beginner's introduction to Clojure. If you haven't
already done so, have a look at the [Getting
Started](/articles/tutorials/getting_started.html) tutorial. Before
continuing, make sure you've got Java and
[Leiningen](http://leiningen.org) installed, and can create a new
project and run a REPL in it. The author expects you'll want to have a
REPL running while following this introduction so you can type
expressions into it as you go.

> **Note:** In the code samples below, unless we're specifically
> discussing the REPL, to reduce clutter we've usually omitted showing
> the REPL prompt (ex. "`user=>`" or "`my-proj.core=>`").
>
> Additionally: In Clojure, a semicolon begins a single-line comment,
> and in this document we use "`; ⇒`" (for trailing comments) and
> "`;; ⇒`" (for comments on their own line) to indicate what the
> previous expression evaluates to.

This introduction is a whirlwind tutorial of most of the basics of
Clojure. Its goal is to rapidly get you acquainted with the core
areas of the language without wasting your time and also without getting
too bogged down in details or advanced topics (the various topics will
get more comprehensive coverage in the topic guides anyway).

As we said in the Getting Started tutorial, Clojure is a wonderfully
simple language, and you're going to love it.




## Preliminaries

Clojure is a general-purpose programming language, and a quite
practical one at that.

The syntax for Clojure is like Lisp and is very simple: code is made
up of expressions which are evaluated to some value. Here are some
examples of expressions:

``` clojure
5                      ; ⇒ 5
"hi"                   ; ⇒ "hi"
[1 2 3]                ; evaluates to the vector `[1 2 3]`
(+ 1 2)                ; evaluates to the sum of 1 and 2
(if true "yes" "no")   ; evaluates to the string "yes"
(println "hello!")     ; evaluates to nil (but also prints "hello!")
```

Expressions can contain sub-expressions:

``` clojure
(+ 1
   (* 2 3)
   (/ 10 2))   ; ⇒ 1 + (2 * 3) + (10 / 2) = 12
```

Expressions in (various types of) brackets are often referred to as
"forms".

When Clojure sees something in parentheses, Clojure assumes it's a
regular function call. There are exceptions to this rule with macros
and special forms; more about that in the [Evaluation](#evaluation)
section, below.

Clojure is not whitespace-sensitive. Also, commas count as whitespace,
so you can omit them (for example, you can write a vector as `[1 2 3]`
instead of `[1, 2, 3]`).

Clojure code is block-structured and lexically scoped (though dynamic
scope is supported as well, if you really need it).

Clojure is a compiled language. The Clojure reader reads your source
code, then your code is compiled to JVM bytecode, and then it's run on
the JVM.  The reader supports a few extra bits of syntactic sugar (for
example, a literal syntax for specifying regular expressions) that we
will cover as we go along.

Throughout this tutorial, we will liberally reference and lean on the
marvelous [Clojure Cheatsheet](http://clojure.org/cheatsheet). Aside
from being a great organizational aide, it also handily includes links
to the relevant [Clojuredocs](http://clojuredocs.org/) pages where you
can find docs for and examples of the various Clojure functions.

In the REPL, at any time you can see the documentation for a given
function:

    (doc some-function)

and even the source code for it:

    (source some-function)

So, it will be of great use to you to have your REPL running so you
can try things out while following along.



## Identifiers

Identifiers are used to names things. For example, in

``` clojure
(def the-answer 42)
```

we've named something "the-answer" and given it the value 42.

We'll mostly use lowercase letters, numbers, and dashes to name
things, although some other characters are allowed too. We'll note
those cases later on as they come up.



## Scalars

Clojure has support for the following kinds of scalar values:

``` clojure
nil
true, false
```

`nil` is like Python's None, or Java's null. It's just another value.
Incidentally, there's no "undefined" value in Clojure --- if you try
to use a symbol which you haven't defined, then it's undefined and the
compiler will let you know about it.

As we go along, type those expressions into your REPL to see them
evaluated. These too:

``` clojure
1        ; integer
1N       ; arbitrary-precision integer
1.2      ; float/double/decimal
1.2M     ; arbitrary-precision decimal
1.2e4    ; scientific notation
1.2e4M   ; sci notation of arbitrary-precision decimal

0x3a     ; hex literal (58 in decimal)
1/3      ; Rational number, or "ratio".
\a       ; The character "a".
"hi"     ; A string.
```

Strings can span multiple lines --- just hit Enter and keep typing. If
you want to include a double-quote mark in a string, backslash-escape
it.

``` clojure
#"^foo\d?$"   ; A regular expression.
:foo          ; A keyword.
```

We'll have more to say about [regular
expressions](#regular_expressions) later on.

Keywords are just scalars that evaluate to themselves and are useful
where in other languages you might use little strings as identifiers
(for example, as the keys in a hashmap). More about keywords in the
next section ([Data Structures](#data_structures)).

``` clojure
'foo   ; A symbol.
```

A *symbol* is an object that represents the *name* of something. The
single quote mark is there to keep Clojure from trying to figure out
to what the symbol refers (the quote isn't part of the identifier of
the symbol). When you want to represent the name of a thing --- rather
than the value to which it refers --- you use a symbol. Their utility
will become clearer later on when we briefly mention
[Macros](#macros).

> **Terminology:** By "object" we just mean the internal thing that
> Clojure uses to represent a value --- *not* "object" as in "object
> oriented programming".  Clojure is not an object oriented
> language. Sure, you can easily access Java OOP objects from Clojure,
> but that is outside the scope of this tutorial.
>
> Also, the words "reference" and "refer" are used in Clojure in the
> generic sense. A symbol refers to an object (it is not the object
> itself).  Clojure *also* happens to support something called
> *reference types*. We'll cover them later on in the [Reference
> Types](#reference_types) section.



## Data Structures

Clojure comes out of the box with nice literal syntax for the various
core data structures:

``` clojure
[1 2 3]            ; A vector (can access items by index).
[1 :two "three"]   ; Put anything into them you like.
{:a 1 :b 2}        ; A hashmap (or just "map", for short).
```

A hashmap is your typical hash/dictionary data structure. In the above
example, the keys are :a and :b, and the values are 1 and 2. One key-value
pair in a map is called an *entry*.

Although it's most common to use keywords (as shown above) for hashmap
keys, you can use any values you like for the keys as well as the
values.

``` clojure
#{:a :b :c}        ; A set (unordered, and contains no duplicates).
'(1 2 3)           ; A list (linked-list)
```

You generally don't use lists very often for typical sequential data
in your programs:

``` clojure
(def my-stuff '("shirt" "coat" "hat"))  ; Works fine, but ...
(def my-stuff ["shirt" "coat" "hat"])   ; this is more typical usage.
```

Lists are most often used when treating code itself as just a bunch of
nested lists --- see [Macros](#macros).

BTW, don't mind that single-quote mark before the list's open paren;
it's just there to tell Clojure that this isn't a function call
(discussed in [Functions](#functions), below), but rather, an actual
list.

> Note: In Clojure, we use the term "vector" rather than "array".
> "Array" would refer to the native Java array, whereas "vector"
> refers to the Clojure data structure.

Nesting data structures works like you'd expect:

``` clojure
#{:a
  [1 2 3]
  {:foo 11 :bar 12}
  #{"shirt" "coat" "hat"}}
```

We will see how to get at values inside nested data strucures a little
later on.



### Abstractions

The data structures we just looked at (lists, vectors, maps, and sets)
are all concrete data types. The various Clojure functions for working
on them (which we will get to later on) actually aren't written to
work on the concrete types, but rather, are written to work on
abstract data types. The concrete data types are implementations of
the various abstract data types.

Some of the Clojure abstractions are:

  * Collection: lists, vectors, maps, and sets are all collections.
  * Sequential: lists and vectors are collections with ordering.
  * Associative: hashmaps associate keys with values. vectors associate numeric indices with values.
  * Indexed: you can index into vectors.

In the docs for the various functions, you'll often see, for example,
that they take a "coll". This means that the particular function will
work on any of the collections.

> If you'd like to look under the covers and see what the type of
> an object is, try `(type my-stuff)`.



## Evaluation

So far you've been typing various literal values (expressions) into
the repl and Clojure has evaluated them and repeated their resulting
values back to you (printed them out in the repl):

``` clojure
user=> "hi"
;; "hi"
user=> :foo
;; :foo
user=> [1 2 3]
;; [1 2 3]
```

Clojure evaluates the expressions you give it and tries to come up
with a resulting value. If the expression starts with an open paren,
Clojure treats it as either a macro, a *special form* (discussed
below) or else a function call.



### Function Calls

If the symbol right after the open paren names a function, Clojure
evaluates all of its function arguments first, then applies the
function to the values of those args:

``` clojure
(my-func arg1 arg2 arg3)
```

You can nest function calls as deep as tasteful discretion allows:

``` clojure
(my-func (my-func2 arg1
                   arg2)
         (other-func arg-a
                     (foo-bar arg-x
                              arg-y
                              (+ arg-xx
                                 arg-yy
                                 arg-zz))
                     arg-b))
```

Note that your code will be easiest to read if you line up args to
functions vertically (as shown above). Your editor should take care of
this for you automatically.

By the way, there are no "operators" in Clojure per se; just function
names (symbols which refer to their corresponding functions). So, for
example, `+`, `>`, `<=`, `=`, `*`, and `not=` are all just function
names.



### Macros and Special Forms

If an expression starts with an open paren, Clojure first checks to
see if it's a macro or special form. These are forms which don't
follow the regular evaluation rule and get special treatment from the
Clojure compiler.

Macros are like functions which take as arguments regular Clojure code
(which is, after all, just a list of expressions and (usually nested)
other lists), and returns the code transformed / expanded in some
useful way.

You write macros to add new syntax to the Clojure language, and
usually it's only done when necessary, after you've already gotten as
far as you can with plain functions.

Macros are created using `defmacro`. Writing them involves
manipulating lists (Clojure code), just like you've already
seen. Though quoting and unquoting is used to control evaluation of
the code you're handling.

Macro calls in your code get expanded at compile-time, right before
the rest of your code is compiled. Certain Clojure built-ins like
`let`, `def`, and `if` are written as special forms which are
hard-coded into the compiler rather than macros, but this is an
implementation detail; the effect is the same.

This tutorial does not discuss macros further.



### Quoting

If for whatever reason you'd rather Clojure *not* treat something like
`(+ 1 2 3)` as a function call, you can "quote" it like so:

``` clojure
'(+ 1 2 3)
;; ⇒ (+ 1 2 3)
```

This causes Clojure to then regard it simply as a 4-element list;
the first element of which is the symbol for some function.  Reasons
for wanting to do this will become clearer later on.




## Let and Locals

When you want some lexically-scoped named values to use in a section
of your code, you can use the `let` expression:

``` clojure
(let [width  10
      height 20]
  (println "hello from inside the `let`.")
  (* width height))
```

The first thing inside the `let` is a binding vector. In it, you
specify the local names you'd like to make available inside the `let`,
along with their values.

> **Formatting note:** Your readers might appreciate you vertically
> lining up the values used in the binding vector, as we've done
> above with 10 and 20.

These local names are symbols that refer directly to the values you
set them to.

You can re-set the symbols in the binding vector multiple times
(building it up into the value you need), if you find it useful:

``` clojure
(let [x 2
      x (* x x)
      x (+ x 1)]
  x)
;; ⇒ 5
```

The `let` expression itself evaluates to the last expression in its
body.  You can put other things inside the `let` (like our `println`
expression, in the previous example), but the overall value of the
`let` is its last expression.

> Note that the `println` expression just evaluates to nil. We don't
> use its value for anything --- we only care about its *side-effects*
> (printing out to the console). More about
> [Side-Effects](#side_effects) shortly.




## Namespaces

Clojure uses *namespaces* to organize function names into groups
and to keep them from colliding with other function names.
All function names live in a namespace. All the core functions
we've been using thus far are in the clojure.core namespace:

``` clojure
(clojure.core/println "hi")
```

That's the fully-qualified name of `println`. You'd normally have to
use the fully-qualified name for functions (or else use an alias to
the namespace --- covered in a moment), but Clojure makes all the
clojure.core functions automatically available by their unqualified
names (that is, sans namespace) for convenience.

Fully-qualified names are written "namespace/symbol". The namespace
may have dots in it, which correspond to directories in your
filesystem. For example, the function foo-bar.core/my-func corresponds
to the my-func function in src/foo_bar/core.clj. (It's just a bit of
the underlying Java platform showing through that you need to use
underscores in your directory names instead of dashes).

It's most common for one source code file to correspond to one
namespace, and often comprise one *library*. At the top of your source
file, you write `(ns whatever)` and that declares the namespace for
the rest of the file.

In the repl, you can make use of libraries --- and at the same time
provide a handy alias for them --- by *requiring* them like so:

``` clojure
(require '[clojure.string :as str])
```

Now we can use all the functions in the clojure.string library by
prefixing them with "str/". We'll do exactly this in the section below
on [Functions for working with
strings](#functions_for_working_with_strings).





## Functions for Creating Data Structures

There are functions for creating the various data structures without
using the usual literal syntax:

``` clojure
(list 1 2 3)            ; ⇒ '(1 2 3)
(vector 1 2 3)          ; ⇒ [1 2 3]
(hash-map :a 1 :b 2)    ; ⇒ {:a 1 :b 2}
(hash-set :a :b :c)     ; ⇒ #{:a :b :c}
```

And there are various functions for converting between vectors, sets,
and maps:

``` clojure
(def my-vec [1 2 3])
(set my-vec)                   ; ⇒ #{1 2 3}

(def my-map {:a 1 :b 2})
(vec my-map)                   ; ⇒ [[:a 1] [:b 2]]
(flatten (vec my-map))         ; ⇒ (:a 1 :b 2)
(set my-map)                   ; ⇒ #{[:b 2] [:a 1]}

(def my-set #{:a :b :c :d})
(vec my-set)                   ; ⇒ [:a :c :b :d]

;; And for fun:
(zipmap [:a :b :c] [1 2 3])    ; ⇒ {:c 3 :b 2 :a 1}
(apply hash-map [:a 1 :b 2])   ; ⇒ {:a 1 :b 2}
```

(We cover `apply` in the [Bread and Butter
functions](#bread_and_butter_functions) section.)

If you need to convert to a sequential collection (list or vector),
but don't need to access by index, you can use `seq` instead of `vec`
(to convert to a generic non-indexable list-like ("sequential") data
structure).  More about `seq` when we get to [Laziness](#laziness).

> By the way, you may have noticed a pattern here: longer function
> names are for passing in values one-by-one to create the data
> structure, whereas the shorter function names are for passing in a
> whole data structure at once:
>
>     literal  long name  short name
>     -------  ---------  ------------------
>     ()       list       *{no short name}*
>     []       vector     vec
>     {}       hash-map   *{no short name}*
>     #{}      hash-set   set
>
> You might think of `seq` as the short name for `list`, but that's
> probably pushing it, since there are a few differences.



## Functions For Working With Data Structures

Getting values from data structures:

``` clojure
;; Vectors
(def v [:a :b :c])
(nth v 1)             ; ⇒ :b
(v 1)                 ; ⇒ :b  (same)
(first v)             ; ⇒ :a
(rest v)              ; ⇒ (:b :c)
(next v)              ; ⇒ (:b :c)
(last v)              ; ⇒ :c

;; Lists
;; Same as vectors, but can't index.

;; Maps
(def m {:a 1 :b 2}
(get m :a)            ; ⇒ 1
(m :a)                ; ⇒ 1       (same)
(:a m)                ; ⇒ 1       (same!)
(get m :x 44)         ; ⇒ 44      (if no :x, 44 is the default)
(keys m)              ; ⇒ (:a :b)
(vals m)              ; ⇒ (1 2)
;; Grab a key or a val from a single map entry:
(key (first m))       ; ⇒ :a
(val (first m))       ; ⇒ 1
;; Of course, note that maps are not ordered.

;; Sets
(def s #{:a :b :c})
(s :a)                ; ⇒ :a
(s :z)                ; ⇒ nil
```

Data structures in Clojure are actually *immutable* --- you can't
change them. Though it may sound batty, it actually works out nicely
in practice, and we'll read more about in the
[Immutability](#values_immutability_and_persistence) section
below. For now, just note that data structures can't be mutated, but
we *can* get a new modified copy of a data structure:

``` clojure
;; Vectors
(def v   [:a :b :c])
(def li '(:a :b :c))
(conj v  :d)          ; ⇒ [:a :b :c :d]
(conj li :d)          ; ⇒ (:d :a :b :c)

v   ; ⇒ is still [:a :b :c]
li  ; ⇒ is still (:a :b :c)

;; Maps
(def m {:a 1 :b 2})
(assoc m :c 3)        ; ⇒ {:a 1 :c 3 :b 2}
(dissoc m :b)         ; ⇒ {:a 1}

m   ; ⇒ is still {:a 1 :b 2}

;; Sets
(def s #{:a :b})
(conj s :c)           ; ⇒ #{:a :c :b}
(disj s :a)           ; ⇒ #{:b}

s   ; ⇒ is still #{:a :b}
```

See the [cheatsheet](http://clojure.org/cheatsheet) for much more
you can do with these core data structures.




## Regular Expressions

As you've seen, Clojure provides a handy literal syntax for regular
expressions: `#"regex here"`. Clojure uses the same regular expression
syntax as Java, which is nearly the same as what Perl 5 (and Python,
and Ruby) uses. You can read more about the specifics in the Java
[java.util.regex Pattern
docs](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html).

Clojure provides a number of functions for working with strings, and a
number of those can make use of regexes. See the next section for some
examples.



## Functions For Working With Strings

There are a number of functions for working with strings listed in the
Strings section of the cheatsheet. Here are some examples of a few of
them:

``` clojure
(str "hi" "there")
;; ⇒ "hithere"
(count "hello")
;; ⇒ 5
(require '[clojure.string :as str])
;; ⇒ nil
(str/split "hello there" #" ")
;; ⇒ ["hello" "there"]
(str/join ["hello" "there"])
;; ⇒ "hellothere"
(str/join " " ["hello" "there"])
;; ⇒ "hello there"
(str/replace "hello there" "ll" "LL")
;; ⇒ "heLLo there"
```

Some of them make optional use of regexes. There's more in the
cheatsheet. Try them out!

Incidentally, since strings are sequential, any function that works on
sequentials works on strings. For example:

``` clojure
(first "hello")
;; ⇒ \h
(last "hello")
;; ⇒ \o
(rest "hello")
;; ⇒ (\e \l \l \o)
(nth "hello" 1)
;; ⇒ \e
(doseq [letter "hello"] (println letter))
;; h
;; e
;; l
;; l
;; o
;; ⇒ nil
```

Again, see the cheatsheet for more.




## Values, Immutability, and Persistence

A *value* is fundamentally a constant thing; For example, the letter
"a" is a value. You don't "set" the letter "a" to some other value; it
always stays the letter "a". It's immutable. The value 10 is always
10. You can't ever "set 10 to 11". That makes no sense. If you want
11, you just use 11 instead of 10.

In Clojure, *all scalars and core data structures are like this*. They
are values. They are immutable. The map

``` clojure
{:name "John"
 :hit-points 200
 :super-power :resourcefulness}
```

is a value. If you want to "change" John's hit-points, you don't
change anything per se, but rather, you just conjure up a whole new
hashmap value.

**But wait:** If you've done any imperative style programming in
C-like languages, this sounds crazy wasteful. *However*, the yin to
this immutability yang is that --- behind the scenes --- Clojure
shares data structures.  It keeps track of all their pieces and
re-uses them pervasively. For example, if you have a 1,000,000-item
list and want to tack on one more item, you just tell Clojure, "give
me a new one but with this item added" --- and Clojure dutifully gives
you back a 1,000,001-item list in no time flat. Unbeknownced to you
it's re-using the original list.

Clojure data structures are said to be *persistent*.

And, again: this works just fine because to you the data structures
are all immutable. There is no "action at a distance". Other functions
can't change the value of a data structure you're working on because
values don't change.

> Note that, of course, Clojure doesn't do any unnecessary copying.
> For example, when you pass a large data structure to a function
>
>     (my-func a-really-big-data-structure)
>
> it merely passes along a reference to the big data structure. You
> can't change it in the caller's scope, because of course it's
> immutable.
>
> And of course, you don't get any action-at-a-distance in situations
> like this either:
>
>     (def a [1 2 3 4 5])
>     (def b a)
>     ;; Do what you will with `b`, ...
>     (my-func a)   ; but it won't affect `a`.
>
> since, regardless, you can't mutate the vector (neither via `b`
> *nor* `a`).

If you're wondering how the heck it's even possible to program at all
if you don't have "variables" and can't change anything, it will
become clear as we continue.



## Control Structures

Clojure has most of the usual control structures you'd expect to find,
for example: `if`, `and`, `or`, and `cond`.  You can find them listed
in the [Cheatsheet](http://clojure.org/cheatsheet).

Note that they are all *expressions* in Clojure, and evaluate to
something.  So, for example, this `if` expression:

``` clojure
(if motor-turning?
  "yes"
  "no")
```

Evaluates to either the value "yes" or the value "no".

Looping is handled by either using one of the various built-in
functions such as `map`, `filter`, `reduce`, `for`, etc., or else it's
handled by manually using `loop` and using recursion. We'll get to
these shortly.

Incidentally, looping is something that is required far less in
Clojure than in imperative languages like Python and Java. The
functions that Clojure provides often makes looping unnecessary.
For example, where in Python you might do something like this:

``` python
specific_stuff = []
for i in my_items:
    if is_what_i_want(i):
        specific_stuff.append(i)
```

in Clojure you lose the loop and it becomes:

``` clojure
(def specific-stuff (filter what-i-want? my-items))
```

This sort of thing comes up again and again, and we'll cover more
examples of it in the [Bread and Butter
functions](#bread_and_butter_functions) section.




## Truthiness

In "`(if <test> <then-this> <otherwise-this>)`" (and in `and`, `or`,
`cond`, etc. expressions), Clojure checks if the `<test>` evaluates to
something that looks either true or false. Clojure takes a very simple
approach here: `nil` and `false` are falsey; everything else is
truthy.

This means that zero, the empty string, and empty core data structures
are all true:

``` clojure
(if   0 :t :f)  ; ⇒ :t
(if  "" :t :f)  ; ⇒ :t
(if  [] :t :f)  ; ⇒ :t
(if  {} :t :f)  ; ⇒ :t
(if #{} :t :f)  ; ⇒ :t
```

If you want to check if one of those is *empty*, you could use the
`empty?` function, though, the docs recommend using this idiom:

``` clojure
(if (seq my-stuff)
  "still has stuff left"
  "all gone")
```



## Equality

You'll often check for equality using `=` (and likewise inequality
using `not=`), for example:

``` clojure
(if (= tries max-tries)
  "you're done"
  "keep going")
```

`=` recursively checks equality of nested data structures (and
considers lists and vectors containing the same values in the same
order as equal), for example:

``` clojure
(= {:a  [1 2 3] :b #{:x :y} :c {:foo 1 :bar 2}}
   {:a '(1 2 3) :b #{:y :x} :c {:bar 2 :foo 1}})
;; ⇒ true
```

There's also a double-equals function `==` that is more forgiving
across various types of numbers:

``` clojure
(= 4 4.0)
;; ⇒ false
(== 4 4.0)
;; ⇒ true
```

See the docs for
[=](http://clojuredocs.org/clojure_core/clojure.core/=) and
[==](http://clojuredocs.org/clojure_core/clojure.core/==) for more
info.



## Predicates and Comparators

*Predicates* are functions that take one or more arguments and return
a true or false value. They usually are named with a trailing question
mark, for example, `even?`, `odd?`, `nil?`, etc. Though, some names
don't have the question mark, such as `>`, `>=`, `<`, `<=`, `=`, `==`,
and `not=`.

*Comparators* are functions that take 2 args and return -1, 0, or 1
depending upon whether the first arg is less than, equal to, or
greater than the second arg. The main one is `compare`.




## Vars

Near the top of this tutorial is the following definition:

``` clojure
(def the-answer 42)
```

The thing being defined here (behind the scenes) is officially called
a *Var*. The symbol "`the-answer`" refers to that var which itself
refers to the value 42:

the-answer (a symbol) → a var → 42 (a value).

When Clojure sees "`the-answer`", it automatically looks up the var,
then from there finds and returns the value 42.

Recall that locals (discussed earlier) don't involve vars at all:
those symbols refer directly to their values.




## Functions: Defining Your Own

You can create a function using `fn`, and give it a name using `def`:

``` clojure
(def my-func
  (fn [a b]
    (println "adding them!")
    (+ a b)))
```

As you might guess, this actually creates the symbol `my-func` which
refers to a var which itself refers to the function (which is a
value). Call it:

``` clojure
(my-func 10 20)   ; Returns/evaluates-to 30.
```

But for creating top-level functions, it's more convenient to use
`defn` (which uses `def` under the hood):

``` clojure
(defn my-func
  "Docstring goes here."
  [a b]
  (println "adding them!")
  (+ a b))
```

A few points to note:

  * The function parameters (`a` and `b`) are present in a vector
    (just like with the `let` expression, except we don't include
    values for them).
  * Inside `my-func` you can do a sequence of operations if you like
    (for example, our `println` call) --- just like in a `let` --- but
    the value of the last expression is what the function call as a
    whole will evaluate to.
  * Function definitions (using `defn`) should only go at the
    "top-level".

Functions can return data structures instead of just scalars:

``` clojure
(defn foo
  [x]
  [x (+ x 2) (* x 2)])
```

and you can of course pass them data structures as well:

``` clojure
(defn bar
  [x]
  (println x))

(bar {:a 1 :b 2})
(bar [1 2 3])
```

To define a function to take, say, two or more arguments:

``` clojure
(defn baz
  [a b & the-rest]
  (println a)
  (println b)
  (println the-rest))
```

Any additional args you pass beyond the first two get packaged into a
sequence assigned to `the-rest`. To have that function take *zero* or
more arguments, change the parameter vector to just `[& the-rest]`.



### Layout of Functions

Your author likes to write his functions in a top-down fashion:

``` clojure
;; BROKEN pseudocode

(do-it)

(defn do-it
  []
  (... (my-func-a ...)))

(defn my-func-a
  [...]
  (... (my-func-b ...)))

(defn my-func-b ...)
```

but Clojure doesn't like that because it wants to have at least
*heard* about a function before you write a call to it. To let Clojure
know about a function's existence, use `declare`:

``` clojure
;; pseudocode

(do-it)

(declare my-func-a)

(defn do-it
  []
  (... (my-func-a ...)))

(declare my-func-b)

(defn my-func-a
  [...]
  (... (my-func-b ...)))

(defn my-func-b ...)
```


## Side-effects

Some expressions in Clojure have side-effects. Many do not. All
expressions evaluate to something.

For example, `(+ 1 2)` evaluates to 3 and has no side-effects.
`(println "hi")` evaluates to nil and has the side-effect of printing
"hi" to standard out. You usually call `println` for the side-effect,
not for the return value.

Pure functions are those which have no side-effects and which do not
depend upon anything outside to compute their return value(s): you
pass it one or more values, and it returns one or more values.

If you want to make an expression that has some side-effects before
it evaluates to a value, use `do`:

``` clojure
(do
  (println "Spinning up warp drive, captain ...")
  (spin-up-warp-drive)
  (get-engine-temperature))
```

There are a handful of functions/macros/special-forms in Clojure for
making use of side-effects, and they are spelled with a "do" at the
beginning. Try these on for size:

``` clojure
(def my-items ["shirt" "coat" "hat"])

(doseq [i my-items]
  (println i))

(dotimes [i 10]
  (println "counting:" i))
```

There's also `dorun` and `doall`, both of which are discussed below in
the section on [Laziness](#laziness).

We say that `let` expressions and function bodies (and also `loop`
expressions, which you'll read about later in [Looping and
Recursion](#looping_and_recursion)) have an "implicit do": within them
you can list expressions one after another, and they all get evaluated
in order, but the last one is what determines the overall resulting
value of the overall expression.

There's a version of `if` which supports no "else" expression and
which provides an "implicit do". It's spelled "`when`" (and likewise
with `if-not` ↔ `when-not`).




## Destructuring

Clojure provides a little bit of extra syntactic support for assigning
values to locals in `let` expressions and function definitions.  Using
`let` as an example, suppose you have a nested data structure, and
you'd like to assign some values in it to locals. Where you *could* do
this:

``` clojure
(def games [:chess :checkers :backgammon :cards])

(let [game-a (games 0)
      game-b (games 1)
      game-c (games 2)
      game-d (games 3)]
  ...
  ...)
```

Destructuring allows you to instead write:

``` clojure
(let [[game-a game-b game-c game-d] games]
  ...
  ...)
```

The thing to the left of "games" in the binding vector is referred to
as the "binding form". In the above case, the binding form is a
vector.

The way it works is: if the binding form is a vector, Clojure assumes
that the thing you're trying to assign to it must also be a vector,
and so it unpacks the values from that data structure into the
corresponding items listed in the binding form.

If you want to omit one or more of the values in the `games`, you
can do so like this:

``` clojure
(let [[_ my-game _ your-game] games]
  ...
  ...)
```

The underscore is just used as a placeholder. It's a valid identifier,
but conventionally used when you don't care what value it gets. Above,
my-game gets :checkers and your-game gets :cards.

Destructuring also works for maps in additon to vectors. For example,
instead of:

``` clojure
(def concert {:band     "The Blues Brothers"
              :location "Palace Hotel Ballroom"
              :promos   "Ladies night, tonight"
              :perks    "Free parking"}

(let [band     (concert :band)
      location (concert :location)
      promos   (concert :promos)
      perks    (concert :perks)]
  ...
  ...)
```

you *could* do:

``` clojure
(let [{band     :band
       location :location
       promos   :promos
       perks    :perks} concert]
  ...
  ...)
```

but an even better shortcut that destructuring provides for that is:

``` clojure
(let [{:keys [band location promos perks]} concert]
  ...
  ...)
```


## Laziness

Most of the sequences Clojure creates (via calls to `map`, `reduce`,
`filter`, `for`, etc. --- covered in the next section) are *lazy*. A
lazy sequence is one that isn't *realized* (computed) all at
once. Instead, its values are only realized when you ask for them. If
you've only asked for the first 5 values of a lazy seq, then that seq
consists of 5 values plus a box that makes more values only when you
ask for them. .

A nice feature of laziness is that you can create lazy infinite
sequences but only realize (and consume memory for) the first *n* that
you actually need.

Be aware that the repl causes lazy lists to be fully realized if you
ask to see their value (which one is apt to do). After using the repl
for a while, you start to get a false sense of eagerness. `;)`

If you've got some code that generates a lazy seq and you want to realize
the whole thing right then and there, you can either use

  * `(doall my-lazy-seq)` (to get the whole thing), or else
  * `(dorun my-lazy-seq)` (to realize each value (presumably for some
    side-effects you're expecting to get in the process) but then
    forget it as you proceed to realize the next one).




## Bread and Butter Functions

Given Clojure's extensive use of immutability, persistent data
structures, and laziness, one of its strong suits is functional
programming. To this author, functional programming means:

  * treating functions just like any other regular value (for example,
    passing them as args to other functions)
  * writing and using functions that return other functions
  * avoiding mutable state, preferring instead Clojure's functional
    alternatives (`map`, `filter`, `reduce`, etc.) or else just
    directly using recursion.

Let's try out some of the power tools that Clojure comes with. In the
subsections that follow, we've left out the corresponding links to
clojuredocs for the given functions, but you'll probably want to read
the docs and see the examples there to get the full story for each.


### map

With `map` you can apply a function to every value in a collection.
The result is a new collection. You can often use `map` instead of
manually looping over a collection. Some examples using `map`:

``` clojure
(map inc [10 20 30])     ; ⇒ (11 21 31)
(map str [10 20 30])     ; ⇒ ("10" "20" "30")
;; You can define the function to be used on-the-fly:
(map (fn [x] (str "=" x "=")) [10 20 30])
;; ⇒ ("=10=" "=20=" "=30=")

;; And `map` knows how to apply the function you give it
;; to mulitple collections in a coordinated way:
(map (fn [x y] (str x y)) [:a :b :c] [1 2 3])
;; ⇒ (":a1" ":b2" ":c3")
```

When working on more than one collection at a time, `map` is smart
enough to stop when the shorter of the colls runs out of items:

``` clojure
(map (fn [x y] (str x y)) [:a :b :c] [1 2 3 4 5 6 7])
;; ⇒ (":a1" ":b2" ":c3")
```


### filter and remove

Use `filter` with a predicate function to pare down a collection to
just the values for which `(the-pred the-value)` returns true:

``` clojure
(filter odd? (range 10))
;; ⇒ (1 3 5 7 9)
```

Use `remove` for the opposite effect (which amounts to *removing* the
items for which `(pred val)` returns true):

``` clojure
(remove odd? (range 10))
;; ⇒ (0 2 4 6 8)
```

You will often find yourself using these functions instead
of writing loops like in imperative languages.



### apply

`apply` is for when you have a function which takes individual args,
for example, `max`, but the values you'd like to pass to it are in a
collection. `apply` "unpacks" the items in the coll:

``` clojure
(max 1 5 2 8 3)
;; ⇒ 8
(max [1 5 2 8 3]) ;; ERROR
(apply max [1 5 2 8 3])
;; ⇒ 8
```

A nice feature of `apply` is that you can supply extra args which
you'd like to be treated as if they were part of the collection:

``` clojure
(apply max 4 55 [1 5 2 8 3])
;; ⇒ 55
```


### for

`for` is for generating collections from scratch (again, without
needing to resort to manually looping). `for` is similar to Python's
"list comprehensions". Some examples of using `for`:

``` clojure
(for [i (range 10)] i)
;; ⇒ (0 1 2 3 4 5 6 7 8 9)
(for [i (range 10)] (* i i))
;; ⇒ (0 1 4 9 16 25 36 49 64 81)
(for [i (range 10) :when (odd? i)] [i (str "<" i ">")])
;; ⇒ ([1 "<1>"] [3 "<3>"] [5 "<5>"] [7 "<7>"] [9 "<9>"])
```

Notice we snuck a "`:when (odd? i)`" in there. `for` even supports a
`:let` modifier in there to set up your values before getting to the
body of the `for` expression.



### reduce

`reduce` is a gem. You use it to apply a function to the first and
second items in a coll and get a result. Then you apply it to the
result you just got and the 3rd item in the coll. Then the result of
*that* and the 4th. And so on.  The process looks something like this:

``` clojure
(reduce + [1 2 3 4 5])
;; → 1 + 2   [3 4 5]
;; → 3       [3 4 5]
;; → 3 + 3   [4 5]
;; → 6       [4 5]
;; → 6 + 4   [5]
;; → 10      [5]
;; → 10 + 5
;; ⇒  15
```

And, of course, you can supply your own function if you like:

``` clojure
(reduce (fn [x y] ...) [...])
```

A nice additional feature of `reduce` is that you can supply a value
for it to start off with:

``` clojure
(reduce + 10 [1 2 3 4 5])
;; ⇒ 25
```

This by itself is pretty handy. But it gets even better. Since you can
supply an initial argument, and you can supply your own function, you
can use a *data structure* as that initial argument and have your
function "build it up" as you go. For example:

``` clojure
(reduce (fn [accum x]
          (assoc accum
                 (keyword x)
                 (str x \- (rand-int 100))))
        {}
        ["hi" "hello" "bye"])

;; → {}
;; → {:hi "hi-29"}
;; → {:hi "hi-29" :hello "hello-42"}
;; ⇒  {:hi "hi-29" :hello "hello-42" :bye "bye-10"}
```

Building up some accumulator using `reduce` and your own custom
function is a fairly common pattern (and once again allows us to
avoid looping and manipulations of anything mutable).



### partial, comp, and iterate

With `partial` you can create a function which wraps another one and
passes it some standard arguments every time, along with the ones you
supply right when you call it. For example:

``` clojure
(defn lots-of-args [a b c d] (str/join "-" [a b c d]))
;; ⇒ #'user/lots-of-args
(lots-of-args 10 20 30 40)
;; ⇒ "10-20-30-40"
(def fewer-args (partial lots-of-args 10 20 30))
;; ⇒ #'user/fewer-args
(fewer-args 40)
;; ⇒ "10-20-30-40"
(fewer-args 99)
;; ⇒ "10-20-30-99"
```

`comp` is for composing a function from other ones. That is, `(comp
foo bar baz)` gives you a function that will first call baz on
whatever you pass it, then bar on the result of that, then foo on the
result of *that*, and finally returns the result.  Here's a silly
example:

``` clojure
(defn wrap-in-stars  [s] (str "*" s "*"))
(defn wrap-in-equals [s] (str "=" s "="))
(defn wrap-in-ats    [s] (str "@" s "@"))

(def wrap-it (comp wrap-in-ats
                   wrap-in-equals
                   wrap-in-stars))

(wrap-it "hi")
;; ⇒ "@=*hi*=@"
;; Which is the same as:
(wrap-in-ats (wrap-in-equals (wrap-in-stars "hi")))
;; ⇒ "@=*hi*=@"
```

`(iterate foo x)` yields an infinite lazy list consisting
of:

``` clojure
(x
 (foo x)
 (foo (foo x))
 (foo (foo (foo x)))
 ...)
```

To just take the first, say, 5 values from an infinite list, try this:

``` clojure
(defn square [x] (* x x))
(take 5 (iterate square 2))
;; ⇒ (2 4 16 256 65536)
```




## Looping and Recursion

As you've seen in the previous section, looping is often just handled
by various built-in functions such as `map`, `filter`, and `reduce`.
You should use those whenever you can. For times when you need more
manual control, you can write loops yourself. By-hand.

A `loop` expression looks like a `let`; you set up locals in its
binding vector, then the body of the loop is executed. The body has an
implicit do, just like `let` and function bodies. However, within the
body of the `loop` expression you exit at some point with what you
have or else loop again. When you loop again, you call the loop (using
`recur`) as if it's a function, passing new values in for the ones you
previously set up in the binding vector. The loop calling itself like
this is called *recursion*. Here's a trivial example:

``` clojure
(loop [accum []
       i     1]
  (if (= i 10)
    accum
    (recur (conj accum i)
           (inc i))))
;; ⇒ [1 2 3 4 5 6 7 8 9]
```

The state in this loop is carried in the `accum` vector, which we
update each time through the loop. `i` is the counter, and we finally
exit the loop (which evaluates to `accum`) when i equals 10.

`accum` could be any other data structure, and that call `(conj accum
i)` could be any expression that yields a new data structure to take
the old one's place the next time through.

You don't actually need a `loop` to use `recur`. If you use `recur` in
a function body, it will just call the function again, replacing the
args it was previously called with with the ones you pass to `recur`.

Finally, recall that if you just need looping for the side-effects
only, see `doseq` and `dotimes`.



## Reference Types

Although we've been saying all along that Clojure doesn't have
"variables", and that everything is immutable, ... that's not entirely
true.

For when you really do need mutability, Clojure offers *reference
types*. And Clojure provides built-in support for helping you mutate
them in safe ways.

Aside from vars (which is a sort of special reference type), there are
3 kinds of reference types:

  * Atoms
  * Refs
  * Agents

You might typically create a reference type like this:

``` clojure
(def my-atom (atom {}))
```

This reference type is an atom, and its state is a hashmap (an empty
one, for now). Here, the `my-atom` symbol refers to a var which refers
to the atom.

Although you *still* can't literally change the value of the atom, you
*can* swap in a new hashmap value for it any time you like. To retrieve
the value of the atom, you "deref" it, or just use the shorter "@"
syntax. Here's an (atom-specific) example:

``` clojure
(def my-atom (atom {:foo 1}))
;; ⇒ #'user/my-atom
@my-atom
;; ⇒ {:foo 1}
(swap! my-atom update-in [:foo] inc)
;; ⇒ {:foo 2}
@my-atom
;; ⇒ {:foo 2}
```

... and we've just changed the state of the atom. (Note, `swap!` is a
function used only for atoms. There are other specific functions for
working with the other reference types.)

The point of having reference types is that, in your programs, you may
want to represent an *identity*. An identity is something that may
change its state over time, but is still the same entity,
regardless. In Clojure, an identity is represented by a reference
type, and its state is represented by a value.

We won't discuss reference types further in this tutorial. Perhaps
someone will write a good topical guide...



## Not Covered In This Tutorial

To keep this tutorial down to a manageable length, advanced topics or
other far (or not so far) corners not covered herein include but
aren't limited to: function literals, multiple-arity functions,
exceptions, dynamic scoping of vars, namespaced keywords, metadata,
any substantial coverage of macros, transients, zippers, delays,
futures, promises, refs, agents or anything about multithreading,
thread-first, thread-last, trampolines, datatypes, protocols,
multimethods, and Java interop.



## Contributors

John Gabriele <jmg3000@gmail.com>, 2012 (original author)
