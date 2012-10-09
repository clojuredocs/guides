---
title: "Clojure interoperability with Java"
layout: article
---

## About this guide

This guide covers:

 * How to instantiate Java classes
 * How to invoke Java methods
 * How to extend Java classes with proxy
 * How to implement Java interfaces with reify
 * How to generate Java classes with gen-class
 * Other topics related to interop

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).


## Overview

Clojure was designed to be a hosted language that directly interoperates with its host platform (JVM, CLR and so on).
Clojure code is compiled to JVM bytecode. For method calls on Java objects, Clojure compiler
will try to emit the same bytecode `javac` would produce.

It is possible to implement interfaces, extend and generate Java classes in Clojure.

Clojure also provides convenient functions and macros that make consuming of Java libraries
easier and often more concise than it would be in Java code.


## Imports

Java classes can be referenced either using their fully-qualified names (FQNs) such as
`java.util.Date` or be *imported* in the current Clojure namespace using `clojure.core/import` and
referenced by short names:

{% highlight clojure %}
java.util.Date ;= java.util.Date
{% endhighlight %}

{% highlight clojure %}
(import java.util.Date)

Date ;= java.util.Date
{% endhighlight %}

`ns` macro supports imports, too:
{% highlight clojure %}
(ns myservice.main
  (:import java.util.Date))
{% endhighlight %}

More about the `ns` macro can be found in the article on [Clojure namespaces](/articles/language/namespaces.html).

Dynamic (at runtime) imports are usually only used in the REPL and cases when there are multiple implementations of a particular
protocol/service/feature and it is not possible to tell which one should be used until run time.

### Inner (Nested) Classes

In Java, classes can be nested inside other classes. They are called *inner classes* and by convention,
separated from their outer class by a dollar sign (`$`):

{% highlight clojure %}
;; this example assumes RabbitMQ Java client is on classpath
(import com.rabbitmq.client.AMQP$BasicProperties)

AMQP$BasicProperties ;= com.rabbitmq.client.AMQP$BasicProperties
{% endhighlight %}


## How to Instantiate Java Classes

Java classes are instantiated using the `new` special form:

{% highlight clojure %}
(new java.util.Date) ;= #inst "2012-10-09T21:23:57.278-00:00"
{% endhighlight %}

However, Clojure reader provides a bit of syntactic sugar and you are much more likely
to see it used:

{% highlight clojure %}
(java.util.Date.)    ;= #inst "2012-10-09T21:24:43.878-00:00"
{% endhighlight %}

It is possible to use fully qualified names (e.g. `java.util.Date`) or short names with imports:

{% highlight clojure %}
(import java.util.Date)

(Date.) ;= #inst "2012-10-09T21:24:27.229-00:00"
{% endhighlight %}

An example with constructor arguments:

{% highlight clojure %}
(java.net.URI. "http://clojure.org") ;= #<URI http://clojure.org>
{% endhighlight %}

## How to Invoke Java Methods

### Instance Methods

Instance methods are invoked using the `.` special form:

{% highlight clojure %}
(let [d (java.util.Date.)]
  (. d getTime)) ;= 1349819873183
{% endhighlight %}

Just like with object instantiation, it is much more common to see an alternative version:

{% highlight clojure %}
(let [d (java.util.Date.)]
  (.getTime d)) ;= 1349819873183
{% endhighlight %}


### Static Methods

Static methods can be invoked with the same `.` special form:

{% highlight clojure %}
(. Math floor 5.677) ;= 5.0
{% endhighlight %}

or (typically) to sugared version, `ClassName/methodName`:

{% highlight clojure %}
(Math/floor 5.677) ;= 5.0
{% endhighlight %}


### Chained Calls

It is possible to chain method calls using the `..` special form:

{% highlight clojure %}
(.. (Date.) getTime toString) ;= "1349821993809"
{% endhighlight %}


## How to Access Java Fields

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## How to Set Java Fields

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Determining Classes of Java Objects

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Extending Java Classes With proxy

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Implementing Java Interfaces With reify

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## gen-class and How to Implement Java Classes in Clojure

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## How To Extend Protocols to Java Classes

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Wrapping Up

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
