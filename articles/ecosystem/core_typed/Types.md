---
title: "core.typed Types"
layout: article
---

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
