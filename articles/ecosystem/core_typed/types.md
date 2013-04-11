---
title: "core.typed Types"
layout: article
---

## Common types

### Any and Nothing

Every type is a subtype to `Any`, written `x <: Any` for all types `x`. Equivalently, any place that `Any` is valid,
any other type can be used. `Any` is also known as the "Top" type.

Conversely, there are no types that are subtypes to `Nothing`. However for all types `x`
it holds that `Nothing <: x`. Put another way, `Nothing` is a valid type to give use
in positions expecting any other type. In practice, `Nothing` is not as useful as `Any`,
and is usually used internally to detect dead code and other code properties.

### Functions

core.typed has a special function type, which is an *ordered intersection* of arities.
It allows us to specify fine grained function invariants.

Starting simply, 

```clojure
(Fn [Any -> Any])
``` 

is a function taking one argument of type `Any`. `[Any -> Any]`
is an equivalent shorthand for single-arity function types.

#### Multiple arities

We can specify multiple arities:

```clojure
(Fn [Any -> Any]
    [Any Any -> Any])
``` 

Here we can call a function of this type with either one or two arguments.
In this case, the ordered intersection type acts as a simple overloading on arity.

Finer invariants can be modelled by specifying multiple arities of the same arity:

```clojure
(Fn [Symbol -> Number]
    [Number -> Symbol])
```

This function returns a `Number` if passed a `Symbol`, and returns a `Symbol` if passed a `Number`.

The exact return type for a function application expression involving multiple arities
is chosen by matching the actual types provided with each arities, top-to-bottom 
(this explains why our functions are "ordered" intersections).
In this case, each arity is disjoint.

Specify overlapping arities hints at the power of ordered intersections.

```clojure
(Fn [Long -> Symbol]
    [Number -> Keyword])
```

This contrived example always returns a `Symbol` for `Long` arguments.

Beware, swapping the arities produces different results!

```clojure
(Fn [Number -> Keyword]
    [Long -> Symbol])
```

The first arity always "wins" because `Number` is strictly more general than `Long`.
Arities should be ordered from less-specific to more-specific.

Finally, a common idiom is to provide a base arity, which has arguments at least as general
as the ones above it.

For example, we might `(Fn [Long -> Symbol] [Number -> Keyword])` want to handle the case where
the argument is *either* a `Long` or a `Number`.
We achieve this by defining a least-upper-bound with a union type.

```clojure
(Fn [Long -> Symbol]
    [Number -> Keyword]
    [(U Long Number) -> (U Symbol Keyword)])
```

Note the result type is sufficiently general to show the result type is either a `Symbol` or `Keyword`.

#### Rest parameters

Rest parameters are specified using a `*`.

eg.

```clojure
(Fn [Any Number * -> Any])
``` 

is a function taking at least one parameter, and any number of parameters after it
of type `Number`.

#### Keyword parameters

Keyword parameters are specified using `&` after the fixed domain.

eg.

```clojure
(Fn [Any & {:a Number} -> Any])
``` 

is a function that takes a fixed parameter and an optional keyword argument `:a`, of 
type `Number`.

We can also specify mandatory keyword parameters:

```clojure
(Fn [Any & {} :mandatory {:a Number} -> Any])
``` 

is the same function, except the keyword argumetn `:a` now must be present when calling.

We can express finer grained invariants by combining keyword types and ordered
function intersection types:

```clojure
(Fn [Any & {} :mandatory {:a Number :b Number} -> Number]
    [Any & {:a Number :b Number} -> Any])
``` 

This function type returns a `Number` if provided both `:a` and `:b` parameters,
otherwise returns `Any` if some other combination of `:a` and `:b` is provided.

### Java Classes

core.typed reuses Java and clojure.lang.* classes. The normal scoping rules apply in types,
e.g., use `:import` to bring classes into scope.

Note: `java.lang.*` classes are implicitly in scope in Clojure namespaces.

### Numbers, Strings and other Java types

core.typed follows the normal rules that apply to Clojure code.

```clojure
clojure.core.typed=> (cf 1 Long)
java.lang.Long
clojure.core.typed=> (cf 1.1 Double)
java.lang.Double
clojure.core.typed=> (cf "a" String)
java.lang.String
clojure.core.typed=> (cf \a Character)
java.lang.Character
```

### Symbols and Keywords

Symbols and Keywords are instances of their corresponding clojure.lang classes.

```clojure
clojure.core.typed=> (cf 'a clojure.lang.Symbol)
clojure.lang.Symbol
clojure.core.typed=> (cf :a clojure.lang.Keyword)
clojure.lang.Keyword
```

### Seqables

Seqables extend `(Seqable a)`, which is covariant in its argument.
Types that extend `(Seqable a`) are capable of creating a sequence
(aka. an `(ISeq a)`)  representation of itself.

```clojure
clojure.core.typed=> (cf {'a 2 'b 3} (Seqable (IMapEntry Symbol Number)))
(clojure.lang.Seqable (clojure.lang.IMapEntry clojure.lang.Symbol java.lang.Number))
clojure.core.typed=> (cf [1 2 3] (Seqable Number))
(clojure.lang.Seqable java.lang.Number)
clojure.core.typed=> (cf '#{a b c} (Seqable Symbol))
```

### Seqs

Seqs extend `(IPersistentSeq a)`, which is covariant in its argument.

