---
title: "A Directory of Clojure Libraries"
layout: article
---

## Overview

This is a categorized and annotated directory of available Clojure
libraries and tools. This directory is **not comprehensive and highly opinionated**.

This directory is manually curated by the Clojure community. Please endeavor to keep it up-to-date,
consisting of **high quality** libraries with adequate documentation. There are many more libraries in the Clojure
ecosystem, but some lack documentation and/or are useful primarily to experienced developers. Such projects
are not included in this document.

For more comprehensive overview of the Clojure library ecosystem, please see [ClojureSphere](http://clojuresphere.com/).


## Support Libraries

### General

  * [useful](https://clojars.org/useful)

  * [ClojureWerkz Support](http://github.com/clojurewerkz/support) <sub>[clojars](https://clojars.org/clojurewerkz/support)</sub>


## Applications & Environment

  * [tools.cli](https://github.com/clojure/tools.cli): a command line argument parser for Clojure

  * [environ](https://clojars.org/environ): Manage environment settings from a number of different sources


## Date and Time

  * [clj-time](https://clojars.org/clj-time): A date and time library for Clojure


## Testing

  * [clojure.test](http://clojure.github.com/clojure/clojure.test-api.html): the standard unit testing library that ships with Clojure

  * [Midje](https://clojars.org/midje): a more featureful test framework

  * [expectations](https://github.com/jaycfields/expectations) <sub>[clojars](https://clojars.org/expectations)</sub>: a minimalist's testing framework

  * [test.generative](https://github.com/clojure/test.generative): generative testing, a la QuickCheck


## Namespaces and Code-as-Data

 * [tools.namespace](https://github.com/clojure/tools.namespace)

 * [builtitude](https://github.com/Raynes/bultitude) <sub>[clojars](https://clojars.org/bultitude)</sub>

 * [findfn](https://github.com/Raynes/findfn) <sub>[clojars](https://clojars.org/findfn)</sub>

 * [ns-tracker](https://github.com/weavejester/ns-tracker) <sub>[clojars](https://clojars.org/ns-tracker)</sub>



## Serialization

### JSON

  * [cheshire](https://github.com/dakrone/cheshire/) <sub>[clojars](https://clojars.org/cheshire)</sub>: very efficient Clojure JSON and SMILE (binary JSON) encoding/decoding.

  * [data.json](https://github.com/clojure/data.json): JSON parser/generator to/from Clojure data structures.

### Protocol Buffers

  * [clojure-protobuf](https://github.com/flatland/clojure-protobuf) <sub>[clojars](https://clojars.org/protobuf)</sub>: a Clojure interface to Google's protocol buffers

### Kryo

  * [carbonite](https://github.com/revelytix/carbonite)

### Clojure Reader

  * [Nippy](https://github.com/ptaoussanis/nippy) <sub>[clojars](https://clojars.org/nippy)</sub>: a more efficient implementation of the Clojure reader

### XML

  * [data.xml](https://github.com/clojure/data.xml): a library for reading and writing XML

### Binary Formats

  * [gloss](https://github.com/ztellman/gloss) <sub>[clojars](https://clojars.org/gloss)</sub>: turns complicated byte formats into Clojure data structures


## File formats

  * [clj-pdf](https://clojars.org/clj-pdf): a library for easily generating PDFs from Clojure

  * [Pantomime](http://github.com/michaelklishin/pantomime) <sub>[clojars](https://clojars.org/pantomime)</sub>: a tiny Clojure library that deals with Internet media types (MIME types) and content type detection

  * [data.csv](https://github.com/clojure/data.csv): a CSV parser


## Templating

  * [Stencil](https://clojars.org/stencil): [Mustache](http://mustache.github.com/) for Clojure (logic-less templates). Fast.

  * [Clostache](https://clojars.org/de.ubercode.clostache/clostache): another nice [Mustache](http://mustache.github.com/) implementation



## HTTP

### Client

  * [clj-http](https://github.com/dakrone/clj-http) <sub>[clojars](https://clojars.org/clj-http)</sub>: An idiomatic Clojure http client wrapping the apache client.

  * [clj-http-lite](https://github.com/hiredman/clj-http-lite) <sub>[clojars](https://clojars.org/clj-http-lite)</sub>: A lightweight version of clj-http having almost same API, but without any Apache dependencies.

## Logging

  * [Timbre](https://clojars.org/com.taoensso/timbre):
    Simple, flexible, all-Clojure logging. No XML!

  * [tools.logging](https://github.com/clojure/tools.logging/)
    {contrib}: standard general-purpose logging.

  * [clj-log](https://clojars.org/clj-log): s-expression logger.



## Web Development

### Web Services

  * [Noir](http://webnoir.org/) <sub>[clojars](https://clojars.org/noir)</sub>: a popular Clojure Web framework suitable for services that generate HTML and pure API endpoints

  * [compojure](https://github.com/weavejester/compojure) <sub>[clojars](https://clojars.org/compojure)</sub>: A concise routing library for Ring

  * [Liberator](https://github.com/clojure-liberator/liberator) <sub>[clojars](https://clojars.org/liberator)</sub>: a Clojure library for building RESTful applications

  * [ring](https://github.com/ring-clojure) <sub>[clojars](https://clojars.org/ring)</sub>: foundational web application library

  * [friend](https://github.com/cemerick/friend) <sub>[clojars](https://clojars.org/com.cemerick/friend)</sub>: Authentication and authorization library for Web apps


### HTML Generation

  * [hiccup](https://clojars.org/hiccup): Generates HTML from Clojure data structures.

  * [markdown-clj](https://clojars.org/markdown-clj): Clojure based Markdown parsers for both Clojure and ClojureScript.


### HTML Parsers

  * [Crouton](https://clojars.org/crouton): A Clojure wrapper for the JSoup HTML and XML parser that handles real world inputs

  * [Crawlista](http://github.com/michaelklishin/crawlista) <sub>[clojars](https://clojars.org/clojurewerkz/crawlista)</sub>: a support library for applications that crawl the Web

  * [TagSoup](http://home.ccil.org/~cowan/XML/tagsoup/): a tool for parsing html as it's found in the wild: poor, nasty, and brutish.


### Data Validation

  * [Validateur](http://clojurevalidations.info) <sub>[clojars](https://clojars.org/com.novemberain/validateur)</sub>: functional validations library inspired by Ruby's ActiveModel


### URIs, URLs

  * [Urly](http://github.com/michaelklishin/urly) <sub>[clojars](https://clojars.org/clojurewerkz/urly)</sub>: unifies `java.net.URL`, `java.net.URI` and string URIs, provides parsing and manipulation helpers

  * [Exploding Fish](https://github.com/wtetzner/exploding-fish) <sub>[clojars](https://clojars.org/org.bovinegenius/exploding-fish)</sub>: a URI library for Clojure

  * [route-one](https://github.com/clojurewerkz/route-one) <sub>[clojars](https://clojars.org/clojurewerkz/route-one)</sub>: a tiny Clojure library that generates HTTP resource routes (as in Ruby on Rails, Jersey, and so on)


### Internationalization (i18n), Localization (l10n)

  * [Tower](https://github.com/ptaoussanis/tower) <sub>[clojars](https://clojars.org/tower)</sub>: a simple, idiomatic internationalization and localization story for Clojure


### RSS

  * [clj-rss](https://clojars.org/clj-rss): RSS feed generation library



## Data Stores

### Relational Databases, JDBC

  * [java.jdbc](https://github.com/clojure/java.jdbc) {contrib}: Basic wrapper for JDBC. Works with all JDBC databases (MySQL, PostgreSQL, Oracle, SQL Server, etc).

  * [Korma](https://clojars.org/korma): ["Tasty SQL for Clojure"](http://sqlkorma.com/docs)

### CouchDB

  * [Clutch](https://github.com/clojure-clutch/clutch) <sub>[clojars](https://clojars.org/com.ashafa/clutch)</sub>: [Apache CouchDB](http://couchdb.apache.org/) client.

### MongoDB

  * [Monger](https://clojuremongodb.info) <sub>[clojars](https://clojars.org/com.novemberain/monger)</sub>: Monger is an idiomatic Clojure MongoDB driver for a more civilized age with solid documentation

  * [congomongo](https://github.com/aboekhoff/congomongo) <sub>[clojars](https://clojars.org/congomongo)</sub>: Basic wrapper for the MongoDB Java driver

  * [Mongoika](https://github.com/yuushimizu/Mongoika) <sub>[clojars](https://clojars.org/mongoika)</sub>

### Riak

  * [Welle](https://clojureriak.info) <sub>[clojars](https://clojars.org/com.novemberain/welle)</sub>: An expressive Clojure client for Riak with solid documentation

### Redis

  * [Carmine](https://github.com/ptaoussanis/carmine) <sub>[clojars](https://clojars.org/com.taoensso/carmine)</sub>: a great Clojure client for Redis

### Neo4J

  * [Neocons](https://clojureneo4j.info) <sub>[clojars](https://clojars.org/clojurewerkz/neocons)</sub>: Neocons is a feature rich idiomatic [Clojure client for the Neo4J REST API](http://clojureneo4j.info)  with solid documentation

  * [Borneo](https://github.com/wagjo/borneo) <sub>[clojars](https://clojars.org/borneo)</sub>

### ElasticSearch

  * [Elastisch](http://clojureelasticsearch.info) <sub>[clojars](https://clojars.org/clojurewerkz/elastisch)</sub>: Elastisch is a minimalistic Clojure client for [ElasticSearch](http://elasticsearch.org) with solid documentation.

  * [Esperanto](https://github.com/drewr/esperanto)

### Memcached, Couchbase, Kestrel

  * [Spyglass](http://clojurememcached.info) <sub>[clojars](https://clojars.org/clojurewerkz/spyglass)</sub>: Spyglass is a very fast Clojure client for Memcached and Couchbase with solid documentation

### Apache Cassandra

  * [Cassaforte](http://github.com/clojurewerkz/cassaforte) <sub>[clojars](https://clojars.org/clojurewerkz/cassaforte)</sub>: A young Clojure client for Apache Cassandra

  * [clj-hector](https://github.com/pingles/clj-hector) <sub>[clojars](https://clojars.org/org.clojars.paul/clj-hector)</sub>: A simple Clojure client for Cassandra that wraps Hector

### Amazon DynamoDB

  * [Rotary](https://github.com/weavejester/rotary) <sub>[clojars](https://clojars.org/rotary)</sub>

### Tokyo Cabinet

  * [tokyocabinet](https://github.com/flatland/tokyocabinet) <sub>[clojars](https://clojars.org/tokyocabinet)</sub>: native Tokyo Cabinet interface for Clojure

### Misc

  * [masai](https://github.com/flatland/masai) <sub>[clojars](https://clojars.org/masai)</sub>: a very simple interface to a number of key-value stores

  * [jiraph](https://github.com/flatland/jiraph) <sub>[clojars](https://clojars.org/jiraph)</sub>: a reasonably licensed embedded graph database with swappable backends



## Networking

 * [Lamina](https://github.com/ztellman/lamina) <sub>[clojars](https://clojars.org/lamina)</sub>: event-driven workflows in Clojure

 * [Aleph](https://github.com/ztellman/aleph) <sub>[clojars](https://clojars.org/aleph)</sub>: asynchronous communication in Clojure


## Application Servers

 * [Immutant](http://immutant.org/) <sub>[clojars](https://clojars.org/org.immutant/immutant)</sub>: a feature rich and integrated application platform for Clojure from Red Hat


## Messaging

### RabbitMQ

 * [Langohr](http://clojurerabbitmq.info) <sub>[clojars](https://clojars.org/com.novemberain/langohr)</sub>: a feature complete RabbitMQ client that embraces AMQP 0.9.1 model and learns from others

### ZeroMQ

 * [Jilch](https://github.com/mpenet/jilch) <sub>[clojars](https://clojars.org/jilch)</sub>: Clojure ZeroMQ Library using JeroMQ, no native dependencies

### Beanstalk

 * [beanstalk](https://github.com/drsnyder/beanstalk) <sub>[clojars](https://clojars.org/com.github.drsnyder/beanstalk)</sub>: a Beanstalkd client

### Amazon SQS

 * [Bandalore](https://github.com/cemerick/bandalore): a Clojure client library for Amazon's Simple Queue Service

### HornetQ

 * [hornetq-clj](https://github.com/hugoduncan/hornetq-clj) <sub>[clojars](https://clojars.org/hornetq-clj/client)</sub>: a tiny HornetQ client


## Data Processing, Computation

 * [Twitter Storm](http://storm-project.net/) <sub>[clojars](https://clojars.org/storm)</sub>: distributed realtime computation system

 * [Cascalog](http://www.cascalog.org/) <sub>[clojars](https://clojars.org/cascalog)</sub>: data processing on Hadoop without the hassle



## Natural Language Processing

 * [clojure-opennlp](https://github.com/dakrone/clojure-opennlp) <sub>[clojars](https://clojars.org/clojure-opennlp)</sub>



## Automation, Provisioning, DevOps Tools

 * [pallet](http://palletops.com/) <sub>[clojars](https://clojars.org/pallet)</sub>: a platform for agile and programmatic automation of infrastructure

 * [jclouds](http://www.jclouds.org/): unified APIs for dozens of cloud (IaaS) providers

 * [clj-ssh](https://github.com/hugoduncan/clj-ssh) <sub>[clojars](https://clojars.org/clj-ssh)</sub>: an SSH client

 * [ssh-transport](https://github.com/pallet/ssh-transport): executes commands over SSH


## Monitoring, metrics

 * [clj-statsd](https://github.com/pyr/clj-statsd) <sub>[clojars](https://clojars.org/clj-statsd)</sub>: simple client library to interface with statsd

 * [pulse](https://github.com/heroku/pulse): Real-time Heroku operations dashboard

 * [riemann](https://github.com/aphyr/riemann) <sub>[clojars](https://clojars.org/riemann)</sub>: A network event stream processing system, in Clojure.


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

  * [propertea](https://github.com/jaycfields/propertea) <sub>[clojars](https://clojars.org/propertea)</sub>: painlessly work with property files


### REPL and Terminal

  * [REPLy](https://github.com/trptcolin/reply) <sub>[clojars](https://clojars.org/reply)</sub>: a Swiss army knife of interactive editing, and better REPL for Clojure

  * [clojure-lanterna](https://clojars.org/clojure-lanterna) <sub>[clojars](https://clojars.org/clojure-lanterna)</sub>: for creating TUIs (terminal-based user-interfaces), like ncurses.



## Concurrency and Parallelism

  * [java.util.concurrent](http://docs.oracle.com/javase/7/docs/technotes/guides/concurrency/index.html): a comprehensive, very mature set of concurrency primitives built into the JDK
  * [Okku](https://github.com/gaverhae/okku) <sub>[clojars](https://clojars.org/org.clojure.gaverhae/okku)</sub>: Akka API for Clojure



## Mathematics

  * [math.numeric-tower](https://github.com/clojure/math.numeric-tower): various utility math functions

  * [math.combinatorics](https://github.com/clojure/math.combinatorics) common combinatorial functions



## Email

  * [Postal](https://github.com/drewr/postal) <sub>[clojars](https://clojars.org/com.draines/postal)</sub>: generate and send email with Clojure

  * [Mailer](https://github.com/clojurewerkz/mailer) <sub>[clojars](https://clojars.org/clojurewerkz/mailer)</sub>: generate and send email using Postal and Moustache templates



## Data Structures and Algorithms

### Strings

  * clojure.string

### Sets

  * clojure.set

### Caching

  * [core.cache](https://github.com/clojure/core.cache): the Clojure API for various cache implementations

### UUIDs

  * [tardis](https://github.com/mpenet/tardis) <sub>[clojars](https://clojars.org/cc.qbits/tardis)</sub>: manages Type 1 UUIDs (time based)

### Monads

  * [algo.monads](https://github.com/clojure/algo.monads): macros for defining monads, and definition of the most common monads

  * [protocol-monads](https://github.com/jduey/protocol-monads): A protocol based monad implementation for clojure

### Memoization

  * [core.memoize](https://github.com/clojure/core.memoize)

### Other

  * [vclock](https://github.com/michaelklishin/vclock) <sub>[clojars](https://clojars.org/clojurewerkz/vclock)</sub>: a vector clocks implementation

  * [chash](https://github.com/michaelklishin/chash) <sub>[clojars](https://clojars.org/clojurewerkz/chash)</sub>: a consistent hashing library


## Scheduling

  * [Quartzite](http://clojurequartz.info) <sub>[clojars](https://clojars.org/clojurewerkz/quartzite)</sub>: a powerful scheduling library


## Graphics and GUI

  * [Quil](https://clojars.org/quil): For making drawings, animations,
    and artwork ([some examples](https://github.com/quil/quil/blob/master/examples/gen_art/README.md)). Wraps
    the ["Processing"](http://www.processing.org/) graphics environment.

  * [seesaw](http://seesaw-clj.org/) <sub>[clojars](https://clojars.org/seesaw)</sub>: A Swing wrapper/DSL.


## Security and Sandboxing

  * [Clojail](https://github.com/flatland/clojail) <sub>[clojars](https://clojars.org/clojail)</sub>: a [code execution] sandboxing library


## Documentation

### Literate Programming

  * [Marginalia](https://github.com/fogus/marginalia) <sub>[clojars](https://clojars.org/marginalia)</sub>: literate programming implementation for Clojure. See [the Marginalia
    site](http://fogus.me/fun/marginalia/) for an example.

  * [Marginalia Leiningen plug-in](https://github.com/fogus/lein-marginalia) <sub>[clojars](https://clojars.org/lein-marginalia)</sub>


### Generating API Reference

  * [Codox](https://github.com/weavejester/codox) <sub>[clojars](https://clojars.org/codox)</sub>: from the author of Compojure. See [compojure
    api docs](http://weavejester.github.com/compojure/) for an
    example.

  * [Autodoc](http://tomfaulhaber.github.com/autodoc/) <sub>[clojars](https://clojars.org/autodoc)</sub>: used
    to generate the official [Clojure API reference](http://clojure.github.com/).


## Tooling

 * [Leiningen](http://leiningen.org): the Clojure build tool

 * [tools.nrepl](https://github.com/clojure/tools.nrepl): nREPL interface

 * [java.jmx](https://github.com/clojure/java.jmx): nice JMX interface

 * [test.benchmark](https://github.com/clojure/test.benchmark): a benchmarking library

 * [tools.trace](https://github.com/clojure/tools.trace): a tracing library

 * [criterium](https://github.com/hugoduncan/criterium) <sub>[clojars](https://clojars.org/criterium)</sub>: a benchmarking library that tries to address common benchmarking pitfalls
