---
title: "Starting with Eclipse and Counterclockwise For Clojure Development"
layout: article
---

## About this tutorial

This guide covers:

 * Installing Eclipse
 * Installing Counterclockwise, the Clojure plugin for Eclipse
 * Creating a sample project

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a>
(including images & stylesheets). The source is available [on Github](https://github.com/clojuredocs/cds).

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4.


## Installing Eclipse

1. Download the latest release of Eclipse from the [official site](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/junor).
2. Install Counterclockwise plugin.
   1. navigate to the "Install new Software" tab under the help menu
   2. paste in the CCW update URL: http://ccw.cgrand.net/updatesite in the "Work with:" text field
   3. check the "Clojure Programming" checkbox and hit the "Next" button
3. Follow the instructions on the screen and restart Eclipse for changes to take effect.

Counterclockwise takes care of setting up Clojure and Leiningen for you. And once the plugin is installed, you will be 
able to create a new Clojure project or a new Leiningen project. 

Clojure project will use Eclipse to manage dependencies, while the Leiningen project will pull dependencies from the
`project.clj` in the root folder of the project.

At this point you should have Eclipse with CCW up and running. Navigate to File->new->project in Eclipse menu. 
Then select Leiningen->Leiningen project. Here you'll see the default Leiningen Template filled in. 
And only thing you have to do is provide a project name. Give the project a name and hit the finish button.

You should now see a new project in your Package Explorer view on the left. If you created a project called 
`clojure-test` then the project template will have a src folder which will contain the package folder named clojure_test.

Since Java cannot use dashes in names, all the dashes in package folders for namespaces get converted to underscores. 
The pckage will contain a core.clj file, and its contents should look like the following:

```clojure
(ns clojure-test.core)
 
(defn -main
  "I don't do a whole lot."
  [& args]
  (println "Hello, World!"))
```

Let's open it and then hit the run button. You should see a REPL pop up momentarily on the bottom of the IDE. 
If all went well, your project should be ready to work on. The code that's in the file will have already been 
loaded up in the REPL when we hit run, and we should now be able to call our -main function.

To do that, let's write the code which calls main below it:

```clojure
(-main)
```

Then navigate the cursor inside the call body and hit CTRL+ENTER on Linux/Windows or CMD+ENTER on OS X. 
You should see "Hello, World!" printed in the REPL view on the bottom. We can now change the behavior of the 
`-main` function and after reloadig it the new behavior will be available next time it'scalled.

It's also recommended to enable the "strict/paredit" mode under Preferences->Clojure->Editor section. 
This will allow the editor to keep track of balancing the parens for you. 

Another useful feature of the editor is the ability to select code by expression. 
If you navigate inside a function and press ALT+SHIFT+UP (use CMD instead of ALT in OS X), then inner 
body of the expression will be selected, pressing it again, will select the expression, and then the outer body, 
and so on. Conversely pressing ALT+SHIFT+DOWN will narrow the selection. This allows you to quickly navigate nested 
structures, and select code by chunks of logic as opposed to simply selecting individual lines.

## Managing dependencies

### Eclipse Dependencies

1. Right click on the project navigate to properties. 
2. Under properties select "Java Build Path"
3. Under Libraries select "Add External JARs..."
4. Click OK

The library will show up under "Referenced Libraries" in Package Explorer.

### Leiningen

You will also see a `project.`clj` file in the root of the project. This file should look like the following:

```clojure
(defproject clojure-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.3.0"]])
```

You can add new dependencies to your project by adding them to the dependencies vector. 
For example, if we wanted to add an HTTP client, we'd go to http://clojuresphere.herokuapp.com/ click on clj-http link. 
From there select the Clojars link and copy the following:

```clojure
[clj-http "0.5.2"]
```

now we'll simply paste it under dependencies in our project.clj:

```clojure
:dependencies [[org.clojure/clojure "1.3.0"]
               [clj-http "0.5.2"]]
```

In the package explorer view on the left expand "Leiningen dependencies" 
and see thath the clj-http jar included there. You will now have to kill our current REPL
if it is running. To do that navigate to the terminal view next to it and press the stop button. 
When we start a new instance of the REPL, the library will be available for use. 

In the core file we can now require the library in the namespace definition:

```clojure
(ns clojure-test.core
 (:require [clj-http.client :as client]))
```

and test using the client by typing

```clojure
(client/get "http://google.com")
```

and running it as we did earlier.
