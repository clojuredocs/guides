---
title: "Web Development (Overview)"
layout: article
---

This guide covers:

  * popular tools and libraries for web development

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on
Github](https://github.com/clojuredocs/guides).



## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.



## Some Options

Below are some of the various options available for web development
with Clojure, listed roughly by size.



### Ring and Compojure

Perhaps the simplest and most minimal setup is to use only Ring and
Compojure. To get started, see the [basic web development
tutorial](/articles/tutorials/basic_web_development.html).



### lib-noir

In addition to Ring and Compojure, you might also make use of
[lib-noir](https://github.com/noir-clojure/lib-noir).



### Luminus

[Luminus](http://www.luminusweb.net/) is [a lein
template](https://github.com/yogthos/luminus-template) for creating
batteries-included web applications. It makes use of Ring, Compojure,
lib-noir, and optionally (as described in its documentation) other
libraries.



## Templating Libraries

Clojure has many options for building HTML.


### Hiccup

[Hiccup](https://github.com/weavejester/hiccup) represents HTML as
Clojure data structures, allowing you to create and manipulate your
HTML easily.

[Tinsel](https://github.com/davidsantiago/tinsel) is a library that
extends Hiccup with selectors and transformers, so that you can write
a template separate from the insertion of your data into the template.


### Mustache

[Clostache](https://github.com/fhd/clostache) implements the
[Mustache](http://mustache.github.com/) templating language for
Clojure.

[Stencil](https://github.com/davidsantiago/stencil) is another
implementation of Mustache.


### Fleet

[Fleet](https://github.com/Flamefork/fleet) embeds Clojure inside HTML
templates, much like Java's JSPs, or Ruby's ERb.


### Clabango

[Clabango](https://github.com/danlarkin/clabango) is modeled after the
[Django templating system](https://docs.djangoproject.com/en/1.4/topics/templates/). It
embeds special tags and filters inside HTML templates to insert and
manipulate data.


### Enlive and Laser

[Enlive](https://github.com/cgrand/enlive) and
[Laser](https://github.com/Raynes/laser) are similar libraries. They
both manipulate plain HTML, and can be used for screen scraping as
well as templating. They work with HTML templates with no special
embedded tags or code. They use selector functions to find pieces of
HTML and transformation function to change the HTML into the way you
want.

See the
[Laser guide](https://github.com/Raynes/laser/blob/master/docs/guide.md)
to see if this style of templating works for you. It is powerful, but
different from most other languages' templating libraries.



## See Also

  * the [web development section of the library
    directory](/articles/ecosystem/libraries_directory.html#web_development).

  * [The Clojure Web Stack and the CRUD Stack](http://brehaut.net/blog/2012/clojure_web_and_the_crud_stack)

  * [A Brief Overview of the Clojure Web Stack](http://brehaut.net/blog/2011/ring_introduction)



## Contributors

* John Gabriele
* Clinton Dreisbach
