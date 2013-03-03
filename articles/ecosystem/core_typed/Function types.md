- Function types are quite different from Typed Racket
  - anything can implement the `IFn` interface
  - In core.typed the `Function` type is more a special type than the type for lambdas

Common things that are `IFn`s:
```clojure
- clojure.lang.Function
- c.l.Keyword
  - [Any -> Any]
- (c.l.PersistentHashMap k v)
  - (All [x] 
      (Fn [Any -> (U nil v)]
          [Any x -> (U x v)]))
- (c.l.PersistentHashSet v)
  - (All [x]
      (Fn [Any -> (U nil v)]))
- c.l.Symbol
  - [Any -> Any]
- (Value :a)
  - (All [x] 
      [Any -> x :filters {:then (is {:a x} 0)}])
- (Value sym)
  - (All [x] [Any -> (U nil v)])
```

The `IFn` class might be parameterised by a `Function` type.
The immediate problem is intersections allows us to have more
than one function type.

eg. What function type is this?

```clojure
(I (Value :a)
   (All [x] 
     [Any -> x :filters {:then (is {:a x} 0)}])
```

Even `(Value :a)` inherits two function types:
- that for c.l.Keyword
- that for `(Value :a)`

`(Value :a) <: (IFn x)` infers `x` to be:

```clojure
(I [Any -> Any]
   (All [x] 
     [Any -> x :filters {:then (is {:a x} 0)}])
```

The second member of the intersection is more specific,
thus can be simplified to:

```clojure
(All [x] 
  [Any -> x :filters {:then (is {:a x} 0)}])
```

Does this work in general? As long as there is a subtyping relationship
between the possible `Function` types, we can infer the most useful
one.
