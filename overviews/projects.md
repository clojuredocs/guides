# Projects

When creating a program written in Clojure, it's customary to organize
all your code and other resources into a self-contained "project"
directory. The project contains not only your program code and test
code, but also links to fetched copies of any libraries (jars) it
depends upon --- *including the Clojure implementation itself* (which
is also a jar file).

Clojure projects contain a top-level project config file (named
"project.clj") which --- among other things --- specifies library
dependencies including specific version numbers.

When you start up a repl, you often do so from within your project
directory, such that it's easy to interactively access your project's
code and code it depends upon.

When you *distribute* a Clojure application (as a runnable jar),
distributed along with it are all the third-party libs (jars) which it
depends upon. This is in contrast to languages like Perl, Python, and
Ruby, where third-party libraries are installed on a per-system basis
into a centralized location where all programs can find them.

Project management tasks are handled by the
[Leiningen](http://leiningen.org/) tool. It can:

  * create a new project
  * build and run your project's code (dependencies will
    automatically be fetched for you)
  * run your project's tests
  * start a repl
  * package your project as a jar
  * display you your project's classpath
  * generate a maven-style "pom" file for your project

See the Leiningen
[README](https://github.com/technomancy/leiningen#readme) and
[TUTORIAL](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md)
for more info.

The vast majority of modern free and open source third-party Clojure
libraries and Clojure apps are hosted at github, exhibit the familiar
Clojure project structure, and are managed by Leiningen.
