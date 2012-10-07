---
title: "Categories of Available Clojure Libraries"
layout: article
---

Categories of libraries available for use with Clojure include:

  * clojure.core (the fundamental built-in library of the Clojure
    language)
  * its own standard libraries (these come with Clojure)
  * the contrib libraries (standard, but they don't ship with Clojure)
  * 3rd-party Clojure libs (available via Clojars)
  * 3rd-party JVM libs (available via Maven Central)
  * Java standard class library (comes standard with Java)
  * other Java libs

(Installation and usage is covered in the [libraries usage
tutorial](../tutorials/libraries_usage.html).)


## Synopsis


    type of lib    namespace         standard?  location
    -------------  ----------------  ---------  --------------------------------------------
    core           clojure.core      yes        built-in
    standard lib   clojure.*foo*     yes        comes with Clojure
    contrib        clojure.*foo*     yes        projects at github, jars via Maven Central
    3rd party      (various)         no         projects at github, jars via Clojars
    Java std lib   (various)         yes        comes with Java
    JVM jar        (various)         no         Maven Central



## Standard Library

In addition to clojure.core, Clojure comes with a number of standard
libraries. They're all listed (along with their documentation) at
<http://clojure.github.com/clojure/index.html>. Like clojure.core,
their namespace names are all prefixed with "clojure."

If you [download the Clojure release](http://clojure.org/downloads)
and look in its "clojure-i.j.k/src/clj/clojure" dir, you can see the
source code files for these libs.



## Contrib

Clojure also has a number of standard extra libs ("contrib" libraries)
which are not distributed with Clojure proper. They all:

  * are listed at the [Clojure
    Contrib](http://dev.clojure.org/display/doc/Clojure+Contrib)
    Confluence wiki page.
  * have namespace names that start with "clojure.", just like the
    standard libs.
  * have documentation available at <http://clojure.github.com/>.
  * live at github as separate projects under the same "clojure" user
    as Clojure itself. The github project names are the namespace
    names minus the "clojure." prefix  (so, for example, the
    "algo.generic" contrib lib uses the namespace
    "clojure.algo.generic" and lives at
    <https://github.com/clojure/algo.generic>).
  * are owned by their author as well as by Rich Hickey, as per the
    Clojure CA ("Contributor Agreement").

> Note that, back in Clojure version 1.2, contrib had previously been
> one big repository ("monolithic contrib"), rather than individual
> ones like we have today ("modular contrib"). Unfortunately, the
> contrib link at [Clojuredocs](http://clojuredocs.org/) still points
> to a page for the old monolithic contrib. To read more about the
> change, see
> [Where+Did+Contrib+Go](http://dev.clojure.org/display/design/Where+Did+Clojure.Contrib+Go).



## Libraries from Clojars

There are many libraries available for Clojure at
[Clojars](http://clojars.org/). Clojars is the
"[CPAN](http://en.wikipedia.org/wiki/CPAN) for Clojure", except that
only basic information about a given project is displayed there (such
as a short description, project url, and some technical info necessary
for using the project).  To read more about a given project, follow
the link to its project page (which is usually at github).

> If a given project is hosted at github, but there's no github link
> at its clojars page, you can help the community by contacting the
> author and letting them know their project may be missing a ":url"
> option in its project.clj file (more details on what that means in
> the next chapter).

Some libraries may also be part of an "umbrella project" (for example,
[Clojurewerks](http://clojurewerkz.org/)).

A final note about Clojure library names: some of them are prefixed
with "clj-", as in "clj-foo".  This may indicate that the Clojure
library wraps a Java library (in this made up example, clj-foo might
wrap the "foo" Java library).



## Libraries from Maven Central

There are also many libraries available for use by Clojure at [Maven
Central](http://search.maven.org/). Maven Central is the "CPAN for the
JVM". (Incidentally though, the contrib libraries are actually hosted
at Maven Central rather than at Clojars.)

To find the libraries you need, you might browse around at:

  * <http://projects.apache.org/>
  * <http://www.eclipse.org/projects/>
  * <http://www.jboss.org/projects>

Those collections are large, and may host both libraries *and* tools
--- with some tools having component libraries that you might find
useful as well.

> As long as we're mentioning jboss, note that the Clojure on JBoss
> app server is [Immutant](http://immutant.org/).



## Java Standard Library: JDK

Clojure can, of course, use Java's built-in library (its standard
class library). See the [Java
documentation](http://docs.oracle.com/javase/7/docs/) for what's
available.

Java interop is fairly simple (though not discussed in
this guide), and --- as with the Clojure standard libraries ---
there's no extra installation required since you already have Java
installed.



## Other Java Libraries

You can use Java libraries even if they're not listed at Maven
Central, but doing so is beyond the scope of this document.
