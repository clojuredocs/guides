---
title: "A Directory of Clojure Libraries"
layout: article
---

This is a categorized and annotated directory of available Clojure
libraries and tools. This directory is not comprehensive.

This directory is manually curated by the Clojure community. Please
endeavor to keep it up-to-date, consisting of libraries you'd
recommend to friends.


## Mathematics

  * See the *Numbers* section of the [cheatsheet](http://clojure.org/cheatsheet).

  * [java.lang.Math](http://docs.oracle.com/javase/7/docs/api/java/lang/Math.html)
    supplies many mathematical functions. Ex.: `(Math/exp 3.0)`

  * [math.combinatorics](https://github.com/clojure/math.combinatorics)
    {contrib}: common combinatorial functions.

  * [math.numeric-tower](https://github.com/clojure/math.numeric-tower)
    {contrib}: Math functions that deal intelligently with the various
    types in Clojure's numeric tower, as well as math functions
    commonly found in Scheme implementations.


## Support Libraries

### General

  * [useful](https://clojars.org/useful)

  * [ClojureWerkz Support](http://github.com/clojurewerkz/support)

### Strings

  * clojure.string


### Sets

  * See the *Sets* section of the [cheatsheet](http://clojure.org/cheatsheet).

  * clojure.set


## Applications & Environment

  * [environ](https://clojars.org/environ): Manage environment
    settings from a number of different sources.




## Date and Time

  * [clj-time](https://clojars.org/clj-time): A date and time library
    for Clojure. Wraps the [Joda
    Time](http://joda-time.sourceforge.net/) library.
  

## Testing

  * clojure.test: the standard unit testing library that ships with Clojure

  * [Midje](https://clojars.org/midje): A more featureful test framework.


## HTML

### Creating

  * [hiccup](https://clojars.org/hiccup): Generates HTML from Clojure data structures.

  * [markdown-clj](https://clojars.org/markdown-clj): Clojure based Markdown parsers for both Clojure and ClojureScript.


### Parsing

  * [TagSoup](http://home.ccil.org/~cowan/XML/tagsoup/): {Java} A tool for parsing
    html as it's found in the wild: poor, nasty, and brutish.


## JSON

  * [cheshire](https://clojars.org/cheshire): very efficient Clojure JSON and SMILE (binary JSON) encoding/decoding.

  * [data.json](https://github.com/clojure/data.json): JSON parser/generator to/from Clojure data structures.


## File formats

  * [clj-pdf](https://clojars.org/clj-pdf): a library for easily
    generating PDFs from Clojure.

  
## Templating

  * [Stencil](https://clojars.org/stencil):
    [Mustache](http://mustache.github.com/) for Clojure (logic-less
    templates). Fast.

  * [Clostache](https://clojars.org/de.ubercode.clostache/clostache):
    Another nice [Mustache](http://mustache.github.com/) implementation.



## HTTP

### Client

  * [clj-http](https://clojars.org/clj-http): An idiomatic Clojure
    http client wrapping the apache client.

### RSS
  
  * [clj-rss](https://clojars.org/clj-rss): RSS feed generation
    library.


## Logging

  * [Timbre](https://clojars.org/com.taoensso/timbre):
    Simple, flexible, all-Clojure logging. No XML!

  * [tools.logging](https://github.com/clojure/tools.logging/)
    {contrib}: standard general-purpose logging.

  * [clj-log](https://clojars.org/clj-log): s-expression logger.


## Web Development

  * [compojure](https://github.com/weavejester/compojure): A concise routing library for Ring.

  * [ring](https://github.com/ring-clojure): foundational web application library.

  * [friend](https://github.com/cemerick/friend): Authentication and authorization library for Web apps.


## Data Stores

### Relational Databases, JDBC

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

  * [Korma](https://clojars.org/korma): ["Tasty SQL for Clojure"](http://sqlkorma.com/docs)


### CouchDB

  * [Clutch](https://clojars.org/com.ashafa/clutch): [Apache CouchDB](http://couchdb.apache.org/) client.

### MongoDB
      
  * [Monger](https://clojuremongodb.info): Monger is an idiomatic [Clojure MongoDB driver](http://clojuremongodb.info) for a more civilized age.

  * [congomongo](https://clojars.org/congomongo): Basic wrapper for [MongoDB](http://www.mongodb.org).

### Riak

  * [Welle](https://clojureriak.info): An expressive Clojure client for Riak.

### Neo4J (REST API)

  * [Neocons](https://clojureneo4j.info): Neocons is a feature rich idiomatic [Clojure client for the Neo4J REST API](http://clojureneo4j.info).

### ElasticSearch

  * [Elastisch](http://clojureelasticsearch.info): Elastisch is a minimalistic [Clojure client for ElasticSearch](http://clojureelasticsearch.info).

### Memcached, Couchbase, Kestrel

  * [Spyglass](http://clojurememcached.info): Spyglass is a very fast Clojure client for Memcached and Couchbase

### Apache Cassandra

  * [Cassaforte](http://github.com/clojurewerkz/cassaforte): A young Clojure client for Apache Cassandra



## I/O

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


## Graphics

  * [Quil](https://clojars.org/quil): For making drawings, animations,
    and artwork ([some
    examples](https://github.com/quil/quil/blob/master/examples/gen_art/README.md)). Wraps
    the ["Processing"](http://www.processing.org/) graphics environment.


### GUI (Graphical User Interface)

  * [seesaw](https://clojars.org/seesaw): A Swing wrapper/DSL.



## Documentation

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
