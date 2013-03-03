# Usage

## Types

[[Types]]

## Anonymous Functions

`clojure.core.typed/fn>` defines a typed anonymous function.

```clojure
eg. (fn [a b] (+ a b))
=>
(fn> [[a :- Number]
       [b :- Number]]
   (+ a b))
```

## Annotating vars

`clojure.core.typed/ann` annotates vars. Var does not have to exist at usage.

If definition isn't type checked, it is assumed correct anyway for checking usages.

All used vars must be annotated when type checking.

## Annotating datatypes

`clojure.core.typed/ann-datatype` annotates datatypes. 

Takes a name and a vector of fieldname/type type entries.

```clojure
(ann-datatype Pair [[lhs :- Term]
                    [rhs :- Term]])

(deftype Pair [lhs rhs]
  ...)
```

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

## Type Aliases

`clojure.core.typed/def-alias` defines a type alias.

```clojure
(def-alias Term (I IUnifyTerms 
                   IUnifyWithNil
                   IUnifyWithObject
                   IUnifyWithLVar
                   IUnifyWithSequential
                   IUnifyWithMap
                   IUnifyWithSet
                   IReifyTerm
                   IWalkTerm
                   IOccursCheckTerm
                   IBuildTerm))
```

## Ignoring code

`clojure.core.typed/tc-ignore` tells the type checker to ignore any forms in the body.

```clojure
(tc-ignore
(defprotocol IUnifyTerms
  (unify-terms [u v s]))
)
```

## Primitive Java Arrays 

"Typed" arrays can be created with `into-array>`, which has a 2 and 3 arity version.

The correspondence between core.typed types and Java types is subtle here. Usually
`into-array>` accepts a core.typed type as its first argument, followed by a collection (as `clojure.core/into-array`).

```clojure
;; `int` is the primitive int in Java. This creates a primitive array.
(class (into-array> int [1]))
;=> [I

;; This is a Number array with nullable elements.
(class (into-array> (U nil Number) [1]))
;=> [Ljava.lang.Number;

;; This is a Number array with non-nullable elements.
;; Notice this generates the same type as before as Java does not distinguish
;; non-/nullable arrays. core.typed statically disallows nil to be added 
;; as an element from any Clojure it checks.
(class (into-array> Number [1]))
;=> [Ljava.lang.Number;

;; An array of nullable primitive ints does not make sense in Java,
;; so it is generalised to an array of Objects.
(class (into-array> (U nil int) [1]))
;=> [Ljava.lang.Object;

;; Unions are often generalised to Object
(class (into-array> (U clojure.lang.Symbol Number) [1]))
;=> [Ljava.lang.Object;
```

When more control is needed of the Java type, the 3 arity version of `into-array>` accepts
the Java type (in core.typed syntax) as first argument, followed by the Clojure type, and the collection.

```clojure
;; Generalising to Number instead of Object.
(class (into-array> Number (U Integer Number) [1]))
;=> [Ljava.lang.Number;
```

The Clojure element type should be a subtype to the Java element type.

## Declarations

`clojure.core.typed/declare-types`, `clojure.core.typed/declare-names` and `clojure.core.typed/declare-protocols` are similar
to `declare` in that they allow you to use types before they are defined.

```clojure
(declare-datatypes Substitutions)
(declare-protocols LVar)
(declare-names MyAlias)
```

## Checking typed namespaces

`clojure.core.typed/check-ns` checks the namespace that its symbol argument represents.

```clojure
(check-ns 'my.ns)
```

## Debugging

`clojure.core.typed/print-env` prints the current environment.

```clojure
(let [a 1]
  (print-env "Env:")
  a)
; Prints: "Env:" {:env {a (Value 1)},  ....}
```

`clojure.core.typed/cf` (pronounced "check form") can be used at the REPL to return the type of a form.

```clojure
(cf 1)
;=> [(Value 1) {:then [top-filter], :else [bot-filter]} empty-object]
```

## Macros & Macro Definitions

Macro definitions are ignored. The type checker operates on the macroexpanded form from
the Compiler's analysis phase.

