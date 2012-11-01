---
title: "How to use Maven to build Clojure code"
layout: article
---

This article describes how to use Maven to build projects written in Clojure (or in
Clojure, and other languages, such as Java).

## What is Maven?

[Maven](http://maven.apache.org) is a software project life cycle management tool.  It implements dependencies
resolution (with automatic download of missing dependencies from repositories), building &
testing of code, deployment of software, etc.  Maven's functionality is extensible with
plugins, so it's possible to use it not only for Java code (primary goal of this tool),
but also for code, written in other languages. You can read more about Maven in
[following, freely available book](http://www.sonatype.com/products/maven/documentation/book-defguide).

Maven differs from other tools, such as Ant - it describes *what* we want to do, in
contrast with Ant, that describes *how* to do something.  Maven uses declarative style to
describe tasks that we want to execute, and all described tasks are performed by
corresponding plugins.

Description of software lifecycle and information about project is stored in `pom.xml` file,
that should exist in root directory of the project (and in root directories of
sub-projects, if your project is separated into several modules).  Project's information
includes name, identifier and version of the project, and often includes more information:
URL of project's site, information about source code repository (so you can use `mvn
scm:update` goal to update code, for example), etc.

Project Object Model (POM) defines set of stages for project's lifecycle - they are
called "lifecycle phases".  Each phase can include several tasks (goals), that define what
will be performed on given stage.  There are several common stages: compilation (`compile`),
testing (`test`), creation of package (`package`), and installation (`install`).  Each of these
phases has dependencies on other phases, that should be executed before its invocation
(compilation should be executed before testing, testing before packaging, etc.).

Usually developer uses phase's name to start process.  For example, `mvn package`, or `mvn
install`, etc.  But developer can also execute concrete Maven's goal.  To do this, he
should specify name of plugin, that implements concrete goal, and task name in given
plugin.  For example, `mvn clojure:run` will start Clojure and execute script, specified in
configuration.  We need to mention, that list of goals, that are executed for concrete
lifecycle phase isn't constant - you can change this list by modifying plugin's
configuration.

## Maven and Clojure

Clojure's support in Maven is provided by
[clojure-maven-plugin](http://github.com/talios/clojure-maven-plugin), that is available
in Maven's central repository, so it always available. (Besides `clojure-maven-plugin`,
there is also [Zi](https://github.com/pallet/zi) plugin, that was developed as part of
[Pallet](http://palletops.com/) project. In contrast to `clojure-maven-plugin` it's
written in Clojure, and more tightly integrated with Clojure-specific subsystems, such
Marginalia, Ritz, etc.)

As a base for your projects you can use `pom.xml` file from
[clojure-maven-example](http://github.com/talios/clojure-maven-example) project.

If you already have `pom.xml` in your project, then to enable this plugin, you will need to
add following code into `<plugins>` section of `pom.xml`:

```xml
  <plugin>
    <groupId>com.theoryinpractise</groupId>
    <artifactId>clojure-maven-plugin</artifactId>
    <version>1.3.10</version>
  </plugin>
```

*Attention:* version number could be changed as development continues.  To find latest
plugin's version number you can use sites [mvnrepository](http://mvnrepository.com/artifact/com.theoryinpractise/clojure-maven-plugin) or [Jarvana](http://jarvana.com/jarvana/), that contains
information about packages, registered in Maven's repositories.  Besides this, you can
omit plugin version - in this case, Maven will automatically use latest available version
(although this isn't always good idea).

Declaration of this plugin will give you all implemented functionality - compilation,
testing & running of code, written in Clojure, etc.  Although, out of box you'll need to
use complete goals names, such as `clojure:compile`, `clojure:test` & `clojure:run`.

But you can make your life easier if you'll add these goals into list of goals for
concrete lifecycle phases (`compile` and `test`).  To do this you need to add section
`<executions>` into plugin's description, as in following example:

```xml
 <plugin>
   <groupId>com.theoryinpractise</groupId>
   <artifactId>clojure-maven-plugin</artifactId>
   <version>1.3.10</version>
   <executions>
     <execution>
       <id>compile</id>
       <phase>compile</phase>
       <goals>
         <goal>compile</goal>
       </goals>
     </execution>
     <execution>
       <id>test</id>
       <phase>test</phase>
       <goals>
         <goal>test</goal>
       </goals>
     </execution>        
   </executions>
 </plugin>
```

In this case, source code, written in Clojure will be compiled - this useful if you
implement `gen-class`, that will be used from Java, or if you don't want to provide source
code for your application.  But sometimes it's much better just to pack source code into
jar, and it will compiled during loading of package (this is default behaviour when you're
declaring `clojure` packaging type) - this allows to avoid binary incompatibility between
different versions of Clojure.  To put source code into jar, you need to add following
code into `resources` section (or change packaging type to `clojure`):

```xml
 <resource>
   <directory>src/main/clojure</directory>
 </resource>
```

By default, Clojure's source code is placed in the `src/main/clojure` directory of the
project's tree, while source code for tests is placed in the `src/test/clojure` directory.
These default values could be changed in [plugin's configuration](#configure).

### Goals, defined in clojure-maven-plugin

`clojure-maven-plugin` implements several commands (goals) that could be divided into
following groups:

 * Goals that work with source code (usually they are linked with corresponding phases of
   lifecycle, as it's shown above):
   * `clojure:compile`: compiles source code, written in Clojure;
   * `clojure:test`: executes tests, written in Clojure.
   * `clojure:test-with-junit`: executes tests using JUnit;
   * `clojure:add-source`: adds directory with source code to archive `...-sources.jar`;
   * `clojure:add-testsource`: add directory with tests source code into archive
     `...-testsources.jar`.

 * Goals for execution of project's code:
   * `clojure:run`: executes script (or scripts) defined by `script` and/or `scripts`
    configuration directives.  This goals is often used to run project with correct
    dependencies;
   * `clojure:repl`: starts Clojure REPL with all dependencies, specified in project.  If
    necessary, it also executes script specified in configuration option `replScript` - for
    example, you can put some initialization code into it.  If the JLine library was
    specified in dependencies, then it will be loaded automatically, making your work in
    REPL more comfortable;
   * `clojure:swank`: starts Swank server, so you can connect to it from Emacs SLIME.  By
    default, this server is running on port 4005 (this value could be changed with system
    option `clojure.swank.port`);
   * `clojure:nailgun`: starts Nailgun server, so you can connect to it from Vim with
    [vimclojure](http://kotka.de/projects/clojure/vimclojure.html).  By default, this server is running on port 2113 (this value could be
    changed with system option `clojure.nailgun.port`).

 * Auxiliary tasks:
   * `clojure:marginalia`: generates documentation using [Marginalia](http://fogus.github.com/marginalia/);
   * `clojure:autodoc`: generates documentation using [autodoc](http://tomfaulhaber.github.com/autodoc/);
   * `clojure:gendoc`: generates documentation using gendoc.

### Clojure-related repositories

There are several Clojure-related repositories.  All Clojure versions (stable &
development) are published at [Sonatype repository](http://dev.clojure.org/display/doc/Maven+Settings+and+Repositories) that is periodically synchronized with
Maven Central.  [Clojars](http://clojars.org) is repository that is used by Clojure community to publish their
projects.

To use repository you need to add following code into `repositories` section in `pom.xml`:

```xml
 <repository>
   <id>clojars</id>
   <url>http://clojars.org/repo/</url>
 </repository>
```

### Dependencies

Maven automatically downloads all necessary dependencies from default repository, and
repositories, specified by user (as shown above).  Downloaded packages are stored in
user's home directory and could be used by other projects without additional downloading.
Each package is uniquely identified by combination of three parameters - group's name
(`groupId` tag), artifact's name (`artifactId` tag), and version (`version` tag).

To use Clojure in your project you need at least specify dependency on language itself.
Right now, the stable version of Clojure is 1.4.0.  To declare this dependency, add
following code into `dependencies` section of `pom.xml` file:

```xml
 <dependency>
   <groupId>org.clojure</groupId>
   <artifactId>clojure</artifactId>
   <version>1.4.0</version>
 </dependency>
```

If you want to use latest version of the language, then you need to add corresponding
repository (snapshots) and use version number like `1.5.0-SNAPSHOTS` instead of version
`1.4.0`.

To perform some tasks, implemented by `clojure-maven-plugin`, you need to specify additional
dependencies:
 * if you will use `clojure:swank` goal, then you need to specify dependency on `swank-clojure` package:

```xml
 <dependency>
  <groupId>swank-clojure</groupId>
  <artifactId>swank-clojure</artifactId>
  <version>1.4.2</version>
</dependency>
```

 * if you will use `clojure:nailgun` task, then you need to download distribution from
   [vimclojure](http://kotka.de/projects/clojure/vimclojure.html)'s site, build it, as described in documentation, and install into local
   Maven repository.  And after this, you need to add following dependency on
   `vimclojure` with following code:

```xml
<dependency>
  <groupId>de.kotka</groupId>
  <artifactId>vimclojure</artifactId>
  <version>X.X.X</version>
 </dependency>
```

 * the JLine library isn't required, but it could be useful if you plan to use REPL -
   this library implements history of commands, etc.  Presence of this library is detected
   automatically when `mvn clojure:repl` goal is executed. You can add dependency for this
   library with following code:

```xml
 <dependency>
  <groupId>jline</groupId>
  <artifactId>jline</artifactId>
  <version>0.9.94</version>
 </dependency>
```

#configure
### Plugin's configuration

Developer can change plugin's configuration options, such as location of source code,
scripts names, etc.  To change some parameter, you need to add its new value into
`configuration` section of the plugin's description.  For example, you can specify name of
the script, that will be executed during testing, using following code:

```xml
<plugin>
   <groupId>com.theoryinpractise</groupId>
   <artifactId>clojure-maven-plugin</artifactId>
   <version>1.3.10</version>
   <configuration>
     <testScript>src/test/clojure/test.clj</testScript>
   </configuration>
   .....
 </plugin>
```

Following options are used to specify options related to source code & compilation:
 * `sourceDirectories` - this option defines list of directories (each of them should be
  wrapped into `sourceDirectory` tag) that contains source code written in Clojure, and that
  will be packed into resulting jar (and compiled, if corresponding option is specified);
 * `testSourceDirectories` - defines list of directories (each of them should be wrapped into
  `testSourceDirectory` tag) with tests, written in Clojure;
 * `warnOnReflection` - option that enables (`true`) or disables (`false`) warnings about
  reflection during compilation of source code.

Besides this, you can control which namespaces will be compiled and/or for which
namespaces testing of source code will be performed.  To do this, you need to add
`namespaces` tag into configuration and list corresponding namespaces inside it (each of
item should be wrapped into `namespace` tag).  You can use regular expressions to specify
all necessary namespaces, and you can also use `!` to exclude namespaces from this list.  In
addition to this option, you can use other two: `compileDeclaredNamespaceOnly` and
`testDeclaredNamespaceOnly` (with values `true` or `false`) - they control, will be these
namespace limitations applied during compilation and/or testing.

There are also several options that are used to specify parameters for execution of your
code and/or tests:
 * `script` and `scripts` - defines one (`script` tag) or several (`scripts` tag with nested `script`
   tags) names of scripts with code, that will executed when you'll execute the
   `clojure:run` task;
 * `testScript`: defines name of script that will executed when you'll execute `clojure:test`
   task.  If there was no value specified in plugin's configuration, then plugin will
   automatically generate run script for all tests, that was found in project;
 * `replScript` - defines name of script, that will executed if you'll execute `clojure:repl` task
  (it's also used by `clojure:swank` and `clojure:nailgun` tasks).  This code will executed
  before entering into REPL, so you can use it to specify initialization code for your
  working environment;
 * `runWithTests` - enables (`true`) or disables (`false`) executions of tests if you run REPL or
  your code via Maven.  You can also change this value by using Maven's command-line
  option.  For example, using following command `mvn clojure:repl
  -Dclojure.runwith.test=false`;
 * `clojureOptions` - using this option you can specify command-line options that will be
  passed to `java` process on every invocation.

## Conclusion

I think, that this article provides enough information for you to start use Maven together
with Clojure.  If you have Clojure-only project, and you don't plan to use all power of
Maven, then may be you can look to the [Leiningen](leiningen.md) - this tool was created to build
projects, written mostly in Clojure.  Another interesting project is [Polyglot Maven](http://polyglot.sonatype.org/), the
main goal of it is creation of special DSL (Domain Specificl Language) using different
languages (Clojure, Scala, Groovy) for description of Maven's configurations (for Clojure
this language is almost the same as language implemented in Leiningen).

Other examples of using Maven with Clojure you can find in different projects: [Incanter](http://github.com/liebke/incanter/tree/1.0.x)
(as example of project, consisting from several modules), [labrepl](http://github.com/relevance/labrepl) and
[clojure-maven-example](http://github.com/talios/clojure-maven-example).  More information on Clojure and Maven you can also find in
following blog posts:
 - [Why using Maven for Clojure builds is a no-brainer](http://muckandbrass.com/web/display/~cemerick/2010/03/25/Why+using+Maven+for+Clojure+builds+is+a+no-brainer) (including video, that shows how to
   work with `clojure-maven-plugin`);
 - [How to create a Clojure application](http://pupeno.com/blog/how-to-create-a-clojure-application/);
 - [Maven’s Not So Bad](http://stuartsierra.com/2009/09/03/mavens-not-so-bad).
