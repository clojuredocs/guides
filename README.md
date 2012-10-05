# CDS: A Clojure Doc Site

An assorted collection of tutorials, overviews, topical guides, and
other documentation (all by various authors) for the Clojure
programming language. Currently available [here, in lovely
html](http://www.unexpected-vortices.com/clojure/cds/index.html).

## Goals

The goal is to eventually have these documents hosted at
doc.clojure.org.

What's *not* here:

  * Cheatsheets. Those can be found at
    [clojure.org/cheatsheet](http://clojure.org/cheatsheet), or with
    tooltips at
    [jafingerhut.github.com](http://jafingerhut.github.com).
  * API reference docs. Those can currently be found (with examples)
    at [Clojuredocs](http://clojuredocs.org/).

Clojuredocs needs a lot of work and redesign
(as in, the way it works) which will take a while. CDS is not concerned with providing the API reference; only tutorials, guides, and
linking to other relevant resources.



## Structure

CDS is structured as a number of guides. They broadly fall into 3 categories:

 * Tutorials
 * Language guides
 * Tool guides

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

These guides cover key Clojure ecosystem tools such as [Leiningen](http://leiningen.org), [Clojars](http://clojars.org), [REPLy](),
nREPL, Emacs, VimClojure, Counterclockwise, La Clojure, etc. It also covers important ecosystem projects that are not tools: books,
ClojureSphere, ClojureWerkz, Flatland and so on.

Target audience: all developers using or interested in the language.



## Resources

For some guidance on writing great documentation, see
<http://jacobian.org/writing/great-documentation/>.


## Contributing

### Toolchain

`README-tools.md` covers development environment setup.


### Contributors List

If you are the primary author of a substantial document, you might
include your name in a `# Contributors` section near the end of it,
noting that you are the original author. If you have made substantial
contributions to an existing document, you might add your name to the
`# Contributors` section. Otherwise, all contributors are encouraged
to add their name to the CONTRIBUTORS file.

Note to potential contributors: if you'd like to make changes to an
existing document, please attempt to stay true to the author's
original style and intent. If you chafe at this idea, you might
consider writing your own separate document for inclusion in this
collection. `:)`


## License

All docs in the clojure-docs-collection are distributed under the
[CC BY 3.0](http://creativecommons.org/licenses/by/3.0/) license
and are copyright their respective primary author(s).
