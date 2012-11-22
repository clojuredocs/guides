---
title: "A Directory of Clojure Libraries"
layout: article
---

This is a categorized and annotated directory of available Clojure
libraries and tools. This directory is **not comprehensive and is necessarily highly opinionated**.

This directory is manually curated by the Clojure community. Please endeavor to keep it up-to-date,
consisting of **high quality** libraries with adequate documentation. There are many more libraries in the Clojure
ecosystem, but some lack documentation and/or are useful primarily to experienced developers and such projects
are not included in this document.

For more comprehensive overview of the Clojure library ecosystem, please see [ClojureSphere](http://clojuresphere.com/).


## Support Libraries

### General

  * [useful](https://clojars.org/useful)

  * [ClojureWerkz Support](http://github.com/clojurewerkz/support) ([at clojars](https://clojars.org/clojurewerkz/support))


## Applications & Environment

  * [tools.cli](https://github.com/clojure/tools.cli): a command line argument parser for Clojure

  * [environ](https://clojars.org/environ): Manage environment settings from a number of different sources

  * [carica](https://github.com/sonian/carica) ([at clojars](https://clojars.org/sonian/carica)):
    Flexible config file usage & management library.


## Date and Time

  * [clj-time](https://clojars.org/clj-time): A date and time library for Clojure


## Testing

  * [clojure.test](http://clojure.github.com/clojure/clojure.test-api.html): the standard unit testing library that ships with Clojure

  * [Midje](https://clojars.org/midje): a more featureful test framework

  * [expectations](https://github.com/jaycfields/expectations) ([at clojars](https://clojars.org/expectations)): a minimalist's testing framework

  * [test.generative](https://github.com/clojure/test.generative): generative testing, a la QuickCheck


## Namespaces and Code-as-Data

 * [tools.namespace](https://github.com/clojure/tools.namespace)

 * [builtitude](https://github.com/Raynes/bultitude) ([at clojars](https://clojars.org/bultitude))

 * [findfn](https://github.com/Raynes/findfn) ([at clojars](https://clojars.org/findfn))

 * [ns-tracker](https://github.com/weavejester/ns-tracker) ([at clojars](https://clojars.org/ns-tracker))



## Serialization

### JSON

  * [cheshire](https://github.com/dakrone/cheshire/) ([at clojars](https://clojars.org/cheshire)): very efficient Clojure JSON and SMILE (binary JSON) encoding/decoding.

  * [data.json](https://github.com/clojure/data.json): JSON parser/generator to/from Clojure data structures.

### Protocol Buffers

  * [clojure-protobuf](https://github.com/flatland/clojure-protobuf) ([at clojars](https://clojars.org/protobuf)): a Clojure interface to Google's protocol buffers

### Kryo

  * [carbonite](https://github.com/revelytix/carbonite)

### Clojure Reader

  * [Nippy](https://github.com/ptaoussanis/nippy) ([at clojars](https://clojars.org/nippy)): a more efficient implementation of the Clojure reader

### XML

  * [data.xml](https://github.com/clojure/data.xml): a library for reading and writing XML

### Binary Formats

  * [gloss](https://github.com/ztellman/gloss) ([at clojars](https://clojars.org/gloss)): turns complicated byte formats into Clojure data structures


## File formats

  * [clj-pdf](https://clojars.org/clj-pdf): a library for easily generating PDFs from Clojure

  * [Pantomime](http://github.com/michaelklishin/pantomime) ([at clojars](https://clojars.org/pantomime)): a tiny Clojure library that deals with Internet media types (MIME types) and content type detection

  * [data.csv](https://github.com/clojure/data.csv): a CSV parser


## Templating

  * [Stencil](https://clojars.org/stencil): [Mustache](http://mustache.github.com/) for Clojure (logic-less templates). Fast.

  * [Clostache](https://clojars.org/de.ubercode.clostache/clostache): another nice [Mustache](http://mustache.github.com/) implementation



## HTTP

### Client

  * [clj-http](https://github.com/dakrone/clj-http) ([at clojars](https://clojars.org/clj-http)): An idiomatic Clojure http client wrapping the apache client.

  * [clj-http-lite](https://github.com/hiredman/clj-http-lite) ([at clojars](https://clojars.org/clj-http-lite)): A lightweight version of clj-http having almost same API, but without any Apache dependencies.

## Logging

  * [Timbre](https://clojars.org/com.taoensso/timbre):
    Simple, flexible, all-Clojure logging. No XML!

  * [tools.logging](https://github.com/clojure/tools.logging/): standard general-purpose logging.

  * [clj-log](https://clojars.org/clj-log): s-expression logger.



## Web Development

### Web Services

  * [Noir](http://webnoir.org/) ([at clojars](https://clojars.org/noir)): a popular Clojure Web framework suitable for services that generate HTML and pure API endpoints

  * [compojure](https://github.com/weavejester/compojure) ([at clojars](https://clojars.org/compojure)): A concise routing library for Ring

  * [Liberator](https://github.com/clojure-liberator/liberator) ([at clojars](https://clojars.org/liberator)): a Clojure library for building RESTful applications

  * [ring](https://github.com/ring-clojure) ([at clojars](https://clojars.org/ring)): foundational web application library

  * [friend](https://github.com/cemerick/friend) ([at clojars](https://clojars.org/com.cemerick/friend)): Authentication and authorization library for Web apps


### HTML Generation

  * [hiccup](https://clojars.org/hiccup): Generates HTML from Clojure data structures.

  * [markdown-clj](https://clojars.org/markdown-clj): Clojure based Markdown parsers for both Clojure and ClojureScript.


### HTML Parsers

  * [Crouton](https://clojars.org/crouton): A Clojure wrapper for the JSoup HTML and XML parser that handles real world inputs

  * [Crawlista](http://github.com/michaelklishin/crawlista) ([at clojars](https://clojars.org/clojurewerkz/crawlista)): a support library for applications that crawl the Web

  * [TagSoup](http://home.ccil.org/~cowan/XML/tagsoup/): a tool for parsing html as it's found in the wild: poor, nasty, and brutish.


### Data Validation

  * [Validateur](http://clojurevalidations.info) ([at clojars](https://clojars.org/com.novemberain/validateur)): functional validations library inspired by Ruby's ActiveModel


### URIs, URLs

  * [Urly](http://github.com/michaelklishin/urly) ([at clojars](https://clojars.org/clojurewerkz/urly)): unifies `java.net.URL`, `java.net.URI` and string URIs, provides parsing and manipulation helpers

  * [Exploding Fish](https://github.com/wtetzner/exploding-fish) ([at clojars](https://clojars.org/org.bovinegenius/exploding-fish)): a URI library for Clojure

  * [route-one](https://github.com/clojurewerkz/route-one) ([at clojars](https://clojars.org/clojurewerkz/route-one)): a tiny Clojure library that generates HTTP resource routes (as in Ruby on Rails, Jersey, and so on)


### Internationalization (i18n), Localization (l10n)

  * [Tower](https://github.com/ptaoussanis/tower) ([at clojars](https://clojars.org/tower)): a simple, idiomatic internationalization and localization story for Clojure


### RSS

  * [clj-rss](https://clojars.org/clj-rss): RSS feed generation library



## Data Stores

### Relational Databases, JDBC

  * [java.jdbc](https://github.com/clojure/java.jdbc): Basic wrapper for JDBC. Works with all JDBC databases (MySQL, PostgreSQL, Oracle, SQL Server, etc).

  * [Korma](https://clojars.org/korma): ["Tasty SQL for Clojure"](http://sqlkorma.com/docs)

### CouchDB

  * [Clutch](https://github.com/clojure-clutch/clutch) ([at clojars](https://clojars.org/com.ashafa/clutch)): [Apache CouchDB](http://couchdb.apache.org/) client.

### MongoDB

  * [Monger](https://clojuremongodb.info) ([at clojars](https://clojars.org/com.novemberain/monger)): Monger is an idiomatic Clojure MongoDB driver for a more civilized age with solid documentation

  * [congomongo](https://github.com/aboekhoff/congomongo) ([at clojars](https://clojars.org/congomongo)): Basic wrapper for the MongoDB Java driver

  * [Mongoika](https://github.com/yuushimizu/Mongoika) ([at clojars](https://clojars.org/mongoika))

### Riak

  * [Welle](https://clojureriak.info) ([at clojars](https://clojars.org/com.novemberain/welle)): An expressive Clojure client for Riak with solid documentation

### Redis

  * [Carmine](https://github.com/ptaoussanis/carmine) ([at clojars](https://clojars.org/com.taoensso/carmine)): a great Clojure client for Redis

### Neo4J

  * [Neocons](https://clojureneo4j.info) ([at clojars](https://clojars.org/clojurewerkz/neocons)): Neocons is a feature rich idiomatic [Clojure client for the Neo4J REST API](http://clojureneo4j.info)  with solid documentation

  * [Borneo](https://github.com/wagjo/borneo) ([at clojars](https://clojars.org/borneo))

### ElasticSearch

  * [Elastisch](http://clojureelasticsearch.info) ([at clojars](https://clojars.org/clojurewerkz/elastisch)): Elastisch is a minimalistic Clojure client for [ElasticSearch](http://elasticsearch.org) with solid documentation.

  * [Esperanto](https://github.com/drewr/esperanto)

### Memcached, Couchbase, Kestrel

  * [Spyglass](http://clojurememcached.info) ([at clojars](https://clojars.org/clojurewerkz/spyglass)): Spyglass is a very fast Clojure client for Memcached and Couchbase with solid documentation

### Apache Cassandra

  * [Cassaforte](http://github.com/clojurewerkz/cassaforte) ([at clojars](https://clojars.org/clojurewerkz/cassaforte)): A young Clojure client for Apache Cassandra

  * [clj-hector](https://github.com/pingles/clj-hector) ([at clojars](https://clojars.org/org.clojars.paul/clj-hector)): A simple Clojure client for Cassandra that wraps Hector

### Amazon DynamoDB

  * [Rotary](https://github.com/weavejester/rotary) ([at clojars](https://clojars.org/rotary))

### Tokyo Cabinet

  * [tokyocabinet](https://github.com/flatland/tokyocabinet) ([at clojars](https://clojars.org/tokyocabinet)): native Tokyo Cabinet interface for Clojure

### Misc

  * [masai](https://github.com/flatland/masai) ([at clojars](https://clojars.org/masai)): a very simple interface to a number of key-value stores

  * [jiraph](https://github.com/flatland/jiraph) ([at clojars](https://clojars.org/jiraph)): a reasonably licensed embedded graph database with swappable backends



## Networking

 * [Lamina](https://github.com/ztellman/lamina) ([at clojars](https://clojars.org/lamina)): event-driven workflows in Clojure

 * [Aleph](https://github.com/ztellman/aleph) ([at clojars](https://clojars.org/aleph)): asynchronous communication in Clojure


## Application Servers

 * [Immutant](http://immutant.org/) ([at clojars](https://clojars.org/org.immutant/immutant)): a feature rich and integrated application platform for Clojure from Red Hat


## Messaging

### RabbitMQ

 * [Langohr](http://clojurerabbitmq.info) ([at clojars](https://clojars.org/com.novemberain/langohr)): a feature complete RabbitMQ client that embraces AMQP 0.9.1 model and learns from others

### ZeroMQ

 * [Jilch](https://github.com/mpenet/jilch) ([at clojars](https://clojars.org/jilch)): Clojure ZeroMQ Library using JeroMQ, no native dependencies

### Beanstalk

 * [beanstalk](https://github.com/drsnyder/beanstalk) ([at clojars](https://clojars.org/com.github.drsnyder/beanstalk)): a Beanstalkd client

### Amazon SQS

 * [Bandalore](https://github.com/cemerick/bandalore): a Clojure client library for Amazon's Simple Queue Service

### HornetQ

 * [hornetq-clj](https://github.com/hugoduncan/hornetq-clj) ([at clojars](https://clojars.org/hornetq-clj/client)): a tiny HornetQ client


## Data Processing, Computation

 * [Twitter Storm](http://storm-project.net/) ([at clojars](https://clojars.org/storm)): distributed realtime computation system

 * [Cascalog](http://www.cascalog.org/) ([at clojars](https://clojars.org/cascalog)): data processing on Hadoop without the hassle



## Natural Language Processing

 * [clojure-opennlp](https://github.com/dakrone/clojure-opennlp) ([at clojars](https://clojars.org/clojure-opennlp))



## Automation, Provisioning, DevOps Tools

 * [pallet](http://palletops.com/) ([at clojars](https://clojars.org/pallet)): a platform for agile and programmatic automation of infrastructure

 * [jclouds](http://www.jclouds.org/): unified APIs for dozens of cloud (IaaS) providers

 * [clj-ssh](https://github.com/hugoduncan/clj-ssh) ([at clojars](https://clojars.org/clj-ssh)): an SSH client

 * [ssh-transport](https://github.com/pallet/ssh-transport): executes commands over SSH


## Monitoring, metrics

 * [clj-statsd](https://github.com/pyr/clj-statsd) ([at clojars](https://clojars.org/clj-statsd)): simple client library to interface with statsd

 * [pulse](https://github.com/heroku/pulse): Real-time Heroku operations dashboard

 * [riemann](https://github.com/aphyr/riemann) ([at clojars](https://clojars.org/riemann)): A network event stream processing system, in Clojure.


## I/O

### Files

File I/O is covered by the JDK and commonly used via `clojure.java.io` functions.

  * [fs](https://clojars.org/fs): utilities for working with the file system


### Standard Streams, Subprocesses

Standard streams I/O is covered by the JDK and commonly used via `clojure.java.io` functions.

  * clojure.java.shell: Conveniently launch a sub-process providing
    its stdin and collecting its stdout.

  * [conch](https://clojars.org/conch): for shelling out to external programs.
    An alternative to clojure.java.shell.


### Property Files

  * [propertea](https://github.com/jaycfields/propertea) ([at clojars](https://clojars.org/propertea)): painlessly work with property files


### REPL and Terminal

  * [REPLy](https://github.com/trptcolin/reply) ([at clojars](https://clojars.org/reply)): a Swiss army knife of interactive editing, and better REPL for Clojure

  * [clojure-lanterna](https://clojars.org/clojure-lanterna) ([at clojars](https://clojars.org/clojure-lanterna)): for creating TUIs (terminal-based user-interfaces), like ncurses.



## Concurrency and Parallelism

  * [java.util.concurrent](http://docs.oracle.com/javase/7/docs/technotes/guides/concurrency/index.html): a comprehensive, very mature set of concurrency primitives built into the JDK
  * [Okku](https://github.com/gaverhae/okku) ([at clojars](https://clojars.org/org.clojure.gaverhae/okku)): Akka API for Clojure



## Mathematics

  * [math.numeric-tower](https://github.com/clojure/math.numeric-tower): various utility math functions

  * [math.combinatorics](https://github.com/clojure/math.combinatorics) common combinatorial functions



## Email

  * [Postal](https://github.com/drewr/postal) ([at clojars](https://clojars.org/com.draines/postal)): generate and send email with Clojure

  * [Mailer](https://github.com/clojurewerkz/mailer) ([at clojars](https://clojars.org/clojurewerkz/mailer)): generate and send email using Postal and Moustache templates



## Data Structures and Algorithms

### Strings

  * clojure.string

### Sets

  * clojure.set

### Caching

  * [core.cache](https://github.com/clojure/core.cache): the Clojure API for various cache implementations

### UUIDs

  * [tardis](https://github.com/mpenet/tardis) ([at clojars](https://clojars.org/cc.qbits/tardis)): manages Type 1 UUIDs (time based)

### Monads

  * [algo.monads](https://github.com/clojure/algo.monads): macros for defining monads, and definition of the most common monads

  * [protocol-monads](https://github.com/jduey/protocol-monads): A protocol based monad implementation for clojure

### Memoization

  * [core.memoize](https://github.com/clojure/core.memoize)

### Other

  * [vclock](https://github.com/michaelklishin/vclock) ([at clojars](https://clojars.org/clojurewerkz/vclock)): a vector clocks implementation

  * [chash](https://github.com/michaelklishin/chash) ([at clojars](https://clojars.org/clojurewerkz/chash)): a consistent hashing library


## Scheduling

  * [Quartzite](http://clojurequartz.info) ([at clojars](https://clojars.org/clojurewerkz/quartzite)): a powerful scheduling library


## Graphics and GUI

  * [Quil](https://clojars.org/quil): For making drawings, animations,
    and artwork ([some examples](https://github.com/quil/quil/blob/master/examples/gen_art/README.md)). Wraps
    the ["Processing"](http://www.processing.org/) graphics environment.

  * [seesaw](http://seesaw-clj.org/) ([at clojars](https://clojars.org/seesaw)): A Swing wrapper/DSL.

  * [clisk](https://github.com/mikera/clisk): Clisk is a DSL-based library for procedural image generation that can be used from Clojure and Java. 

## Security and Sandboxing

  * [Clojail](https://github.com/flatland/clojail) ([at clojars](https://clojars.org/clojail)): a [code execution] sandboxing library


## Documentation

### Literate Programming

  * [Marginalia](https://github.com/fogus/marginalia) ([at clojars](https://clojars.org/marginalia)): literate programming implementation for Clojure. See [the Marginalia
    site](http://fogus.me/fun/marginalia/) for an example.

  * [Marginalia Leiningen plug-in](https://github.com/fogus/lein-marginalia) ([at clojars](https://clojars.org/lein-marginalia))


### Generating API Reference

  * [Codox](https://github.com/weavejester/codox) ([at clojars](https://clojars.org/codox)): from the author of Compojure. See [compojure
    api docs](http://weavejester.github.com/compojure/) for an
    example.

  * [Autodoc](http://tomfaulhaber.github.com/autodoc/) ([at clojars](https://clojars.org/autodoc)): used
    to generate the official [Clojure API reference](http://clojure.github.com/).


## Tooling

 * [Leiningen](http://leiningen.org): the Clojure build tool

 * [tools.nrepl](https://github.com/clojure/tools.nrepl): nREPL interface

 * [java.jmx](https://github.com/clojure/java.jmx): nice JMX interface

 * [test.benchmark](https://github.com/clojure/test.benchmark): a benchmarking library

 * [tools.trace](https://github.com/clojure/tools.trace): a tracing library

 * [criterium](https://github.com/hugoduncan/criterium) ([at clojars](https://clojars.org/criterium)): a benchmarking library that tries to address common benchmarking pitfalls
