---
title: "core.typed Quick Guide"
layout: article
---

## Design choices

### All vars must have annotated static types

Use `clojure.core.typed/ann` to assign types to vars

eg. Assign `my-fn` in the current namespace the type `[Any -> Any]` (a function of one argument).

```clojure
(ann my-fn [Any -> Any])
```

### Type checking is separate to compilation and must be explicitly run

Use `clojure.core.typed/check-ns` to type check the current namespace.
This can be done at the REPL.

Note: Global annotations like `ann` are only valid when found in a namespace currently being
checked with `check-ns`, or wrapped in a `cf`. A raw `ann` in a REPL has *no effect*.
Global annotations should be top-level forms or inside a (possibly nested) top-level `do`.

### All function arguments need to be annotated, or default to `Any`

Use `clojure.core.typed/ann-form` to annotate a function.

eg.

```clojure
(ann-form #(+ 1 %) [Number -> Number])
```

### Everything is type checked, but core.typed can ignore certain expressions

core.typed is early in development and there are Clojure idioms it cannot
currently type check. Wrap top-level expressions in `clojure.core.typed/tc-ignore`
to ignore them.

Suggestion: If porting a namespace to core.typed, initially use `tc-ignore` liberally to ignore problematic
code while determining the types for expressions. Once most vars are annotated, revisit
these sites to determine the issue.

## Debugging

### `print-env` is your friend

`clojure.core.typed/print-env` takes a debug string and prints the local type environment at the current expression.

### Use `cf` to experiment at the REPL

`clojure.core.typed/cf` takes an expression and optionally an expected type and type checks the expression,
returning its inferred type.

eg.

```clojure
clojure.core.typed=> (cf (fn [a]
                           {:pre [(number? a)]}
                           (inc a)))
[(Fn [Any -> java.lang.Number]) {:then tt, :else ff}]
```

If `cf` returns a vector of results, the first element is the static type.

### Use `ann-form` to ensure expressions are particular types

`clojure.core.typed/ann-form` can be used as a kind of static `assert`.

```clojure
clojure.core.typed=> (cf (let [a (+ 1 2)
                               _ (ann-form a clojure.lang.Symbol)]
                           a))
#<AssertionError java.lang.AssertionError: Assert failed: 6: Local binding a expected type clojure.lang.Symbol, but actual type clojure.core.typed/AnyInteger
(or (not expected) (subtype? t (ret-t expected)))>
```

### core.typed understands assertions and conditionals

Normal "untyped"  Clojure code often use type predicates combined with assertions or conditionals to direct control flow.
core.typed uses them to gain type information about the current environment.

```clojure
(let [a (ann-form 1 Number)
      _ (print-env "before assert")
      _ (assert (integer? a))
      _ (print-env "after assert")])
; "before assert"{:env {a java.lang.Number}, 
;                 :props ()}
; "after assert"{:env {_28338 nil, _ nil, a clojure.core.typed/AnyInteger}, 
;                :props ((is clojure.core.typed/AnyInteger a) (when (! (U false nil) _) ff) (when (! (U false nil) _) ff) (when (! (U false nil) _28338) ff))}
```

The `:env` map is maps local bindings to their current types.
`:props` is a list of propositions currently in scope (can usually be ignored, mostly useful for internal debugging purposes).

Notice the local binding `a` has a more accurate type after the `assert` expression.

Note: core.typed operates on a hygienic AST, so shadowed bindings will have gensymed names.

## Typing core constructs

### core.typed understands datatype definitions

Use `clojure.core.typed/ann-datatype` to give a datatype an expected type.

### Use `defprotocol>` instead of `defprotocol`

core.typed currently cannot understand protocol definitions. Simply replace references to `defprotocol`
with `clojure.core.typed/defprotocol>`

### core.typed understands simple multimethods

core.typed can infer accurate types for multimethods that dispatch on simple things like keywords or `class`.
Just assign an expected type to the multimethod's var with `ann` and core.typed will use it to infer accurate
types in each `defmethod`.

If in doubt whether a multimethod is being inferred properly, use the debugging techniques to double check.
core.typed may not throw an exception if the dispatch is too complex to type check currently.

### Macros & Macro Definitions

Macro definitions are ignored. The type checker operates on the macroexpanded form from
the Compiler's analysis phase.

## Type Syntax

### Types use the current global scope of the namespace

Simply adding an `(:import ...)` to the `ns` declaration as usual in Clojure brings the class name into scope.
Otherwise, refers to classes via their fully qualified name.
