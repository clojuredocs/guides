---
title: "Clojure with Vim and fireplace.vim"
layout: article
---

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on
Github](https://github.com/clojuredocs/cds).

## Overview

[fireplace.vim](https://github.com/tpope/vim-fireplace) is a Vim plugin developed by [Tim Pope](http://tpo.pe/) which provides support for the "dynamic" aspects of Clojure development. Namely, connection to an [nREPL](https://github.com/clojure/tools.nrepl) server, code evaluation, code completion, and basically everything beyond syntax highlighting and indentation.

This guide will cover installation and some basic usage within a typical Clojure project.

## About the Name

_fireplace was once called foreplay, but people weren't ready for that, so now it's fireplace_

## What About VimClojure?

Until recently (late 2012), VimClojure was the preeminent plugin for Clojure development in Vim. Since then, its developer, Meikel Brandmeyer, has [acknowledged](https://groups.google.com/forum/?fromgroups=#!topic/vimclojure/B-UU8qctd5A) that VimClojure development has slowed to a trickle and that fireplace is the future. That said, VimClojure is still a viable and excellent development evironment.

## What Versions of Clojure and fireplace Does This Guide Cover?

This guide covers Clojure 1.4 and the latest version of fireplace as of January 2013. It should work on most versions of both.

## Essential References

fireplace.vim lives on [github](https://github.com/tpope/vim-fireplace). That's the source of truth for development and where issues should be reported.

The [vimclojure mailing list](https://groups.google.com/forum/?fromgroups#!forum/vimclojure) remains the primary place to get help and ask questions related to Clojuring in Vim.

Finally, don't forget the documentation that comes with the plugin itself. All features are documented there in standard Vim help format. Just `:help fireplace`, or [view it online](https://github.com/tpope/vim-fireplace/blob/master/doc/fireplace.txt). *I'm dead serious. READ THIS FILE.*

## How fireplace.vim Works
Before using fireplace, it's useful to have a basic understanding of how it works, especially when it comes to evaluating Clojure code. This will help avoid confusion and tears. fireplace itself is a collection of VimScript code running withing Vim. When it needs to evaluate some Clojure code (for example, you just required a namespace, or gave it a command to run the tests in a namespace) it sends commands to an nREPL server embedded in a Java Virtual Machine (JVM).

_Note that because fireplace evaluates code synchronously, so if you evaluate a long-running command, **your entire Vim process will be blocked/frozen** until the operation completes. More on this below._

Usually the task of running an nREPL server falls to Leiningen, but fireplace can connect to any nREPL server. See the [nREPL docs](https://github.com/clojure/tools.nrepl) for details.

## Installing Vim
TODO: Someone should write Vim installation instructions and basic usage here.

### OSX

* Install [Homebrew](http://mxcl.github.com/homebrew/)
* The dance: `brew update`
* If you're like me, you tend to inspect a homebrew formula's options before installing: `brew options macvim`
* And if you're like me: `brew install --override-system-vim macvim`
* Optionally you can: `brew linkapps` which will, by default, create a symlink to `MacVim.app` in `~/Applications`.
* Assuming your `PATH` is set correctly, you can run vim from the command line by typing `mvim`
* If you'd rather launch it from the dock, you can navigate to `~/Applications` and double-click `MacVim.app` assuming you've run `brew linkapps`

### Debian/Ubuntu
TODO: Ubuntu vim install instructions

### Windows
TODO: Windows vim install instructions

## Installing fireplace.vim
Because fireplace.vim handles only the dynamic aspects of Clojure development, a separate plugin, [vim-clojure-static](https://github.com/guns/vim-clojure-static) (extracted from VimClojure and maintained by [Sung Pae](https://github.com/guns)) includes support for syntax highlighting, indentation, etc. You'll want to install both for a pleasant experience.

Installation is covered thoroughly in the [fireplace README](https://github.com/tpope/vim-fireplace/blob/master/README.markdown). Since it's likely to be most up-to-date, we'll defer to it for purposes of installation.

Once installed, there's really nothing to configure. Yay!

## Basics

Let's go through the process of creating a simple project to demonstrate some of fireplace.vim's capabilities.
Because this author is a little lazy, we'll use the same example as the [emacs tutorial](/articles/tutorials/emacs.html). That is, a simple
command-line argument parser.

First, we'll need a new project:

```
$ lein new command-line-args
$ cd command-line-args
```

Now we need to start up Vim as well as the nREPL server. Luckily, just running the Leiningen 2 REPL will start up a server for us. If you're using console Vim, I suggest running
each in its own console.

In console 1:

```
$ lein repl
nREPL server started on port 58197
... REPL help message elided ...
user=>
```

Nice. And in console 2:

```
$ vim
```

If you're using gvim, just start gvim and then run the REPL:

```
$ gvim
$ lein repl
nREPL server started on port 58197
... REPL help message elided ...
user=>
```

_fireplace.vim will automatically figure out the nREPL port from the `target/repl-port` file written by Leiningen._

Now we can start editing code (see [Editing](#editing) below for tips on effectively editing Clojure code in Vim).

Let's add a simple test. Execute `:e test/command_line_args/core_test.clj`, enter insert mode and add the following to the file:

```clojure
(deftest pairs-of-values
   (let [args ["--server" "localhost"
               "--port" "8080"
               "--environment" "production"]]
      (is (= {:server "localhost"
              :port "8080"
              :environment "production"}
              (parse-args args)))))
```

To run the test, save the file, `:w`, and now we'll invoke our first fireplace.vim command, `cpr`. *All commands are run in normal mode, so bang on escape a few times first*. This performs a `(require ... :reload)` on the current buffer/namespace. Here we don't even get a chance to run the test. The code doesn't even compile because `command-line-args.core/parse-args` hasn't been defined yet. Let's fix that.

*Note: Anytime fireplace reports an exception in a namespace, you can get the full stack trace by opening Vim's "location list" with `:lopen`. Hit enter on a Clojure stack frame to jump to the relevant source.*

The easiest way to get to the `command-line-args.core` namespace is to put your cursor on `command-line-args.core` in the `ns` declaration at the top of the file and hit `gf`. This key combination will open the file for the namespace under the cursor. *It even works for namespaces in jar files!*

Next add the following definition in the `command-line-args.core` namespace:

```clojure
(defn parse-args [args]
  {})
```

_Tip: Use `ctrl-w f` (ctrl-w followed by the f key) to split the view into two windows opening the file under the cursor in a new split._

Save the file, `:w`, and then switch back to `test/command_line_args/core_test.clj`. Now we can try requiring the test namespace again. This time we'll use `cpR` (that's with a big 'R'), which performs a `(require ... :reload-all)` on the current namespace. That should succeed, so we can run the tests with the command `:Eval (clojure.test/run-tests)` which will print out a test failure with something like this:

```
Testing command-line-args.core-test

FAIL in (pairs-of-values) (core_test.clj:9)
expected: (= {:server "localhost", :port "8080", :environment "production"} (parse-args args))
  actual: (not (= {:server "localhost", :environment "production", :port "8080"} {}))

Ran 1 tests containing 1 assertions.
1 failures, 0 errors.
{:type :summary, :pass 0, :test 1, :error 0, :fail 1}
```

Note that we know a few fireplace commands now:

* `gf` - Jumps to the namespace under the cursor
* `cpr` and `cpR` - `(require :reload)` or `(require :reload-all)` the current namespace.
* `:Eval (clojure code)` - Evaluate arbitrary Clojure code in the current namespace.
* `:lopen` - Not really a fireplace command, but it opens the stacktrace for the last exception in the buffer.

Now we can fix some errors. Return to `command-line-args.core` and edit:

```clojure
(defn parse-args [args]
  (apply hash-map args))
```

Running our tests again we get another error:

```
Testing command-line-args.core-test

FAIL in (pairs-of-values) (core_test.clj:9)
expected: (= {:server "localhost", :port "8080", :environment "production"} (parse-args args))
  actual: (not (= {:server "localhost", :environment "production", :port "8080"} {"--port"
 "8080", "--server" "localhost", "--environment" "production"}))

Ran 1 tests containing 1 assertions.
1 failures, 0 errors.
{:type :summary, :pass 0, :test 1, :error 0, :fail 1}
```

We need to strip off those dashes and turn them into keywords. Back to `core.clj` again. Let's take a different route this time. Put your cursor on `parse-args` in the test file and hit `] ctrl-D` (two keystrokes). That jumps right to `parse-args`. Now we can fix it up:

```clojure
(defn parse-args [args]
  (into {} (map (fn [[k v]] [(keyword (.replace k "--" "")) v])
                (partition 2 args))))
```

and the test passes.

_Tip_: During testing you may get into the situation where you've deleted an old, possibly failing, test, yet it still runs because it's still in memory. Supposing the name of the test is `a-test`, you have a few options:

* Hit `cqp` (or use `:Eval`) and run `(ns-unmap *ns* 'a-test)` to remove the test from the namespace.
* Restart the Leiningen REPL. fireplace will re-initialize the connection the next time you run a command.

Wondering what `clojure.core/ns-unmap` does? In fireplace, use the command `:Doc ns-unmap` to see the docstring. If a symbol's under your cursor you can just hit `shift-K` to do the same thing.

## The Quasi-REPL and Evaluating Code
Note we've learned another handy command, `cqp`, which opens fireplace's "quasi-repl" where you can execute Clojure code in the current namespace. Tab-completion and command history are supported there as you'd expect. If you need to do a little more editing or you'd like to edit and re-run a previous command, hit `cqc` to bring up a command-line window similar to what you'd get with `q:` in normal Vim.

Many times you'd like to just evaluate some snippet of code. fireplace.vim really shines here because it brings idiomatic Vim to bear on the problem. For example, it's common to have a snippet of test code you're playing with in a namespace, often in a `(comment )` at the end of the file. Let's say we have something like this:

```clojure
(comment
  (my-service-call (choose-server load-balancer)
                   (normalize-request { ... request map ...})))
```

Say we wanted to evaluate the entire `(my-service-call ...)` expression. There are a few ways to do this:

* Put the cursor on `my-service-call` and hit `cpp`. This evaluates the inner-most expression containing the cursor
* Put the cursor on the opening paren and hit `cp%`. `cp` followed by a Vim text motion (see below) executes the text described by that motion. We could also do, for example `cpiw` (inner word) to look at the value of the symbol under the cursor.
* Put the cursor on the opening paren and hit `cq%`. This will grab the text (the `%` text motion again) and pre-populate the command-line window (`cqc` above) with the expression.

With all that it's easy to see we can evaluate the whole `my-service-call` expression, or any of the sub-expressions in the comment. Furthermore, if you're code's all referentially transparent and everything, the `cpX` family of commands have cousins of the form `c!X` which replace the expression with its result.

## The (non-quasi) REPL
Unlike VimClojure, fireplace.vim doesn't have an integrated REPL. I (DR) miss it a little, but [REPL-y](https://github.com/trptcolin/reply), the REPL that ships with Leiningen is very nice and since it's separated from Vim there's much less chance of locking up Vim with a long-running or infinite operation.

Keep in mind that the nREPL server (the lein REPL) and fireplace are both playing in the same JVM. fireplace can see changes you make in nREPL and vice versa. So you can make some changes, save and hit `cpr` to reload the namespace and switch over to the REPL and the changes will be there.

## Editing
This section includes some tips on basic Clojure code editing in Vim.

TODO: Move this to a separate, more general doc?

### "Words"
The `vim-clojure-static` plugin makes some minor adjustments to Vim's settings to improve the editing experience. In particular, it extends the notion of a "word" (`:help word`) to include additional characters like `-` (hyphen), `.` (dot), etc. This changes has a number of effects:

* Insert mode code completion (`ctrl-n` and `ctrl-p`) will complete words with hyphens which is very common in Clojure code
* The insanely useful, magical "star" (`*`) operator in normal mode will search for the full Clojure symbol under the cursor, including dots, hyphens, etc.
* Word motions (`:help w`), include dots, hyphens, etc. So `dw` in normal mode will delete an entire Clojure symbol.

### Wrangling Parentheses
The most effective way to edit Clojure code is *structurally* with [paredit.vim](https://bitbucket.org/kovisoft/paredit/overview), but if you don't have time to learn that, Vim still brings a lot to the table for dealing with all the parentheses in Clojure code.

First, obviously, the `%` motion (`:help %`) is very useful. In normal mode, put the cursor on an opening or closing paren and you can:

* Hit `%` to jump to the matching paren.
* Hit `d%` to delete the parens and everything they contain.
* Hit `y%` to "yank"/copy the parens and everything in them.
* Hit `c%` to delete the parens and the text they contain and start editing.
* Hit `v%` to select the parens and the text they contain visually.


The `%` motion is useful, but it's often more convenient to work with Vim's "block" text object (`:help text-objects`). This manifests in two forms. First, `ab` ("all block") which is an entire form, including the parentheses. Second, `ib` ("inner block") which is all the text within the enclosing parentheses. So, put your cursor anywhere within some parentheses in normal mode and you can:

* Hit `dab` ("delete all block") to delete the entire form.
* Hit `dib` ("delete inner block") to delete everything inside the parens.
* Hit `cab` ("change all block") to delete the entire form and enter insert mode.
* Hit `cib` ("change inner block") to delete the contents of the form, preserving parens, and enter insert mode.
* Hit `yab` ("yank all block") to copy the entire form including parens.
* Hit `yib` ("yank inner block") to copy the everything inside the parens.

And so on. Getting these commands in muscle memory can really speed up working with Clojure forms.

_Tip: Vim has text objects for blocks enclosed in square brackets (vectors), quotes (strings), curly braces (maps, sets) etc. They're all invaluable. `:help text-objects` !!!_

## Code Completion
Some code completion is available with fireplace. It is built using Vim's `omni-complete`
system. When typing a symbol, hit `ctrl-x ctrl-o` to start omni-complete. A popup with a
list of suggestions will appear. Use `ctrl-n` and `ctrl-p` to change the selection and hit
enter to expand a selection into the buffer.

Note that Vim's built-in code completion, `ctrl-n` and `ctrl-p` in insert mode, also work fine while editing Clojure code.

## Getting Documentation

fireplace has handy shortcuts for getting documentation for Clojure functions:

* `shift-K`: Lookup the doc string for the symbol under the cursor. Works for Java classes as well.
* `:Doc`: Lookup the doc string for a symbol entered in a prompt. Works for Java classes as well.

And since source code is often the best/only documentation in Clojure:

* `[d`: display the source for the symbol under the cursor
* `:Source`: display the source code for a symbol entered in a prompt

## Other Resources

## Contributors

[Dave Ray](http://darevay.com), 2013 (original author)
