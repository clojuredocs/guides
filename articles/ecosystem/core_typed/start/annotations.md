---
title: "Annotations"
layout: article
---

core.typed requires a moderate amount of assistance from the user to help infer types.

There are two main things that need annotating:

1. All vars must be annotated
2. All function parameters must be annotated, or default to `Any`.

From the provided annotations, core.typed uses local type inference to infer
the types for local bindings, interop calls, and other expressions, mostly
without further assistance.

## Vars

When core.typed finds a var reference, `def`, `binding`, or some other var-related construct
that relys on the derefereced value of a var, it requires an expected type.

```clojure
clojure.core.typed=> (declare abc)
#'clojure.core.typed/abc
clojure.core.typed=> (cf abc)
#<AssertionError java.lang.AssertionError: Assert failed: Untyped var reference: clojure.core.typed/abc
(contains? (clojure.core/deref *var-annotations*) nsym)>
```

### Vars in current namespace

Use `clojure.core.typed/ann` to associate a static type with a var.

```clojure
clojure.core.typed=> (cf (ann abc Number))
[clojure.core.typed/abc java.lang.Number]
clojure.core.typed=> (cf (def abc 1))
clojure.lang.Var
clojure.core.typed=> (cf abc)
java.lang.Number
```

`ann` qualifies the var in the current namespace if unqualified.
 
### Vars in other namespaces

Sometimes vars from other namespaces need annotation. Just qualify the var as you
would in the current namespace (aliases are recognised) to associate it with a static type.

```clojure
clojure.core.typed=> (cf clojure.core/*compile-path*)
#<AssertionError java.lang.AssertionError: Assert failed: Untyped var reference: clojure.core/*compile-path*
(contains? (clojure.core/deref *var-annotations*) nsym)>
clojure.core.typed=> (cf (ann clojure.core/*compile-path* String))
[clojure.core/*compile-path* java.lang.String]
clojure.core.typed=> (cf clojure.core/*compile-path*)
java.lang.String
```

### Unchecked Vars

We can instruct core.typed to ignore certain var definitions by adding `:nocheck` metadata
to `ann` forms.

```clojure
(ns typed.nocheck
  (:require [clojure.core.typed :refer [ann-nocheck ann check-ns]]))

(ann ^:nocheck foo [Number -> Number])
(defn foo [a]
  'a)

(ann bar [Number -> Number])
(defn bar [b]
  (+ 2 (foo b)))
```

### Var Warnings

After type checking has been performed, core.typed warns about vars that have been assigned types
but have no corresponding checked `def` form. The `def` must at least make a binding,
so it would be a warning if the var was only `declare`d.

```clojure
(ns clojure.core.typed.test.nocheck
  (:require [clojure.core.typed :refer [ann-nocheck ann check-ns]]))

(ann ^:nocheck foo [Number -> Number])
(defn foo [a]
  'a)

(ann bar [Number -> Number])
(defn bar [b]
  (+ 2 (foo b)))

;(check-ns)
; ...
; WARNING: Var clojure.core.typed.test.var-usage/foo used without checking definition
;=> nil
```

## Functions

There are several ways to annotate a function type.

### Partial annotation with `fn>`

To annotate just the arguments of a `fn`, use the `fn>` wrapper. It is exactly like `fn`,
except each argument is wrapped in a vector which includes its static type.

```clojure
clojure.core.typed=> (cf (fn> [[a :- Number]] (+ a 1)))
[(Fn [java.lang.Number -> java.lang.Number]) {:then tt, :else ff}]
```

All the usual destructuring is supported.

```clojure
clojure.core.typed=> (cf (fn> [[{:keys [a b c]} :- '{:a Number :b Long :c Double}]]
                           [a b c]))
[(Fn ['{:a java.lang.Number, :b java.lang.Long, :c java.lang.Double} -> '[java.lang.Number java.lang.Long java.lang.Double]]) 
 {:then tt, :else ff}]
```

### Full annotation with `ann-form`

Often it is more useful to provide a full function type as a `fn`'s annotation. This
especially works well with Clojure's anonymous function syntax.

```clojure
clojure.core.typed=> (cf (ann-form #(inc %)
                                   [Number -> Number]))
(Fn [java.lang.Number -> java.lang.Number])
```

This way, you can also assign anonymous functions ordered intersection function types.

```clojure
clojure.core.typed=> (cf (fn [a]
                           (cond
                             (number? a) 1
                             (symbol? a) 'a))
                         (Fn [Number -> Number]
                             [Symbol -> Symbol]))
(Fn [java.lang.Number -> java.lang.Number] 
    [clojure.lang.Symbol -> clojure.lang.Symbol])
```
