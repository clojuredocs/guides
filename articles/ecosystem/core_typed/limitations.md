---
title: "core.typed User Documentation Home"
layout: article
---

## Namespace management

Typed dependencies NYI.

## Destructuring

Only map destructuring *without* options is supported.

Other forms of destructuring require equality filters.

## Shadowing bindings

If an argument is shadowed and the shadowed binding is referenced
in filters or object then the shadow is indistinguishable from the parameter
and parameter will be incorrectly abstracted.

eg.
```clojure
(fn [a]
  (if (= a 1)
    (let [a 'foo] ; here this shadows the argument, impossible to recover filters
      a)          ; in fact any new filters about a will be incorrectly assumed to be the argument
      false)) 
```

(See `abstract-result` in `typed/test.clj`)

## Dotted Functions

A dotted function contains a dotted variable in its function type.

eg. map's type: 
     `(All [c a b ...]
                [[a b ... b -> c] (U nil (Seqable a)) (U nil (Seqable b)) ... b -> (Seqable c)]))`

We can't currently check the definitions of functions with dotted rest arguments.

## Rest Arguments

Currently cannot check the definition of functions with rest arguments,
but usage checking should work.

## Using `filter`

Not everything can be inferred from a `filter`. A common example is
`(filter identity coll)` does not work. The reason is `identity` only
gives negative information when its result is true: that the argument is *not*
`(U nil false)`.

This idiom must be converted to this syntax `(fn [a] a)` and then annotated with
positive propositions.

```clojure
;eg. 

(filter (ann-form (fn [a] a)
                  [(U nil Number) -> (U nil Number) :filters {:then (is Number 0)}])
        [1 nil 2])
; :- (Seqable Number)
```

Positive information infers just fine, like `(filter number? coll)`.
The above idiom is useful when you are filtering something like a `(Seqable (U nil x))` and there is no
predicate to test for `x`, so you can only test if something isn't `nil`.

## Looping macros

Looping macros like `for`, `dotimes`, and `doseq` are currently not supported, but
are high priority for new features.
