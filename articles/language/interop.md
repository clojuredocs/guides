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

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.


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

``` clojure
java.util.Date ;= java.util.Date
```

``` clojure
(import java.util.Date)

Date ;= java.util.Date
```

`ns` macro supports imports, too:
``` clojure
(ns myservice.main
  (:import java.util.Date))
```

More about the `ns` macro can be found in the article on [Clojure namespaces](/articles/language/namespaces.html).

Dynamic (at runtime) imports are usually only used in the REPL and cases when there are multiple implementations of a particular
protocol/service/feature and it is not possible to tell which one should be used until run time.

### Automatic Imports For java.lang.*

Classes from the `java.lang` package are automatically imported. For example, you can use `String` or `Math`
without explicitly importing them:

``` clojure
(defn http-uri?
  [^String uri]
  (.startsWith (.toLowerCase uri) "http"))

(Math/round 0.7886)
```


### Inner (Nested) Classes

In Java, classes can be nested inside other classes. They are called *inner classes* and by convention,
separated from their outer class by a dollar sign (`$`):

``` clojure
(import java.util.Map$Entry)

Map$Entry ;= java.util.Map$Entry

;; this example assumes RabbitMQ Java client is on classpath
(import com.rabbitmq.client.AMQP$BasicProperties)

AMQP$BasicProperties ;= com.rabbitmq.client.AMQP$BasicProperties
```

Note that if you need to use both a class and one or more of its inner classes, they all need to be imported separately.
As far as JVM is concerned, they are all separate classes, there is no "imports hierarchy".


## How to Instantiate Java Classes

Java classes are instantiated using the `new` special form:

``` clojure
(new java.util.Date) ;= #inst "2012-10-09T21:23:57.278-00:00"
```

However, Clojure reader provides a bit of syntactic sugar and you are much more likely
to see it used:

``` clojure
(java.util.Date.)    ;= #inst "2012-10-09T21:24:43.878-00:00"
```

It is possible to use fully qualified names (e.g. `java.util.Date`) or short names with imports:

``` clojure
(import java.util.Date)

(Date.) ;= #inst "2012-10-09T21:24:27.229-00:00"
```

An example with constructor arguments:

``` clojure
(java.net.URI. "http://clojure.org") ;= #<URI http://clojure.org>
```

## How to Invoke Java Methods

### Instance Methods

Instance methods are invoked using the `.` special form:

``` clojure
(let [d (java.util.Date.)]
  (. d getTime)) ;= 1349819873183
```

Just like with object instantiation, it is much more common to see an alternative version:

``` clojure
(let [d (java.util.Date.)]
  (.getTime d)) ;= 1349819873183
```


### Static Methods

Static methods can be invoked with the same `.` special form:

``` clojure
(. Math floor 5.677) ;= 5.0
```

or (typically) to sugared version, `ClassName/methodName`:

``` clojure
(Math/floor 5.677) ;= 5.0

(Boolean/valueOf "false") ;= false
(Boolean/valueOf "true")  ;= true
```


### Chained Calls

It is possible to chain method calls using the `..` special form:

``` clojure
(.. (java.util.Date.) getTime toString) ;= "1349821993809"
```


### Multiple Calls On the Same Object

If you need to call a bunch of methods on a mutable object, you
can use the `doto` macro:

``` clojure
(doto (java.util.Stack.)
  (.push 42)
  (.push 13)
  (.push 7)) ;= #<Stack [42, 13, 7]>
```

The `doto` macro returns its first argument as a result.


## How to Access Java Fields

Public mutable fields are not common in Java libraries but sometimes you need to access them.
It's done with the same dot special form:

``` clojure
(import java.awt.Point)

(let [pt (Point. 0 10)]
  (. pt x)) ;= 0

(let [pt (Point. 0 10)]
  (. pt y)) ;= 10
```

and just like with instance methods, it is much more common to see the following version:

``` clojure
(import java.awt.Point)

(let [pt (Point. 0 10)]
  (.x pt)) ;= 0

(let [pt (Point. 0 10)]
  (.y pt)) ;= 10
```


## How to Set Java Fields

To set a public mutable field, use `clojure.core/set!` that takes a field in the dot notation
demonstrated earlier and a new value:

``` clojure
(import java.awt.Point)

(let [pt (Point. 0 10)]
  (set! (.y pt) 100)
  (.y pt)) ;= 100
```

Fortunately, mutable public fields are rare to meet in the JVM ecosystem so you won't need
to do this often.


