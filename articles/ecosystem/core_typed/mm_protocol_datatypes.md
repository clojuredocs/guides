---
title: "core.typed User Documentation Home"
layout: article
---

## Annotating Protocols

`clojure.core.typed/ann-protocol` annotates protocols.

Takes a name and a optionally a :methods keyword argument mapping
method names to expected types.

Protocol definitions should use `clojure.core.typed/defprotocol>` (identical syntax to `defprotocol`).

```clojure
(ann-protocol IUnifyWithLVar
              unify-with-lvar [Term LVar ISubstitutions -> (U ISubstitutions Fail)])

(defprotocol> IUnifyWithLVar
  (unify-with-lvar [v u s]))
```

Each protocol method argument (including the first) is explicit in the type annotation.
Often, the the first argument (aka. `this`) will just be the protocol, but in some cases
it is convenient to add more general types.

## Annotating datatypes

`clojure.core.typed/ann-datatype` annotates datatypes. 

Takes a name and a vector of fieldname/type type entries.

```clojure
(ann-datatype Pair [lhs :- Term
                    rhs :- Term])

(deftype Pair [lhs rhs]
  ...)
```

Each protocol extended in `deftype` must have an annotated expected type with `ann-protocol`.

The types for Java interface method are inferred from their corresponding Java type.
