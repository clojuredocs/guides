---
title: "Getting Started with Clojure"
layout: article
---

This guide covers:

 * prerequisites (such as Leiningen) and installing
 * running the REPL
 * creating a project
 * interactive development

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on
Github](https://github.com/clojuredocs/guides).


## Overview

Clojure is a wonderfully simple language and you are going to love
it.

To quickly get started, first make sure you've got Java installed.

Then install the [Leiningen](http://leiningen.org/) project management
tool.

> This author (jg) recommends always installing by downloading the
> script directly (as described in the instructions at leiningen.org),
> rather than using your OS's package manager. This will ensure that
> you get the latest lein version 2.

Clojure programs are typically developed inside their own project
directory, and Leiningen manages projects for you. Lein takes care of
pulling in dependencies (including Clojure itself), running the REPL,
running your program and its tests, packaging your program for
distribution, and other administrative tasks. Run `lein help` to
see the list of all the tasks in can perform.

> Again, there's no need to "install" Clojure, per se. Lein
> will take care of fetching it for you.


## Trying out the REPL

Although lein facilitates managing your projects, you can also run it
on its own (outside of any particular project directory). Once you
have the `lein` tool installed, run it from anywhere you like to get a
repl:

    $ lein repl

You should be greeted with a "`user=>`" prompt. Try it out:

``` clojure
user=> (+ 1 1)
;; ⇒ 2
user=> (distinct [:a :b :a :c :a :d])
;; ⇒ (:a :b :c :d)
user=> (dotimes [i 3]
  #_=>   (println (rand-nth ["Fabulous!" "Marvelous!" "Inconceivable!"])
  #_=>            i))
;; Marvelous! 0
;; Inconceivable! 1
;; Fabulous! 2
;; ⇒ nil
```


## Your first project

Create your first Clojure program like so:

``` bash
lein new app my-proj
cd my-proj
# Have a look at the "-main" function in src/my_proj/core.clj.
lein run
```

and see the output from that `println` function call in
my_proj/core.clj!


## Interactive Development

In your project directory, start up a repl (`lein repl`) and
run your `-main` function to see its output in the repl:

    $ lein repl
    ...
    my-proj.core=> (-main)
    Hello, World!
    nil

(The prompt is now "my-proj.core=>" instead of "user=>" because lein
has started the repl in an app project. More about that ("namespaces")
in the topical guides.)

From elsewhere, open up your my-proj/src/my_proj/core.clj file
in your editor. Modify the text in that `println` call.

Back in the repl, reload your source file and run `-main` again:

    my-proj.core=> (require 'my-proj.core :reload)
    my-proj.core=> (-main)

to see your changes.


## See Also

Other getting started documentation you might find useful:

  * [Clojure Distilled](http://yogthos.github.io/ClojureDistilled.html):
    introduction to core concpets necessary for working with Clojure
  * [A Brief Beginner's Guide to
    Clojure](http://www.unexpected-vortices.com/clojure/brief-beginners-guide/index.html):
    contains a bit more overview and background material for learning your way
    around the landscape.
  * [Starting Clojure screencast](http://cemerick.com/2012/05/02/starting-clojure/):
    an extensive getting-started screencast using Eclipse to develop a webapp project.


## Next Stop

Next stop: [the basic Clojure language tutorial](/articles/tutorials/introduction.html).



## Contributors

John Gabriele <jmg3000@gmail.com> (original author)