## How To Work With Enums

[Enums (enumeration) type](http://docs.oracle.com/javase/tutorial/java/javaOO/enum.html) values are accessed
the same way as fields, except on enum classes and not objects:

``` clojure
java.util.concurrent.TimeUnit/MILLISECONDS ;= #< MILLISECONDS>
```


## Determining Classes of Java Objects

To get class of a particular value, pass it to `clojure.core/class`:

``` clojure
(class 1)      ;= java.lang.Long
(class 1.0)    ;= java.lang.Double
(class "docs") ;= java.lang.String
(class (java.net.URI. "http://github.com")) ;= java.net.URI
```

As this example demonstrates, Clojure strings are JVM strings, integer literals are compiled
as longs and floating point literals are compiled as doubles.


## How To Get a Java Class Reference By Name

To obtain a class reference by its string name (fully qualified), use `Class/forName` via Java interop:

``` clojure
(Class/forName "java.util.Date") ;= java.util.Date
```

### Array Types, Primitives

JVM has what is called **primitive types** (numerics, chars, booleans) that are not "real" objects.
In addition, array types have pretty obscure internal names. If you need to obtain a reference to
an array of longs, for example, pass `"[[J"` to `Class/forName`. Below is the full table:

<table class="table-striped table-bordered table">
  <thead>
    <tr>
      <th>Internal JVM class name</th>
      <th>Array of ? (type)</th>
    </tr>
  </thead>

  <tbody>
    <tr>
      <td>"[[S"</td>
      <td>short</td>
    </tr>
    <tr>
      <td>"[[I"</td>
      <td>integer</td>
    </tr>
    <tr>
      <td>"[[J"</td>
      <td>long</td>
    </tr>
    <tr>
      <td>"[[F"</td>
      <td>float</td>
    </tr>
    <tr>
      <td>"[[D"</td>
      <td>double</td>
    </tr>
    <tr>
      <td>"[[B"</td>
      <td>byte</td>
    </tr>
    <tr>
      <td>"[[C"</td>
      <td>char</td>
    </tr>
    <tr>
      <td>"[[Z"</td>
      <td>boolean</td>
    </tr>
  </tbody>
</table>

If this does not make much sense, don't worry. Just remember to come
back to this guide when you need to extend a protocol for an array of
primitives.



## Extending Java Classes With proxy

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## Implementing Java Interfaces With reify

It is possible to implement Java interfaces in Clojure. It is
typically needed to interact with Java libraries that take arguments
implementing a particular interface.

Interfaces are implemented using the `reify` special form.

Given the following Java interface:

``` java
public
interface FilenameFilter {
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param   dir    the directory in which the file was found.
     * @param   name   the name of the file.
     * @return  <code>true</code> if and only if the name should be
     * included in the file list; <code>false</code> otherwise.
     */
    boolean accept(File dir, String name);
}
```

here is how to implement it in Clojure:

``` clojure
;; a FileFilter implementation that accepts everything
(reify java.io.FilenameFilter
  (accept [this dir name]
    true))
```

`reify` takes an interface (fully-qualified name or short name) and one or more
method implementations that mimic function definitions without the `defn` and with
*this* (as in Java, JavaScript or *self* in Ruby, Python) reference being the first argument:

``` clojure
(accept [this dir name]
  true)
```

With `reify`, generally there is no need to add type hints on arguments: Clojure
compiler typically will detect the best matching method (by name and number of arguments).

`reify` returns a *Java class instance*. Clojure compiler will generate a class that implements
the interface and instantiate it. To demonstrate that reified objects indeed implement
the interface:

``` clojure
(let [ff (reify java.io.FilenameFilter
           (accept [this dir name]
             true))]
  (instance? java.io.FileFilter ff)) ;= true
```

`reify` can be used to implement multiple interfaces at once:

``` clojure
(let [ff (reify java.io.FilenameFilter
           (accept [this dir name]
             true)

           java.io.FileFilter
           (accept [this dir]
             true))]
  (instance? java.io.FileFilter ff)) ;= true
```

### reify, Parameter Destructuring and Varargs

`reify` does not support destructuring or variadic number of arguments in method signatures.
For example, the following will not work and won't even compile in Clojure 1.5:

``` clojure
(reify com.megacorp.api.AnInterface
  (aMethod [a [b c]]
    (comment ...))
  (anotherMethod [a & rest]
    (comment ...)))
```

### Example 1

The following example demonstrates how instances created with `reify` are passed around
as regular Java objects:

``` clojure
(import java.io.File)

;; a file filter implementation that keeps only .clj files
(let [ff (reify java.io.FilenameFilter
           (accept [this dir name]
             (.endsWith name ".clj")))
    dir  (File. "/Users/antares/Development/ClojureWerkz/neocons.git/")]
  (into [] (.listFiles dir ff))) ;= [#<File /Users/antares/Development/ClojureWerkz/neocons.git/project.clj>]
```


### Clojure Functions Implement Runnable and Callable

Note that Clojure functions implement `java.lang.Runnable` and
`java.util.concurrent.Callable` directly so you can pass functions to
methods found in various classes from the `java.util.concurrent` package.

For example, to run a function in a new thread:

``` clojure
(let [t (Thread. (fn []
                   (println "I am running in a separate thread")))]
  (.start t))
```

Or submit a function for execution to a thread pool (in JDK terms: an execution service):

``` clojure
(let [pool (Executors/newFixedThreadPool 16)]
  (.submit pool (cast Callable (fn []
                                  (println "I am executed in a thread pool")))))
```

Note that without the cast, Clojure compiler would not be able to determine
which exact version of the method we intend to invoke, because `java.util.concurrent.ExecutionService/submit`
has two versions, one for `Runnable` and one for `Callable`. They work very much the same but return
slightly different results (`Callable` produces a value while `Runnable` always returns nil when
executed).

The exception we would get without the case is

```
CompilerException java.lang.IllegalArgumentException: More than one matching method found: submit, compiling:(NO_SOURCE_PATH:2)
```


## gen-class and How to Implement Java Classes in Clojure

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)


