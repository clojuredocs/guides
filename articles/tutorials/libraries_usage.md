---
title: "Using the Various Categories of Libraries"
layout: article
---

If you've [located a library](../ecosystem/libraries_directory.html)
which you'd like to use in your project, the first thing to do is edit
your project.clj file such that [lein](http://leiningen.org/) knows
your project now depends upon it. The format for specifying a library
dependency looks like this:

<tt>[**group-id**/**artifact-id version-string**]</tt>

and is sometimes referred to as the "coordinates" of the dependency.

The group-id indicates who's associated with that particular library.
The artifact-id is the library name (the group-id is optional if it's
the same as the artifact-id). The version string follows the common
"major.minor.patch" pattern (ex. "1.0.2").

Here's an example:

{% highlight clojure %}
[org.clojure/data.json "0.1.3"]
{% endhighlight %}

> Note that the convention for most libs at Clojars (the canonical
> ones that you'll usually be using) is to have the group-id be the
> same as the artifact-id. And in that case, the group-id is omitted
> from the coordinates. If you see a project at clojars with a
> group-id like "org.clojars.*username*", it usually indicates that
> the project is a forked version of the canonical one.

Another requirement for using a library is that you must edit your
source file to make the lib available from within your code.

Examples are given below on how to use each of several types of
available libraries.

The examples below are shown using a trivial project created by
running `lein new app foo-bar`.



## A Note on Names

There are at least a few names associated with each library:

  * **Its artifact-id.** This is at the top of the library's
    project.clj (sometimes also preceeded by a group-id then a slash).

  * **Its github project name.** Most Clojure lib repos are hosted at
    github, and *most* of them are named the same as their artifact-id.

  * **The namespaces it provides.** These are what you specify in your
    source code in order to use the library. For libraries which only
    provide one namespace, it's often "*the-artifact-id*.core". Note
    that the contrib libs have namespaces which begin with "clojure.",
    though their artifact ids do not (see the example below).




## Using Standard Libraries

To use standard libraries in your code you don't need to touch
your project.clj file since these libs already come with Clojure.
Just edit your core.clj file. For example, you can use the
clojure.string standard lib like this:

{% highlight clojure %}
(ns foo-bar.core
  (:require [clojure.string :as str]))

(defn -main
  "docstring goes here"
  [& args]
  (println (str/reverse "encoded secret!")))
{% endhighlight %}


(Note, we're using the short name "str" here as an alias for
clojure.string to save ourselves some typing.)

Do `lein run` to try it out.



## Using Contrib Libraries

All the [contrib
libs](http://dev.clojure.org/display/doc/Clojure+Contrib) have project
pages at github (under <https://github.com/clojure>), with artifacts
(jars) hosted at [Maven Central](http://search.maven.org/).  To use
one — for example, [data.json](https://github.com/clojure/data.json) —
we first need to look up the coordinates for it in the README.md at
its github project page.

Add that to your project.clj's :dependencies list:

{% highlight clojure %}
(defproject foo-bar "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.json "0.1.3"]]
  :main foo-bar.core)
{% endhighlight %}

and make your core.clj look like:

{% highlight clojure %}
(ns foo-bar.core
  (:require [clojure.data.json :as json]))

(defn -main
  "docstring goes here"
  [& args]
  (println (json/json-str {:a [1 2 3] :b "Hello"})))
{% endhighlight %}

(Note, we're using "json" as an alias for clojure.data.json to save
ourselves some typing.)

Do `lein run` to try it out. Note that lein is smart — it noticed that
your project needed the data.json jar, so it automatically went and
fetched it for you. Look in the ~/.m2/repository/org/clojure/data.json
dir and see for yourself.



## Using Libraries from Clojars

If you've found a library you're interested in at
[Clojars](http://clojars.org/), the Clojars page for the lib should
show you exactly what you need to add to your project.clj's
:dependencies list. As noted above, for canonical 3rd-party libs at
Clojars, it's common that the group-id will be the same as the
artifact-id, and so would be omitted in the "coordinates" string you
add to your project.clj.

In your core.clj file, you'll often put something like "`(:require
lib-name.core)`" in your ns macro, and then later use a function
from that library like so: `(lib-name.core/func-name ...)`

The Clojars page for the lib should also contain a link to the github
(or other home) page for the library, which should contain a README.md
showing example usage. If there is no such link, consider searching
for the lib at github and then filing a bug report at the lib's
project page about the issue.


### Downloading jars from Clojars ###

If for some reason you'd like to directly download jar files from
Clojars, look in <http://clojars.org/repo/>.




## Using Various Java Libraries

Search [Maven Central](http://search.maven.org/) for the library
you're interested in, and adjust your project.clj's :dependencies just
like how you did [when using a contrib library](#using-contrib-libraries).

As for using Java libs which are *not* registered at Maven Central,
see the lein [repeatability
doc's](https://github.com/technomancy/leiningen/wiki/Repeatability)
"Free-Floating Jars" section.




## Viewing the Dependency Tree

For a given project, to see the which libraries depend upon which,
print out the dependency tree like so:

    lein deps :tree



## Contributors

John Gabriele, 2012 (original author)
