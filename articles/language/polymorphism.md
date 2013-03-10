---
title: "Polymorphism in Clojure: Protocols and Multimethods"
layout: article
---

This guide covers:

 * What are polymorphic functions
 * Type-based polymoprhism with protocols
 * Ad-hoc polymorphism with multimethods
 * How to create your own data types that behave like core Clojure data types

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.


## Overview

According to Wikipedia,

> In computer science, polymorphism is a programming language feature that allows values of different data types to be handled using a uniform interface.

Polymorphism is not at all unique to object-oriented programming languages. Clojure has excellent support for
polymorphism.

For example, when a function can be used on multiple data types or behave differently based on additional argument
(often called *dispatch value*), that function is *polymorphic*. A simple example of such function is a function that
serializes its input to JSON (or other format).

Ideally, developers would like to use the same function regardless of the input, and be able to extend
it to new inputs, without having to change the original source. Inability to do so is known as the [Expression Problem](http://en.wikipedia.org/wiki/Expression_problem).

In Clojure, there are two approaches to polymorphism:

 * Data type-oriented. More efficient (modern JVMs optimize this case very well), less flexible.
 * So called "ad-hoc polymorphism" where the exact function implementation is picked at runtime based on a special argument (*dispatch value*).

The former is implemented using *protocols*, a feature first introduced in Clojure 1.2. The latter is available via
*multimethods*, a feature that was around in Clojure since the early days.


## Type-based Polymorphism With Protocols

It is common for polymorphic functions to *dispatch* (pick implementation) on the type of the first argument. For example,
in Java or Ruby, when calling `#toString` or `#to_s` on an object, the exact implementation is located using that object's
type.

Because this is a common case and because JVM can optimize this dispatch logic very well, Clojure 1.2 introduced a new
feature called *protocols*. Protocols are simply groups of functions. Each of the functions can have different
implementations for different data types.

Protocols are defined using the `clojure.core/defprotocol` special form. The example below defines a protocol for working with URLs and URIs.
While URLs and URIs are not the same thing, some operations make sense for both:

``` clojure
(defprotocol URLLike
  "Unifies operations on URLs and URIs"
  (^String protocol-of  [input] "Returns protocol of given input")
  (^String host-of      [input] "Returns host of given input")
  (^String port-of      [input] "Returns port of given input")
  (^String user-info-of [input] "Returns user information of given input")
  (^String path-of      [input] "Returns path of given input")
  (^String query-of     [input] "Returns query string of given input")
  (^String fragment-of  [input] "Returns fragment of given input"))
```

`clojure.core/defprotocol` takes the name of the protocol and one or more lists of 
**function name**, **argument list**, **documentation string**:

``` clojure
(^String protocol-of  [input] "Returns protocol of given input")
(^String host-of      [input] "Returns host of given input")
```

The example above uses return type hints. This makes sense in the example but is not necessary. It could have been written
it as

``` clojure
(defprotocol URLLike
  "Unifies operations on URLs and URIs"
  (protocol-of  [input] "Returns protocol of given input")
  (host-of      [input] "Returns hostname of given input")
  (port-of      [input] "Returns port of given input")
  (user-info-of [input] "Returns user information (username:password) of given input")
  (path-of      [input] "Returns path of given input")
  (query-of     [input] "Returns query string of given input")
  (fragment-of  [input] "Returns fragment of given input"))
```

There are 3 ways URIs and URLs are commonly represented on the JVM:

 * `java.net.URI` instances
 * `java.net.URL` instances
 * Strings

When a new protocol imlementation is added for a type, it is called **extending the protocol**. The most common way to extend
a protocol is via the `clojure.core/extend-protocol`:

``` clojure
(import java.net.URI)
(import java.net.URL)

(extend-protocol URLLike
  URI
  (protocol-of [^URI input]
    (when-let [s (.getScheme input)]
      (.toLowerCase s)))
  (host-of [^URI input]
    (-> input .getHost .toLowerCase))
  (port-of [^URI input]
    (.getPort input))
  (user-info-of [^URI input]
    (.getUserInfo input))
  (path-of [^URI input]
    (.getPath input))
  (query-of [^URI input]
    (.getQuery input))
  (fragment-of [^URI input]
    (.getFragment input))

  URL
  (protocol-of [^URL input]
    (protocol-of (.toURI input)))
  (host-of [^URL input]
    (host-of (.toURI input)))
  (port-of [^URL input]
    (.getPort input))
  (user-info-of [^URL input]
    (.getUserInfo input))
  (path-of [^URL input]
    (.getPath input))
  (query-of [^URL input]
    (.getQuery input))
  (fragment-of [^URL input]
    (.getRef input)))
```

Protocol functions are used just like regular Clojure functions:

``` clojure
(protocol-of (URI. "http://clojure-doc.org")) ;= "http"
(protocol-of (URL. "http://clojure-doc.org")) ;= "http"

(path-of (URL. "http://clojure-doc.org/articles/content.html")) ;= "/articles/content.html"
(path-of (URI. "http://clojure-doc.org/articles/content.html")) ;= "/articles/content.html"
```

### Using Protocols From Different Namespaces

Protocol functions are required and used the same way as regular protocol functions. Consider a
namespace that looks like this

``` clojure
(ns superlib.url-like
  (:import [java.net URL URI]))

(defprotocol URLLike
  "Unifies operations on URLs and URIs"
  (^String protocol-of  [input] "Returns protocol of given input")
  (^String host-of      [input] "Returns host of given input")
  (^String port-of      [input] "Returns port of given input")
  (^String user-info-of [input] "Returns user information of given input")
  (^String path-of      [input] "Returns path of given input")
  (^String query-of     [input] "Returns query string of given input")
  (^String fragment-of  [input] "Returns fragment of given input"))

(extend-protocol URLLike
  URI
  (protocol-of [^URI input]
    (when-let [s (.getScheme input)]
      (.toLowerCase s)))
  (host-of [^URI input]
    (-> input .getHost .toLowerCase))
  (port-of [^URI input]
    (.getPort input))
  (user-info-of [^URI input]
    (.getUserInfo input))
  (path-of [^URI input]
    (.getPath input))
  (query-of [^URI input]
    (.getQuery input))
  (fragment-of [^URI input]
    (.getFragment input))

  URL
  (protocol-of [^URL input]
    (protocol-of (.toURI input)))
  (host-of [^URL input]
    (host-of (.toURI input)))
  (port-of [^URL input]
    (.getPort input))
  (user-info-of [^URL input]
    (.getUserInfo input))
  (path-of [^URL input]
    (.getPath input))
  (query-of [^URL input]
    (.getQuery input))
  (fragment-of [^URL input]
    (.getRef input)))
```

To use `superlib.url-like/path-of` and other functions, you require them as regular functions:

``` clojure
(ns myapp
  (:require [superlib.url-like] :refer [host-of scheme-of]))

(host-of (java.net.URI. "https://twitter.com/cnn/"))
```


### Extending Protocols For Core Clojure Data Types

TBD


### Protocols and Custom Data Types

TBD: cover extend-type, extend


### Partial Implementation of Protocols

With protocols, it is possible to only implement certain functions for certain types.


## Ad-hoc Polymorphism with Multimethods

### First Example: Shapes

Lets start with a simple problem definition. We have 3 shapes: square, circle and triangle, and
need to provide an polymorphic function that calculates the area of the given shape.

In total, we need 4 functions:

 * A function that calculates area of a square
 * A function that calculates area of a circle
 * A function that calculates area of a triangle
 * A polymorphic function that acts as a "unified frontend" to the functions above

we will start with the latter and define a *multimethod* (not related to methods on Java objects or object-oriented programming):

``` clojure
(defmulti area (fn [shape & _]
                 shape))
```

Our multimethod has a name and a *dispatch function* that takes arguments passed to the multimethod and returns
a value. The returned value will define what implementation of multimethod is used. In Java or Ruby, method implementation
is picked by traversing the class hierarchy. With multimethods, the logic can be anything you need. That's why it is
called *ad-hoc polymorphism*.

An alternative way of doing the same thing is to pass `clojure.core/first` instead of an anonymous function:

``` clojure
(defmulti area first)
```

Next lets implement our area multimethod for squares:

``` clojure
(defmulti area (fn [shape & _]
                 shape))

(defmethod area :square
  [_ side]
  (* side side))
```

Here `defmethod` defines a particular implementation of the multimethod `area`, the one that will be used if dispatch function
returns `:square`. Lets try it out. Multimethods are invoked like regular Clojure functions:

``` clojure
(area :square 4)     ;= 16
```

In this case, we pass dispatch value as the first argument, our dispatch function returns it unmodified and
that's how the exact implementation is looked up.

Implementation for circles looks very similar, we choose `:circle` as a reasonable dispatch value:

``` clojure
(defmethod area :circle
  [_ radius]
  (* radius Math/PI Math/PI))

(area :circle 3)     ;= 29.608813203268074
```

For the record, `Math/PI` in this example refers to `java.lang.Math/PI`, a field that stores the value of Pi.

Finally, an implementation for triangles. Here you can see that exact implementations can take different number of
arguments. To calculate the area of a triangle, we multiple base by height and divide it by 2:

``` clojure
(defmethod area :triangle
  [_ b h]
  (* 1/2 b h))

(area :triangle 3 5) ;= 15/2
```

In this example we used **Clojure ratio** data type. We could have used doubles as well.

Putting it all together:

``` clojure
(defmulti area (fn [shape & _]
                 shape))

(defmethod area :square
  [_ side]
  (* side side))

(defmethod area :circle
  [_ radius]
  (* radius Math/PI Math/PI))

(defmethod area :triangle
  [_ b h]
  (* 1/2 b h))

(area :square 4)     ;= 16
(area :circle 3)     ;= 29.608813203268074
(area :triangle 3 5) ;= 15/2
```


### Second Example: TBD

TBD: an example that demonstrates deriving


## How To Create Custom Data Type That Core Functions Can Work With

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Wrapping Up

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