## How To Extend Protocols to Java Classes

Clojure protocols can be extended to any java class (including
Clojure's internal types) very easily using `extend`:

Using the example of a json library, we can define our goal as getting
to the point where the following works:

``` clojure
(json-encode (java.util.UUID/randomUUID))
```

First, let's start with the protocol for json encoding an object:

``` clojure
(defprotocol JSONable
  (json-encode [obj]))
```

So, everything that is "JSONable" implements a `json-encode` method.

Next, let's define a dummy method to do the "encoding" (in this
example, it just prints to standard out instead, it doesn't actually
do any json encoding):

``` clojure
(defn encode-fn
  [x]
  (prn x))
```

Now, define a method that will encode java objects by calling `bean`
on them, then making each value of the bean map a string:

``` clojure
(defn encode-java-thing
  [obj]
  (encode-fn
   (into {}
         (map (fn [m]
                [(key m) (str (val m))])
              (bean obj)))))
```

Let's try it on an example object, a UUID:

``` clojure
(encode-java-thing (java.util.UUID/randomUUID))
;; ⇒ {:mostSignificantBits "-6060053801408705927",
;;    :leastSignificantBits "-7978739947533933755",
;;    :class "class java.util.UUID"}
```

The next step is to extend the protocol to the java type, telling
clojure which java type to extend, the protocol to implement and the
method to use for the `json-encode` method:

``` clojure
(extend java.util.UUID
  JSONable
  {:json-encode encode-java-thing})
```

Now we can use `json-encode` for the object we've extended:

``` clojure
(json-encode (java.util.UUID/randomUUID))
;; ⇒  {:mostSignificantBits "3097485598740136901",
;;     :leastSignificantBits "-9000234678473924364",
;;     :class "class java.util.UUID"}
```

You could also write the function inline in the extend block, for
example, extending `nil` to return a warning string:

``` clojure
(extend nil
  JSONable
  {:json-encode (fn [x] "x is nil!")})

(json-encode nil)
;; ⇒  "x is nil!"
```

The `encode-java-thing` method can also be reused for other java types
we may want to encode:

``` clojure
(extend java.net.URL
  JSONable
  {:json-encode encode-java-thing})

(json-encode (java.net.URL. "http://aoeu.com"))
;; ⇒  {:path "",
;;     :protocol "http",
;;     :authority "aoeu.com",
;;     :host "aoeu.com",
;;     :ref "",
;;     :content "sun.net.www.protocol.http.HttpURLConnection$HttpInputStream@4ecac02f",
;;     :class "class java.net.URL",
;;     :defaultPort "80",
;;     :port "-1",
;;     :query "",
;;     :file "",
;;     :userInfo ""}
```

## Wrapping Up

TBD: [How to Contribute](https://github.com/clojuredocs/cds#how-to-contribute)
