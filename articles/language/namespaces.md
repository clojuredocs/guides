---
title: "Clojure namespaces"
layout: article
---

## About this guide

This guide covers:

 * Clojure namespaces
 * How to use functions in other namespaces
 * `require` vs `refer` vs `use`
 * How code compilation works in Clojure

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).


## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.


## Overview

Clojure functions are organized into *namespaces*. Clojure namespaces are very similar to
Java packages and Python modules. Namespaces are basically maps (dictionaries) that map names
to *vars*. In many cases, those vars store functions in them.


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
  (:require clojure.set)

;; now it is possible to do
;; (clojure.set/difference #{1 2 3} #{3 4 5})
```

This will make sure the `clojure.set` namespace is loaded, compiled and available as `clojure.set`
(using its fully qualified name). It is possible (and common) to make a namespace available
as a different alias:

``` clojure
(ns megacorp.profitd.scheduling
  (:require [clojure.set :as cs])

;; now it is possible to do
;; (cs/difference #{1 2 3} #{3 4 5})
```

One more example with two required namespaces:

``` clojure
(ns megacorp.profitd.scheduling
  (:require [clojure.set  :as cs]
            [clojure.walk :as walk])
```

To make functions in `clojure.set` available in the defined namespace via short names
(e.g. without the `clojure.set` or other prefix), you can tell Clojure compiler
to *refer* to certain functions:

``` clojure
(ns megacorp.profitd.scheduling
  (:require [clojure.set :refer [difference intersection]])

;; now it is possible to do
;; (difference #{1 2 3} #{3 4 5})
```

The `:refer` feature of the `:require` form is new in Clojure 1.4.


### The Current Namespace

Under the hood, Clojure keeps **current namespace** a special var, [*ns*](http://clojuredocs.org/clojure_core/clojure.core/*ns*).
When vars are defined using the [def](http://clojuredocs.org/clojure_core/clojure.core/def) special form, they are
added to the current namespace. 


### The :refer-clojure Helper Form

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


### The :use Helper Form

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


### The :gen-class Helper Form

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## How to Use Functions From Other Namespaces

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## `require` vs `refer`, `use`

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Namespaces and Class Generation

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## How Code Compilation Works in Clojure

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Wrapping Up

Namespaces are basically maps (dictionaries) that map names to
vars. In many cases, those vars store functions in them.

This implementation lets Clojure have many of its highly dynamic
features at a very reasonable runtime overhead cost. For example, vars
in namespaces can be temporarily altered for unit testing purposes.
