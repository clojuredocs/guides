---
title: "Clojure namespaces and vars"
layout: article
---

## About this guide

This guide covers:

 * An overview of Clojure namespaces and vars
 * How to define namespaces
 * How to use functions in other namespaces
 * `require`, `refer` and `use`
 * Common compilation errors and typical problems that cause them
 * Namespaces and their relation to code compilation in Clojure

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).


## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.


## Overview

Clojure functions are organized into *namespaces*. Clojure namespaces
are very similar to Java packages and Python modules. Namespaces are
basically maps (dictionaries) that map names to *vars*. In many cases,
those vars store functions in them.


## Defining a Namespace

Namespaces are usually defined using the `clojure.core/ns` macro. In its basic
form, it takes a name as a symbol:

``` clojure
(ns superlib.core)
```

Namespaces can have multiple segments, separated by a dot:

``` clojure
(ns megacorp.service.core)
```

It is **highly recommended** to avoid using single segment namespaces
(e.g. `superlib`) to avoid inconvenient conflicts other developers
will have to work around. If a library or application belongs to an
organization or a group of projects, the
`[organization].[library|app].[group-of-functions]` pattern is
recommended. For example:

``` clojure
(ns clojurewerkz.welle.kv)

(ns megacorp.search.indexer.core)
```

In addition, the `ns` macro takes a number of optional forms:

 * `(:import ...)`
 * `(:require ...)`
 * `(:use ...)`
 * `(:refer-clojure ...)`
 * `(:gen-class ...)`

These are just slightly more concise variants of `clojure.core/import`, `clojure.core/require`, et cetera.

### The :import Helper Form

An example with `(:import ...)`:

``` clojure
(ns megacorp.profitd.scheduling
  (:import java.util.concurrent.Executors))
```

This will make sure the `java.util.concurrent.Executors` class is imported and can be used by its short
name, `Executors`. It is possible to import multiple classes:

``` clojure
(ns megacorp.profitd.scheduling
  (:import java.util.concurrent.Executors
           java.util.concurrent.TimeUnit
           java.util.Date))
```

If multiple imported classes are in the same namespace (like in the example above),
it is possible to avoid some duplication by using an *import list*. The first element
of an import list is the package and other elements are class names in that package:

``` clojure
(ns megacorp.profitd.scheduling
  (:import [java.util.concurrent Executors TimeUnit]
           java.util.Date))
```

Even though *import list* is called a list, it can be any Clojure collection (typically
vectors are used).


### The :require Helper Form

An example with `(:require ...)`:

``` clojure
(ns megacorp.profitd.scheduling
  (:require clojure.set))

;; now it is possible to do
;; (clojure.set/difference #{1 2 3} #{3 4 5})
```

This will make sure the `clojure.set` namespace is loaded, compiled and available as `clojure.set`
(using its fully qualified name). It is possible (and common) to make a namespace available
as a different alias:

``` clojure
(ns megacorp.profitd.scheduling
  (:require [clojure.set :as cs]))

;; now it is possible to do
;; (cs/difference #{1 2 3} #{3 4 5})
```

One more example with two required namespaces:

``` clojure
(ns megacorp.profitd.scheduling
  (:require [clojure.set  :as cs]
            [clojure.walk :as walk]))
```

To make functions in `clojure.set` available in the defined namespace via short names
(e.g. without the `clojure.set` or other prefix), you can tell Clojure compiler
to *refer* to certain functions:

``` clojure
(ns megacorp.profitd.scheduling
  (:require [clojure.set :refer [difference intersection]]))

;; now it is possible to do
;; (difference #{1 2 3} #{3 4 5})
```

The `:refer` feature of the `:require` form is new in Clojure 1.4.

It is possible to refer to all functions in a namespace (usually not necessary):

``` clojure
(ns megacorp.profitd.scheduling
  (:require [clojure.set :refer :all]))

;; now it is possible to do
;; (difference #{1 2 3} #{3 4 5})
```


