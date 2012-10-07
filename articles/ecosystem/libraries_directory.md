---
title: "A Directory of Clojure Libraries"
layout: article
---

This is a categorized and annotated directory of available Clojure
libraries and tools.

This directory is not comprehensive.

This directory is manually curated by the Clojure community. Please
endeavor to keep it up-to-date, consisting of libraries you'd
recommend to friends.

For help on how to use the various libraries available in your code,
see the [libraries-usage tutorial](../tutorials/libraries_usage.html).

Conventions in this document:

  * Clojure core built-in functions are written without the
    leading "clojure.core/".
  * Standard library names all begin with "clojure.".
  * Contrib libraries are marked "{contrib}" and link to their
    respective github project pages.
  * Third-party libraries link to their respective canonical Clojars
    pages.
  * References to built-in Java classes are noted here and there, and
    use fully-qualified names.
  * Third-party Java libraries are marked "{Java}".

> A couple of notes on Clojure library naming:
>
>  1. Clojure libraries that wrap or otherwise provide access to an
>     underlying Java library often have their name prefixed with "clj-".
>
>  2. Clojure libraries that provide their own implementation of a
>     known library may sometimes be named with a "-clj" suffix,
>     for example "markdown-clj".

Locations of library documentation:

  * [the core built-ins and the standard
    library](http://clojure.github.com/clojure/index.html) (see also
    [ClojureDocs](http://clojuredocs.org/))
  * [contrib libraries](http://clojure.github.com/) (also listed at
    the [confluence contrib
    page](http://dev.clojure.org/display/doc/Clojure+Contrib) with
    links to their respective github project pages (which may contain
    additional separate docs))

> Historical Note: In ancient times (pre Clojure 1.3), contrib was not
> the modular set of individual libraries you find today, but instead
> was a single monolithic library. To learn more about the transition,
> see [Where did Clojure.Contrib
> Go](http://dev.clojure.org/display/design/Where+Did+Clojure.Contrib+Go).

Also very useful are [the cheatsheets with
tooltips](http://jafingerhut.github.com/).

Any links to Java docs point to the ones for version 7:
<http://docs.oracle.com/javase/7/docs/api/>.

**********************************************************************

Libraries
=========

Mathematics
-----------

  * See the *Numbers* section of the [cheatsheet](http://clojure.org/cheatsheet).

  * [java.lang.Math](http://docs.oracle.com/javase/7/docs/api/java/lang/Math.html)
    supplies many mathematical functions. Ex.: `(Math/exp 3.0)`

  * [math.combinatorics](https://github.com/clojure/math.combinatorics)
    {contrib}: common combinatorial functions.

  * [math.numeric-tower](https://github.com/clojure/math.numeric-tower)
    {contrib}: Math functions that deal intelligently with the various
    types in Clojure's numeric tower, as well as math functions
    commonly found in Scheme implementations.


Support Libraries
-----------------

### Strings

  * See the *Strings* section of the [cheatsheet](http://clojure.org/cheatsheet).

  * clojure.string

  * [useful](https://clojars.org/useful): See useful.string

  * See also the docs for
    [java.lang.String](http://docs.oracle.com/javase/7/docs/api/java/lang/String.html).


### Sets

  * See the *Sets* section of the [cheatsheet](http://clojure.org/cheatsheet).

  * clojure.set


Environment
-----------

  * Read environment variables with java.lang.System/getenv. Ex:
    `(System/getenv "HOME")`

    Note: you can find out a number of other things about your runtime
    system by doing `(System/getProperty "foo.bar")`. For a list of
    strings you can use in place of "foo.bar", see the [docs for
    java.lang.System/getProperties](http://docs.oracle.com/javase/7/docs/api/java/lang/System.html#getProperties%28%29).

  * [environ](https://clojars.org/environ): Manage environment
    settings from a number of different sources.


I/O
---

### Stdin and Stdout

  * To read user input from stdin in the repl, use `read-line`.

  * clojure.pprint/pprint for pretty printing.


### Terminal

  * [clojure-lanterna](https://clojars.org/clojure-lanterna): for
    creating TUIs (terminal-based user-interfaces), like ncurses. It's
    a Clojure-friendly wrapper around the Java
    [lanterna](https://code.google.com/p/lanterna/) library.


### Files

  * To read the complete contents of a file to a string: `slurp`.

  * To write a string to file: `spit`.

  * clojure.java.io

  * [fs](https://clojars.org/fs): utilities for working with the file
    system.


### Process Management

  * clojure.java.shell: Conveniently launch a sub-process providing
    its stdin and collecting its stdout.

  * [conch](https://clojars.org/conch): for shelling out to external programs.
    An alternative to clojure.java.shell.


Date and Time
-------------

  * `(System/currentTimeMillis)` gets you the current epoch time in
    milliseconds.

  * `(Thread/sleep $n)` will have the current thread sleep for $n
    milliseconds.

  * [clj-time](https://clojars.org/clj-time): A date and time library
    for Clojure. Wraps the [Joda
    Time](http://joda-time.sourceforge.net/) library.
  

Testing
-------

  * clojure.test: Easy, quick, standard.

  * [Midje](https://clojars.org/midje): A more featureful test framework.


HTML
----

### Creating

  * [hiccup](https://clojars.org/hiccup): Easily generate HTML from
    Clojure data structures.

  * [markdown-clj](https://clojars.org/markdown-clj): Clojure
    based Markdown parsers for both Clojure and ClojureScript.


### Parsing

  * [TagSoup](http://home.ccil.org/~cowan/XML/tagsoup/): {Java} A tool for parsing
    html as it's found in the wild: poor, nasty, and brutish. In your project.clj,
    you want something like `[org.ccil.cowan.tagsoup/tagsoup "1.2.1"]`


JSON
----

  * [data.json](https://github.com/clojure/data.json)
    {contrib}: JSON parser/generator to/from Clojure data structures.

  * [cheshire](https://clojars.org/cheshire): Clojure JSON and JSON
    SMILE (binary json format) encoding/decoding.


File formats
------------

  * [clj-pdf](https://clojars.org/clj-pdf): a library for easily
    generating PDFs from Clojure.

  
Templating
----------

  * [Stencil](https://clojars.org/stencil):
    [Mustache](http://mustache.github.com/) for Clojure (logic-less
    templates). Fast.

  * [Clostache](https://clojars.org/de.ubercode.clostache/clostache):
    Another nice [Mustache](http://mustache.github.com/) implementation.



HTTP
----

### Client

  * [clj-http](https://clojars.org/clj-http): An idiomatic Clojure
    http client wrapping the apache client.

### RSS
  
  * [clj-rss](https://clojars.org/clj-rss): RSS feed generation
    library.


Logging
-------

  * [tools.logging](https://github.com/clojure/tools.logging/)
    {contrib}: standard general-purpose logging.

  * [clj-log](https://clojars.org/clj-log): s-expression logger.

  * [Timbre](https://clojars.org/com.taoensso/timbre):
    Simple, flexible, all-Clojure logging. No XML!


Web Application Libraries
-------------------------

  * [ring](https://clojars.org/ring): foundational web application library.

  * [compojure](https://clojars.org/compojure): A concise routing library for Ring.

  * [friend](https://clojars.org/com.cemerick/friend): Authentication
    and authorization library for Ring Clojure web apps and services.


Database
--------

  * [java.jdbc](https://github.com/clojure/java.jdbc) {contrib}: Basic wrapper for JDBC.
    Supports many databases including
    [Apache Derby](http://search.maven.org/#search|ga|1|g:"org.apache" a:"derby"),
    [HSQLDB](http://search.maven.org/#search|ga|1|g:"org.hsqldb" a:"hsqldb"),
    Microsoft SQL Server via both the
    [jTDS driver](http://search.maven.org/#search|ga|1|g:"net.sourceforge.jtds" a:"jtds") and the
    [Microsoft JDBC4 driver](http://www.microsoft.com/en-us/download/details.aspx?id=11774) (not on Maven),
    [MySQL](http://search.maven.org/#search|ga|1|mysql-connector-java),
    [PostgreSQL](http://search.maven.org/#search|ga|1|g:"postgresql" a:"postgresql") and
    [SQLite](http://search.maven.org/#search|ga|1|sqlite-jdbc).
    See the java.jdbc documentation for example dependencies for using these databases as well as basic usage.
    [java.jdbc examples](https://github.com/uvtc/clojure-dining-car/blob/master/examples/java.jdbc.md).

  * [Korma](https://clojars.org/korma): DSL for working with
    relational DB's. It's ["Tasty SQL for
    Clojure"](http://sqlkorma.com/docs).

  * [clutch](https://clojars.org/com.ashafa/clutch): For using [Apache
    CouchDB](http://couchdb.apache.org/).
    
  * [congomongo](https://clojars.org/congomongo): Basic wrapper for
    [MongoDB](http://www.mongodb.org).
  
  * [monger](https://clojars.org/com.novemberain/monger): More
    sophisticated wrapper for [MongoDB](http://www.mongodb.org) with a
    DSL.

  * [Welle](https://clojars.org/com.novemberain/welle): An expressive
    Clojure client for Riak.

  * [Neocons](https://clojars.org/clojurewerkz/neocons): An idiomatic
    Clojure client for the Neo4J REST API. Cypher queries, Heroku
    add-on support, and more.


Graphics
--------

  * [Quil](https://clojars.org/quil): For making drawings, animations,
    and artwork ([some
    examples](https://github.com/quil/quil/blob/master/examples/gen_art/README.md)). Wraps
    the ["Processing"](http://www.processing.org/) graphics environment.


### GUI (Graphical User Interface)

  * [seesaw](https://clojars.org/seesaw): A Swing wrapper/DSL.


************************************************************************

Tools
=====

In this section we provide links to the projects' main websites rather
than to their Clojars pages, since the tools are generally not used in
the same way as libraries.


Documentation
-------------

  * [Marginalia](https://github.com/fogus/marginalia): View a
    project's docstrings side-by-side with its code. To see an
    example, see [the Marginalia
    site](http://fogus.me/fun/marginalia/). It's probably most common
    to use it via the [Marginalia Leiningen
    plug-in](https://github.com/fogus/lein-marginalia).


### Generating API Docs

  * [codox](https://github.com/weavejester/codox): See the [compojure
    api docs](http://weavejester.github.com/compojure/) for an
    example.

  * [Autodoc](http://tomfaulhaber.github.com/autodoc/): Currently used
    to generate the official [Clojure API
    docs](http://clojure.github.com/).