```clojure
clojure.core.typed=> (cf (seq [1 2]) (ISeq Number))
(clojure.lang.ISeq java.lang.Number)
```

### Lists

Lists extend `(IPersistentList a)`, which is covariant in its argument.

```clojure
clojure.core.typed=> (cf '(1 2) (IPersistentList Number))
(clojure.lang.IPersistentList java.lang.Number)
```

### Vectors

Vectors extend `(IPersistentVector a)`, which is covariant in its argument.

```clojure
clojure.core.typed=> (cf [1 2] (IPersistentVector Number))
(clojure.lang.IPersistentVector java.lang.Number)
```

### Maps

Maps extend `(IPersistentMap a b)`, which is covariant in both its arguments.

```clojure
clojure.core.typed=> (cf {'a 1 'b 3} (IPersistentMap Symbol Long))
(clojure.lang.IPersistentMap clojure.lang.Symbol java.lang.Long)
```

### Sets

Sets extend `(IPersistentSet a)`, which is covariant in its argument.

```clojure
clojure.core.typed=> (cf #{1 2 3} (IPersistentSet Number))
(clojure.lang.IPersistentSet java.lang.Number)
```

### Atoms

An Atom of type `(Atom w r)` can accept values of type `w` and provide values of type `r`.
It is contravariant in `w` and covariant in `r`.

Usually `w` and `r` are identical, so an alias `(clojure.core.typed/Atom1 wr)` is provided,
which is equivalent to `(Atom wr wr)`.

```clojure
clojure.core.typed=> (cf (atom {}) (Atom1 (IPersistentMap Symbol Number)))
(clojure.core.typed/Atom1 (clojure.lang.IPersistentMap clojure.lang.Symbol java.lang.Number))
```

## Type Grammar

A rough grammar for core.typed types.

```
Type :=  nil
     |   true
     |   false
     |   (U Type*)
     |   (I Type+)
     |   FunctionIntersection
     |   (Value CONSTANT-VALUE)
     |   (Rec [Symbol] Type)
     |   (All [Symbol+] Type)
     |   (All [Symbol* Symbol ...] Type)
     |   (HMap {Keyword Type*})        ;eg (HMap {:a (Value 1), :b nil})
     |   '{Keyword Type*}              ;eg '{:a (Value 1), :b nil}
     |   (Vector* Type*)
     |   '[Type*]
     |   (Seq* Type*)
     |   (List* Type*)
     |   Symbol  ;class/protocol/free resolvable in context

FunctionIntersection :=  ArityType
                     |   (Fn ArityType+)

ArityType :=   [FixedArgs -> Type]
           |   [FixedArgs RestArgs * -> Type]
           |   [FixedArgs DottedType ... Symbol -> Type]

FixedArgs := Type*
RestArgs := Type
DottedType := Type
```

## Types

### Value shorthands

`nil`, `true` and `false` resolve to the respective singleton types for those values

### Intersections

`(I Type+)` creates an intersection of types.


### Unions

`(U Type*)` creates a union of types.

### Functions

A function type is an ordered intersection of arity types.

There is a vector sugar for functions of one arity.

### Heterogeneous Maps

*Warning*: Heterogeneous maps are alpha and their design is subject to change.

A heterogeneous map type represents a map that has at least a particular set of keyword keys.

```clojure
clojure.core.typed=> (cf {:a 1})
[(HMap {:a (Value 1)}) {:then tt, :else ff}]
```
This type can also be written `'{:a (Value 1)}`.

Lookups of known keys infer accurate types.

```clojure
clojure.core.typed=> (cf (-> {:a 1} :a))
(Value 1)
```

Currently, they are limited (but still quite useful):
- the presence of keys is recorded, but not their absence
- only keyword value keys are allowed.

These rules have several implications.

#### Absent keys

Looking up keys that are not recorded as present give inaccurate types

```clojure
clojure.core.typed=> (cf (-> {:a 1} :b))
Any
```

#### Non-keyword keys

Literal maps without keyword keys are inferred as `APersistentMap`.

```clojure
clojure.core.typed=> (cf {(inc 1) 1})
[(clojure.lang.APersistentMap clojure.core.typed/AnyInteger (Value 1)) {:then tt, :else ff}]
```



Optional keys can be defined either by constructing a union of map types, or by passing
the `HMap` type constructor an `:optional` keyword argument with a map of optional keys.

### Heterogeneous Vectors

`(Vector* (Value 1) (Value 2))` is a IPersistentVector of length 2, essentially 
representing the value `[1 2]`. The type `'[(Value 1) (Value 2)]` is identical.

### Polymorphism

The binding form `All` introduces a number of free variables inside a scope.

Optionally scopes a dotted variable by adding `...` after the last symbol in the binder.

eg. The identity function: `(All [x] [x -> x])`
eg. Introducing dotted variables: `(All [x y ...] [x y ... y -> x])

### Recursive Types

`Rec` introduces a recursive type. It takes a vector of one symbol and a type.
The symbol is scoped to represent the entire type in the type argument.

```clojure
; Type for {:op :if
;           :test {:op :var, :var #'A}
;           :then {:op :nil}
;           :else {:op :false}}
(Rec [x] 
     (U (HMap {:op (Value :if)
               :test x
               :then x
               :else x})
        (HMap {:op (Value :var)
               :var clojure.lang.Var})
        (HMap {:op (Value :nil)})
        (HMap {:op (Value :false)})))))
```
