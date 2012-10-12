---
title: "Listing of All CDS Documents"
layout: article
---

## Overview

[CDS](https://github.com/clojuredocs/cds) is a categorized and
manifold collection of guides for the Clojure programming language and
its ecosystem.

CDS recognizes that different Clojure users have different level of expertise
and separates content into several groups:

 * Tutorials
 * Core language guides
 * Ecosystem (tools, libraries, community, books) guides
 * Cookbooks


## Tutorials

### [Getting Started](/articles/tutorials/getting_started.html) (Partially Complete)

If you're new to Clojure, this is a good place to start.


### [Introduction](/articles/tutorials/introduction.html) (Partially Complete)

A swift introduction to the Clojure language, covering most of the
basics.



## Clojure Language Guides

### [Functions](/articles/language/functions.html) (Partially Complete)

Functions are at the heart of Clojure.

This guide covers:

 * How to define functions
 * How to invoke functions
 * Multi-arity functions
 * Variadic functions
 * Higher order functions
 * Other topics related to functions


### [clojure.core Overview](/articles/language/core_overview.html) (In Progress)

`clojure.core` is the core Clojure library.

This guide covers:

 * Key functions of `clojure.core`
 * Key macros of `clojure.core`
 * Key vars of `clojure.core`


### [Interoperability with Java](/articles/language/interop.html) (Partially Complete)

The Clojure language implementation is symbiotic with its host
platform (the JVM), providing direct interoperability.

This guide covers:

 * How to instantiate Java classes
 * How to invoke Java methods
 * How to extend Java classes with proxy
 * How to implement Java interfaces with reify
 * How to generate Java classes with gen-class
 * Other topics related to interop


### [Namespaces](/articles/language/namespaces.html) (In Progress)

Namespaces organize Clojure functions.

This guide covers:

 * Clojure namespaces
 * How to use functions in other namespaces
 * `require` vs `refer` vs `use`
 * How code compilation works in Clojure


### [Polymorphism: Protocols and Multimethods](/articles/language/polymorphism.html) (Partially Complete)

This guide covers:

 * What are polymorphic functions
 * Type-based polymoprhism with protocols
 * Ad-hoc polymorphism with multimethods
 * How to create your own data types that behave like core Clojure data types


### [Collections and Sequences](/articles/language/sequences.html) (TBD)

This guide covers:

 * Collections in Clojure
 * Sequences in Clojure
 * Key operations on sequences
 * Sequence vs Seq


### [Concurrency & Parallelism](/articles/language/concurrency_and_parallelism.html) (TBD)

This guide covers:

 * An overview of concurrency hazards
 * Clojure's approach to state and identity
 * Immutable data structures
 * Reference types (atoms, vars, agents, refs)
 * Using Clojure functions with `java.util.concurrent` abstractions
 * The Reducers framework (Clojure 1.5+)
 * Other topics related to concurrency and runtime parallelism


### [Macros and Metaprogramming](/articles/language/macros.html) (TBD)

This guide covers:

 * Clojure macros
 * Clojure compilation process
 * Other topics related to metaprogramming


### [Laziness](/articles/language/laziness.html) (TBD)

This guide covers:

 * What are lazy sequences
 * Pitfalls with lazy sequences
 * How to create functions that produce lazy sequences
 * How to force evaluation


## The Ecosystem

### [Getting Started with Leiningen](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md) (Semi-complete)

This guide covers:

 * What is Leiningen and what it can do for you
 * How to create a project with Leiningen
 * How to manage project dependencies
 * Accessing the REPL
 * How to run tests for your project
 * How to run the app
 * How to compile your code and dependencies into a single JAR for deployment ("überjar")
 * How to share (publish) a library


### [Clojure Community](/articles/ecosystem/community.html) (Complete)

This guide covers:

 * Planet Clojure, mailing lists, IRC channel
 * Clojure conferences
 * Local Clojure user groups
 * Other Clojure community resources


### [Clojure Library Directory](/articles/ecosystem/libraries_directory.html) (Complete)

A curated and highly opinionated categorized directory of available Clojure libraries and tools.



### [Data Processing (Overview)](/articles/ecosystem/data_processing.html) (TBD)

This guide covers:

 * An overview of why Clojure is an excellent choice for data processing
 * Popular tools and libraries in the area


### [Web Development (Overview)](/articles/ecosystem/web_development.html) (TBD)

This guide covers:

 * Popular tools and libraries in the area


### [Editors & IDEs](/articles/ecosystem/development_tools.html) (TBD)

This guide covers:

 * An overview of development tools
 * A brief intro to Emacs, nREPL, SLIME/Swank
 * A brief intro to VimClojure
 * A brief intro to Counterclockwise (Eclipse plugin)
 * A brief intro to La Clojure (IntelliJ IDEA plugin)


### [Books](/articles/ecosystem/books.html) (TBD)

This guide covers:

 * Books on Clojure
 * Books on ClojureScript


### [Library Development and Distribution](/articles/ecosystem/libraries_authoring.html) (TBD)

This guide covers:

 * Basic setup for library development
 * How to publish a library to Clojars


### [Leiningen Profiles](https://github.com/technomancy/leiningen/blob/master/doc/PROFILES.md) (Semi-complete)

This guide covers:

 * What are Leiningen profiles
 * How to use them


### [Distributing Libraries with Leiningen](https://github.com/technomancy/leiningen/blob/master/doc/DEPLOY.md) (Complete)

This guide covers:

 * How Clojure libraries are distributed
 * How to publish Clojure libraries to clojars.org
 * How to publish Clojure libraries to Maven Central
 * How to publish Clojure libraries to your own Maven repository


### [Leiningen Plugins](https://github.com/technomancy/leiningen/blob/master/doc/PLUGINS.md) (Semi-complete)

This guide covers:

 * What Leiningen plugins can do
 * How to install Leiningen plugins
 * How to develop plugins
 * How to distribute plugins



## Cookbooks

### [Strings](/articles/cookbooks/strings.html) (TBD)

This cookbook covers:

 * How to work with strings
 * How to work with regular expressions
 * How to work with characters

### [Mathematics](/articles/cookbooks/math.html) (incomplete)

Includes coverage of facilities for doing math with Clojure.


### [Data Structures](/articles/cookbooks/data_structures.html) (TBD)

This cookbook covers:

 * Vectors
 * Maps
 * Lists
 * Sets
 * Generic operations on sequences

### [Files and Directories](/articles/cookbooks/files_and_directories.html)

This cookbook covers:

 * Reading and writing text and binary files
 * Listing directory contents
 * Creating files and directories
 * Moving files and directories
 * Removing files and directories
 * Accessing file metadata
 * Other operations on files and directories


### [Date and Time](/articles/cookbooks/date_and_time.html) (TBD)

This guide covers:

 * Working with JDK dates
 * Working with Joda Time and `clj-time`
 * Instant literals (Clojure 1.4+)


## License

All the content is distributed under the
[CC BY 3.0](http://creativecommons.org/licenses/by/3.0/) license
and are copyright their respective primary author(s).


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide [on Twitter](https://twitter.com/clojuredocs) or the [Clojure mailing list](https://groups.google.com/group/clojure).

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes.
Reader feedback is key to making the documentation better.
