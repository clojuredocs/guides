---
title: "Looping constructs"
layout: article
---

Due to limitations in core.typed's inference, we require using "typed" versions
of several core forms.

# loop

Usages of `loop` should be replaced with `clojure.core.typed/loop>`.

The syntax is identical except each loop variable requires a type annotation.

```clojure
(loop> [[a :- Number] 1
        [b :- (U nil Number)] nil]
  ...)
```

# Named fn's

Named `fn`'s require full annotation for accurate recursive calls inside the `fn` body.

```clojure
clojure.core.typed=> (cf (ann-form (fn a [n] (+ (a 1) n))
                                   [Number -> Number]))
(Fn [java.lang.Number -> java.lang.Number])
```

# for

Use `clojure.core.typed/for>` instead of `for`. 

`for>` requires annotations for the return type of the body
of the for, and the left hand side of each binding form.

```clojure
(for> :- Number
      [[a :- (U nil AnyInteger)] [1 nil 2 3]
       :when a]
  (inc a))
```

# doseq

Use `clojure.core.typed/doseq>` instead of `doseq`. 

`doseq>` requires annotations for the left hand side of each binding form.

```clojure
(doseq> [[a :- (U nil AnyInteger)] [1 nil 2 3]
         :when a]
   (inc a))
```
