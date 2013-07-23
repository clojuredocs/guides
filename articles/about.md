---
title: "About"
layout: article
---

CDS (Clojure Documentation Site) is a community documentation project for the Clojure programming language. It is not affiliated with
`Clojure/core`, does not require going through the Clojure Contributor Agreement, and is developed [on GitHub](http://github.com/clojuredocs).

## Rationale

The rationale is explained in more detail in the [announcement blog post](http://blog.clojurewerkz.org/blog/2012/10/10/announcing-a-new-clojure-documentation-project/).

## History

CDS was started in early October, 2012, by several active members of the Clojure community due to their dissatisfaction
with the state of documentation and documentation contribution process (that involved mailing Clojure Contributor Agreement in paper).


## Goals

The goal is to produce quality technical documentation for Clojure users and potential adopters with various expertise levels.

CDS strives to cover all aspects of Clojure: from tutorials and language guides to overview of the ecosystem, how
libraries are developed and published, topics operations engineers will be interested in, JVM ecosystem tools
and so on.

Adopting a language always takes more than just reading a book or a few tutorials about language features. Understanding
design goals, the ecosystem and operations is just as important. CDS will try to address this.


### What CDS is Not

What's *not* here:

  * Cheatsheets. The official [Clojure cheatsheet](http://clojure.org/cheatsheet) is very good.  There is also an unofficial [ClojureScript cheasheet](https://github.com/fogus/clojurescript-cheatsheet) available for download and contribution.
  * API reference docs. Those can currently be found (with examples) at [Clojuredocs](http://clojuredocs.org/).

Clojuredocs needs a lot of work and redesign (as in, the way it works) which will take a while. CDS is not concerned with providing the API reference;
only tutorials, guides, and linking to other relevant resources.



## Structure

CDS is structured as a number of guides. They broadly fall into 4 categories:

  * Tutorials
  * Language guides
  * Tool guides
  * Cookbooks


### Tutorials

These guides are for complete newcomers and should include a lot of hand holding. They don't assume any
previous familiarity with Clojure, the JVM, the JVM tool ecosystem, functional programming, immutability, and so on.

Target audience: newcomers to the language.


### Language guides

These guides are more in-depth, focused on various aspects of the language and interoperability.
Examples of such guides include:

  * Sequences
  * Interoperability
  * Reference types
  * Laziness
  * Macros and compilation

Target audience: from developers who already have some familiarity with the language to those who have been using it for
a while.


### Tools & Ecosystem guides

These guides cover key Clojure ecosystem tools such as [Leiningen](http://leiningen.org), [Clojars](http://clojars.org), [REPLy](https://github.com/trptcolin/reply),
[nREPL](https://github.com/clojure/tools.nrepl), [Emacs clojure-mode](https://github.com/technomancy/clojure-mode), VimClojure, [Counterclockwise](https://code.google.com/p/counterclockwise/), [La Clojure](http://plugins.jetbrains.com/plugin?pluginId=4050), etc. It also covers important ecosystem projects that are not tools: books,
[ClojureSphere](http://www.clojuresphere.com/), [ClojureWerkz](http://clojurewerkz.org/), [Flatland](https://github.com/flatland) and so on.

Target audience: all developers using or interested in the Clojure programming language.



### Cookbooks

Concise [Clojure example code](content.html#cookbooks), categorized by subject.



## Mailing List

CDS [currently uses Clojure mailing list](https://groups.google.com/group/clojure) for discussions. Feel free to join it and ask any questions you may have.


## News & Announcements on Twitter

News and announcements are posted [on Twitter](http://twitter.com/clojuredocs).



## Reporting Issues

If you find a mistake, poor grammar, an important topic not covered, or an outdated example, please [file an issue](http://github.com/clojuredocs/guides/issues) on Github.


## Contributing

CDS uses [ClojureWerkz Docslate](https://github.com/clojurewerkz/docslate). All tutorials and guides are written in Markdown.

The toolchain and setup process are described [in the README](https://github.com/clojuredocs/guides/blob/master/README-tools.md).

To submit changes, create a branch and make your changes on it. Once you are done with your changes and all tests pass, submit a pull request
on GitHub.
