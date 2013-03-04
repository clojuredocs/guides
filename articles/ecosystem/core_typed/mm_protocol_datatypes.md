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
              :methods
              {unify-with-lvar [Term LVar ISubstitutions -> (U ISubstitutions Fail)]})

(defprotocol> IUnifyWithLVar
  (unify-with-lvar [v u s]))
```


## Annotating datatypes

`clojure.core.typed/ann-datatype` annotates datatypes. 

Takes a name and a vector of fieldname/type type entries.

```clojure
(ann-datatype Pair [[lhs :- Term]
                    [rhs :- Term]])

(deftype Pair [lhs rhs]
  ...)
```