### The Current Namespace

Under the hood, Clojure keeps **current namespace** a special var, [*ns*](http://clojuredocs.org/clojure_core/clojure.core/*ns*).
When vars are defined using the [def](http://clojuredocs.org/clojure_core/clojure.core/def) special form, they are
added to the current namespace.




### The :refer-clojure Helper Form

Functions like `clojure.core/get` and macros like `clojure.core/defn` can be used without
namespace qualification because they reside in the `clojure.core` namespace and Clojure
compiler automatically *refers* all vars in it. This leaders to a problem: if your
namespace defines a function with the same name (e.g. `find`), you will get a warning
from the compiler, like this:

```
WARNING: find already refers to: #'clojure.core/find in namespace: megacorp.profitd.scheduling, being replaced by: #'megacorp.profitd.scheduling/find
```

This means that in the `megacorp.profitd.scheduling` namespace, `find` already refers to
a value which happens to be `clojure.core/find`, but it is being replaced by a
different value. Remember, Clojure is a very dynamic language and namespaces are
basically maps, as far as the implementation goes. Most of the time, however,
replacing vars like this is not intentional and Clojure compiler emits a warning.

To solve this problem, you can exclude certain `clojure.core` functions from being
referred using the `(:refer-clojure ...)` form with the `ns`:

``` clojure
(ns megacorp.profitd.scheduling
  (:refer-clojure :exclude [find]))

(defn find
  "Finds a needle in the haystack"
  [^String haystack]
  (comment ...))
```

In this case, to use `clojure.core/find`, you will have to use its fully
qualified name: `clojure.core/find`:

``` clojure
(ns megacorp.profitd.scheduling
  (:refer-clojure :exclude [find]))

(defn find
  "Finds a needle in the haystack"
  [^String haystack]
  (clojure.core/find haystack :needle))
```


### The :use Helper Form

In Clojure versions before 1.4, there was no `:refer` support for the
`(:require ...)` form. Instead, a separate form was used: `(:use ...)`:

``` clojure
(ns megacorp.profitd.scheduling-test
  (:use clojure.test))
```

In the example above, **all** functions in `clojure.test` are made available
in the current namespace. This practice (known as *naked use*) works for `clojure.test` in
test namespaces, but in general not a good idea. `(:use ...)` supports limiting
functions that will be referred:

``` clojure
(ns megacorp.profitd.scheduling-test
  (:use clojure.test :only [deftest testing is]))
```

which is a pre-1.4 alternative of

``` clojure
(ns megacorp.profitd.scheduling-test
  (:require clojure.test :refer [deftest testing is]))
```

It is highly recommended to use `(:require ... :refer [...])` on Clojure 1.4
and later releases. `(:use ...)` is a thing of the past and now that
`(:require ...)` with `:refer` is capable of doing the same thing when you
need it, it is a good idea to let `(:use ...)` go.


### The :gen-class Helper Form

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


### Documentation and Metadata

Namespaces can have documentation strings. You can add one with the optional
`ns` macro parameter:

``` clojure
(ns superlib.core
  "Core functionality of Superlib.

   Other parts of Superlib depend on functions and macros in this namespace."
  (:require [clojure.set :refer [union difference]]))
```

or metadata:

``` clojure
(ns ^{:doc "Core functionality of Superlib.
            Other parts of Superlib depend on functions and macros in this namespace."
      :author "Joe Smith"}
   superlib.core
  (:require [clojure.set :refer [union difference]]))
```

metadata can contain any additional keys such as `:author` for various tools
(e.g. [Codox](https://clojars.org/codox), [Cadastre](https://clojars.org/cadastre) or [lein-clojuredocs](https://clojars.org/lein-clojuredocs)) to use.


## How to Use Functions From Other Namespaces in the REPL

The `ns` macro is how you usually require functions from other namespaces.
However, it is not very convenient in the REPL. For that case, `clojure.core/require`
can be used directly:

``` clojure
;; will be available as clojure.set, e.g. clojure.set/difference
(require 'clojure.set)

;; will be available as io, e.g. io/resource
(require '[clojure.java.io :as io])
```

It takes a quoted *libspec*. *libspec* is either a namespace name or
a collection (typically a vector) of `[name :as alias]` or `[name :refer [fns]]`:

``` clojure
(require '[clojure.set :refer [difference]])

(difference #{1 2 3} #{3 4 5 6}) ;= #{1 2}
```

The `:as` and `:refer` options can be used together:

``` clojure
(require '[clojure.set :as cs :refer [difference]])

(difference #{1 2 3} #{3 4 5 6}) ;= #{1 2}
(cs/union #{1 2 3} #{3 4 5 6})   ;= #{1 2 3 4 5 6}
```

`clojure.core/use` does the same thing as `clojure.core/require` with the
`:refer` option discussed. It is not generally recommended with Clojure
versions starting with 1.4. Just use `clojure.core/require` with `:refer`
instead.


## Namespaces and Class Generation

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Namespaces and Code Compilation in Clojure

Clojure is a compiled language: code is compiled when it is loaded (usually with `clojure.core/require`).

A namespace can contain vars or be used purely to extend protocols, add multimethod implementations
or conditionally load other libraries (e.g. the most suitable JSON parser or key/value store implementation).
In all cases, to trigger compilation, you need to require the namespace.


## Private Vars

Vars (and, in turn, functions defined with `defn`) can be private. There are two ways to
specify that a function is private, via its metadata or the `defn-` macro:

``` clojure
(ns megacorp.superlib)

;;
;; Implementation
;;

(defn- data-stream
  [source]
  (comment ...))

(def ^{:private true} sealed-deal?
  [deal]
  (comment ...))
```


## How to Look up and Invoke a Function by Name

It is possible to look up a function in particular namespace by name with `clojure.core/resolve` that takes
quoted names of the namespace and function. The returned value can be used just like any other
function, for example, passed as an argument to a higher order function:

``` clojure
(resolve 'clojure.set 'difference) ;= #'clojure.set/difference

(let [f (resolve 'clojure.set 'difference)]
   (f #{1 2 3} #{3 4 5 6})) ;= #{1 2}
```



## Compiler Exceptions

This section describes some common compilation errors.


### ClassNotFoundException

This exception means that JVM could not load a class. It is either misspelled or not on the classpath.
Potentially your project has unsatisfied dependency (some dependencies may be optional).

Example:

``` clojure
user=> (import java.uyil.concurrent.TimeUnit)
ClassNotFoundException java.uyil.concurrent.TimeUnit  java.net.URLClassLoader$1.run (URLClassLoader.java:366)
```

In the example above, `java.uyil.concurrent.TimeUnit` should have been `java.util.concurrent.TimeUnit`.


### CompilerException java.lang.RuntimeException: No such var

This means that somewhere in the code a non-existent var is used. It may be a typo, an
incorrect macro-generated var name or a similar issue. Example:

``` clojure
user=> (clojure.java.io/resouce "thought_leaders_quotes.csv")
CompilerException java.lang.RuntimeException: No such var: clojure.java.io/resouce, compiling:(NO_SOURCE_PATH:1)
```

In the example above, `clojure.java.io/resouce` should have been `clojure.java.io/resource`. `NO_SOURCE_PATH`
means that compilation was triggered from the REPL and not a Clojure source file.



TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)



## Temporarily Overriding Vars in Namespaces

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)



## Getting Information About and Programmatically Manipulating Namespaces

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)



## Wrapping Up

Namespaces are basically maps (dictionaries) that map names to
vars. In many cases, those vars store functions in them.

This implementation lets Clojure have many of its highly dynamic
features at a very reasonable runtime overhead cost. For example, vars
in namespaces can be temporarily altered for unit testing purposes.
