---
title: "Generating Documentation"
layout: article
---

This guide notes some commonly-used tools for generating project
documentation.

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on
Github](https://github.com/clojuredocs/guides).



## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.



## Overview

Projects commonly (hopefully?) have at least two types of
documentation:

  * standalone
    [markdown](http://en.wikipedia.org/wiki/Markdown)-formatted docs
    in the project's doc directory
  * docstrings

There are a number of tools for generating handsome API docs from
docstrings and other project metadata.


## Codox

If you'd like to generate nice-looking html API docs for your library,
use [codox](https://github.com/weavejester/codox). Usage instructions
are in the codox readme. Running codox (it's a lein plug-in and is run
via `lein codox` in your project) will create a "doc" subdirectory
containing the resulting html.



## Marginalia

If you'd like to render API docs side-by-side with the source code
it's documenting, use [the marginalia lein
plug-in](https://github.com/fogus/lein-marginalia). Usage instructions
are in the readme.



## Cadastre

If you'd like to generate copious raw data from a project (which
includes docstrings as well as other metadata), have a look at
[cadastre](https://github.com/dakrone/cadastre).
