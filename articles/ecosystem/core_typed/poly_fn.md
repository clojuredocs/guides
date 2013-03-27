---
title: "Polymorphic Functions"
layout: article
---

core.typed supports polymorphic function types. They allow us to specify
function types which are both general and accurate.

# All

The primitive `All` constructor creates a polymorphic binder and scopes
type variables in a type.

The identity function has a simple polymorphic type:

```clojure
(All [x]
  [x -> x])
```

Read: for all types `x`, a function that takes an `x` and returns an `x`.

Polymorphic types are introduced with annotations, but where are they eliminated?
We use local type inference to infer type variable types based on how they are used.

```clojure
(identity :a)
```

In the above example, we infer `x` to be `Keyword`, and instantiate the polymorphic
type as `[Keyword -> Keyword]`.

## Bounds

Type variables support upper and lower type bounds, which default to `Any` and `Nothing`
respectively.

Equivalently, the type:

```clojure
(All [x] ...)
```

is shorthand for:

```clojure
(All [[x :> Nothing :< Any]] ...)
```

We use bounds to ensure a type variable can only be instantiated to a particular type.

The type of an identity function that only accepts `Number`s can be written:

```clojure
(All [[x :< Number]]
  [x -> x])
```

Bounds do not seem as useful in core.typed as languages like Java or Scala.
Often, combinations of ordered function intersections and unions are more useful.

Bounds are also recursive: a bound can refer to the variable it's bounding.
Type variables to the left of the type variable being bounded in the same binder are in scope in a bound.

## Higher-kinded variables

Note: Experimental feature

A type variable can be of a higher-kind.

```clojure
(def-alias AnyMonad 
  (TFn [[m :kind (TFn [[x :variance :covariant]] Any)]]
    '{:m-bind (All [x y]
                [(m x) [x -> (m y)] -> (m y)])
      :m-result (All [x]
                  [x -> (m x)])
      :m-zero (U (All [x] (m x)) Undefined)
      :m-plus (U (All [x]
                   [(m x) * -> (m x)])
                 Undefined)}))
```

In this type, `x` is a type function taking a type and returning a type.
For those familiar with Haskell, `x` is of kind `* -> *`.

The type function is also covariant, which further ensures `x` is instantiated
to a covariant type function.
