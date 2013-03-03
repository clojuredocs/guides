---
title: "Filters"
layout: article
---

core.typed includes an implementation of occurrence typing, which helps the type
checker refine types according to control flow.

Occurrence typing helps core.typed infer a very accurate type for this expression
by recognising the semantics of predicates like `symbol?` and `number?`.

```clojure
clojure.core.typed=> (cf (let [a (ann-form 1 Any)]
                           (cond
                             (symbol? a) a
                             (number? a) a)))
(U clojure.lang.Symbol java.lang.Number nil)
```

## Filters

core.typed collects more information than just types for each expression. 

A structure called a filter set is also inferred. A filter set is a collection
of two filters:

- a filter that is true is the expression is a true value, called the `then` filter
- a filter that is true is the expression is a false value, called the `else` filter

### Trivial Filters

There are two trivial filters:

- `tt`, the trivially true filter
- `ff`, the impossible filter

We can use `cf` to check the filters of expressions.

```clojure
clojure.core.typed=> (cf 1)
[(Value 1) {:then tt, :else ff}]
```

The second place of the result vector is the filter set inferred for the expression.
`{:then tt, :else ff}` reads: the expression could be a true value, but it is impossible
for it to be a false value. This of course aligns with the semantics of numbers in Clojure.

False values are never true:

```clojure
clojure.core.typed=> (cf nil)
[nil {:then ff, :else tt}]
```

### Positive and Negative Type Filters

Filters can hold information relating bindings to types.

A positive type filter refines a local binding to be a type.

This filter says that the local binding `a` is of type `Number`.

```clojure
(is Number a)
```

A negative type filter refines a local binding to *not* be a type.

This filter says that the local binding `a` is *not* of type `Number`.

```clojure
(! Number a)
```

### Latent Filters

Filters almost never need to be written directly in normal code. *Latent* filters
however are very useful, and provide the most useful information to core.typed.

A *latent filter set* is a filter set attached to a function type. It is latent 
because it is not used directly: instead when a function with a latent filter set
is called, the filter set is instantiated in a way that makes sense in the current
context before it is used like a normal filter.

### Predicates

A very common place for a latent filters are in the types for predicates.

The type for `symbol?`, is

```clojure
[Any -> Boolean :filters {:then (is Symbol 0), :else (! Symbol 0)}]
```

First, notice that latent type predicates can also take an integer as an identifier.
The `0` represents the first argument of the function the latent filter set is attached to.

So the latent `then` filter `(is Symbol 0)` says the first argument to `symbol?` is of type `Symbol`
if the whole expression is a true value. To retrieve a non-latent filter, the `0` is instantiated to 
the appropriate local binding.

```
Note: Use `clojure.core.typed/print-filterset` to print the filter set of an expression.
```

```clojure
clojure.core.typed=> (cf (let [a (ann-form 1 Any)]
                           (print-filterset "symbol filters" 
                             (symbol? a))))
"symbol filters"
{:then (is clojure.lang.Symbol a), :else (! clojure.lang.Symbol a)}
empty-object
Flow tt
boolean
```

By printing the filter set of `(symbol? a)` we can see this in work, which
has a non-latent filter set of `{:then (is clojure.lang.Symbol a), :else (! clojure.lang.Symbol a)}`.

### Paths and Objects

TODO
