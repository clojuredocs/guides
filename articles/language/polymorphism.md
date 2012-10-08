---
title: "Polymorphism in Clojure: protocols and multimethods"
layout: article
---

## About this guide

This guide covers:

 * What are polymorphic functions
 * Type-based polymoprhism with protocols
 * Ad-hoc polymorphism with multimethods
 * How to create your own data types that behave like core Clojure data types

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).


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

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Ad-hoc Polymorphism with Multimethods

### First Example: Shapes

Lets start with a simple problem definition. We have 3 shapes: square, triangle and circle, and
need to provide an polymorphic function that calculates the area of the given shape.

In total, we need 4 functions:

 * A function that calculates area of a square
 * A function that calculates area of a triangle
 * A function that calculates area of a triangle
 * A polymorphic function that acts as a "unified frontend" to the functions above

we will start with the latter and define a *multimethod* (not related to methods on Java objects or object-oriented programming):

{% highlight clojure %}
(defmulti area (fn [shape & _]
                 shape))
{% endhighlight %}

Our multimethod has a name and a *dispatch function* that takes arguments passed to the multimethod and returns
a value. The returned value will define what implementation of multimethod is used. In Java or Ruby, method implementation
is picked by traversing the class hierarchy. With multimethods, the logic can be anything you need. That's why it is
called *ad-hoc polymorphism*.

An alternative way of doing the same thing is to pass `clojure.core/first` instead of an anonymous function:

{% highlight clojure %}
(defmulti area first)
{% endhighlight %}

Next lets implement our area multimethod for squares:

{% highlight clojure %}
(defmulti area (fn [shape & _]
                 shape))

(defmethod area :square
  [_ side]
  (* side side))
{% endhighlight %}

Here `defmethod` defines a particular implementation of the multimethod `area`, the one that will be used if dispatch function
returns `:square`. Lets try it out. Multimethods are invoked like regular Clojure functions:

{% highlight clojure %}
(area :square 4)     ;= 16
{% endhighlight %}

In this case, we pass dispatch value as the first argument, our dispatch function returns it unmodified and
that's how the exact implementation is looked up.

Implementation for circles looks very similar, we choose `:circle` as a reasonable dispatch value:

{% highlight clojure %}
(defmethod area :circle
  [_ radius]
  (* radius Math/PI Math/PI))

(area :circle 3)     ;= 29.608813203268074
{% endhighlight %}

For the record, `Math/PI` in this example refers to `java.lang.Math/PI`, a field that stores the value of Pi.

Finally, an implementation for triangles. Here you can see that exact implementations can take different number of
arguments. To calculate the area of a triangle, we multiple base by height and divide it by 2:

{% highlight clojure %}
(defmethod area :triangle
  [_ b h]
  (* 1/2 b h))

(area :triangle 3 5) ;= 15/2
{% endhighlight %}

In this example we used **Clojure ratio** data type. We could have used doubles as well.

Putting it all together:

{% highlight clojure %}
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
{% endhighlight %}


### Second Example: Content Serialization

TBD: content serialization in Welle uses multimethods and is a real world example even beginners
can relate to. MK.


## How To Create Custom Data Type That Core Functions Can Work With

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Wrapping Up

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
