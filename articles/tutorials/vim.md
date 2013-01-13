---
title: "Clojure with Vim and VimClojure"
layout: article
---

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on
Github](https://github.com/clojuredocs/cds).

_**Note: You may want to take a look at [Clojure with Vim and foreplay.vim](/articles/tutorials/vim_foreplay.html) since it is more actively maintained than VimClojure.**_

## Overview

VimClojure is a Vim plugin providing a Clojure development environment. Highlights include syntax highlighting, code completion, documentation, and an integrated REPL. This guide will cover how to get VimClojure installed, and some basic usage within a typical Clojure project.

## What Version of Clojure and VimClojure Does This Guide Cover?

This guide covers Clojure 1.4 and VimClojure 2.3.6. Most of what's covered here will work both with earlier versions of Clojure and VimClojure.

## Essential References
VimClojure is hosted on BitBucket [here] (https://bitbucket.org/kotarak/vimclojure/). There is a [Github mirror](TODO) as well as the main download on [vim.org](http://www.vim.org/scripts/script.php?script_id=2501), but always keep in mind that the "source of truth" for VimClojure development is the BitBucket site.

You can get help on the [VimClojure mailing list](https://groups.google.com/forum/?fromgroups#!forum/vimclojure).

Finally, don't forget the VimClojure documentation the comes with the plugin itself. `:help vimclojure.txt`.

## How VimClojure Works
Before using VimClojure, it's useful to have a basic understanding of how it works, especially when it comes to evaluating Clojure code. This will help avoid confusion and tears. VimClojure itself is a collection of VimScript code running withing Vim. When it needs to evaluate some Clojure code (for example, you just opened a file, or gave it a command to run the tests in a namespace) it sends commands to a Nailgun server embedded in a Java Virtual Machine (JVM). It sends these commands by executing the Nailgun client which is a lightweight system executable.

_Note that because VimClojure evaluates code synchronously, so if you evaluate a long-running command, **your entire Vim process will be blocked/frozen** until the operation completes. More on this below._

The nailgun server can be manually integrated and executed with any Java process (it's just a jar), but typically the task of starting up the JVM and the nailgun server falls to the [lein-tarsier](https://github.com/sattvik/lein-tarsier) Leiningen plugin.

Most difficulties and misunderstanding with VimClojure are caused by missing or misplaced Nailgun client executable or issues with the Nailgun server.

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

## Installing VimClojure
VimClojure is installed like any other Vim plugin. The "old fashioned" way is to download the latest release from [vim.org](http://www.vim.org/scripts/script.php?script_id=2501) and unzip it into your `~/.vim` or `~/vimfiles` folder depending on OS. Most people prefer a more sane approach to Vim plugin management though.

### With Pathogen
If you're using [Pathogen](http://www.vim.org/scripts/script.php?script_id=2332), simply unzip the VimClojure archive from [vim.org](http://www.vim.org/scripts/script.php?script_id=2332) into your `.vim/bundle` folder.

### With Vundle

If you are already using [Vundle](https://github.com/gmarik/vundle), just add this one line to your `~/.vimrc` after the `Bundle 'gmarik/vundle'` line:

```vim
Bundle 'VimClojure'
```

If you are not already using Vundle, and want to use it:

Step 1 : Add this to the top of your `~/.vimrc`:

```vim
set nocompatible
filetype off
set runtimepath+=~/.vim/bundle/vundle/
call vundle#rc()
Bundle 'gmarik/vundle'
Bundle 'VimClojure'
filetype on
```

Step 2 : Run this in the terminal:

```bash
mkdir -p ~/.vim/bundle
git clone http://github.com/gmarik/vundle.git ~/.vim/bundle/vundle
vim +BundleInstall +qall
```

### Setting up the Nailgun Client
In order to get the capabilities of VimClojure beyond just indentation and syntax highlighting, you'll need a Nailgun client. It can be downloaded [here](http://kotka.de/projects/vimclojure/vimclojure-nailgun-client-2.3.0.zip). For completeness, the Nailgun homepage is [here](http://www.martiansoftware.com/nailgun/quickstart.html).

For OSX and Linux, expand the archive, and run:

```bash
$ cd vimclojure-nailgun-client
$ make
```

This will produce a standalone executable, `ng`, which is the Nailgun client.

For Windows users, `ng.exe` is provided pre-built in the archive.

In either case, once you have the Nailgun client executable, put it somewhere, either on the sytem path, or in a directory accessible by Vim, e.g. `~/bin/ng`. You'll need to remember this location for the "Configuring VimClojure" step below.

### Installing lein-tarsier
Properly setting up the project classpath and running the Nailgun server has historically been one of the trickier aspects of getting started with VimClojure. Fortunately, two things have happened. The Clojure community has pretty much settled on [Leiningen](https://github.com/technomancy/leiningen) as its de facto build tool and Daniel Solano Gómez has developed the [lein-tarsier](https://github.com/sattvik/lein-tarsier) plugin which makes starting a Nailgun server from a Leiningen-based project a piece of cake.

So, for Leiningen 2 based projects add `[lein-tarsier "0.9.4"]` to the `plugins` vector of your `:user`
profile located in `~/.lein/profiles.clj`.  For example:

```clj
    {:user {:plugins [[lein-tarsier "0.9.4"]]}}
```

This setup is covered in more detail in the [lein-tarsier](https://github.com/sattvik/lein-tarsier) readme.

## Configuring VimClojure

Once the plugin's installed and you have lein-tarsier, you can configure VimClojure in your `~/.vimrc` file. Here's a configuration with all the bells and whistles:

```
" Automatically determine indenting using fuzzy matching. e.g. the a line starting "(with-"
" will be indented two spaces.
let vimclojure#FuzzyIndent=1

" Highlight built-in functions from clojure.core and friends
let vimclojure#HighlightBuiltins=1

" Highlight functions from contrib
let vimclojure#HighlightContrib=1

" As new symbols are identified using VimClojure's dynamic features, automatically
" highlight them.
let vimclojure#DynamicHighlighting=1

" Color parens so they're easier to match visually
let vimclojure#ParenRainbow=1

" Yes, I want nailgun support
let vimclojure#WantNailgun = 1

" Full path to the nailgun client
let vimclojure#NailgunClient = "/full/path/to/ng"
```

Note the setting for the Nailgun client on the last line.

See the VimClojure help for more details on these settings and more.

### Local Leader
Most VimClojure shortcuts are initiated with Vim's "local leader", (`:help maplocalleader`). The default value is `\` (backslash), but can be configured as follows:

```vim
" Set local leader to comma
let maplocalleader=","
```

`\` will be assumed throughout this tutorial for consistency.

## Basics

Let's go through the process of creating a simple project to demonstrate some of VimClojure's capabilities.
Because this author is a little lazy, we'll use the same example as the emacs tutorial. That is, a simple
command-line argument parser.

First, we'll need a new project:

```bash
$ lein new command-line-args
$ cd command-line-args
```

Now we need to start up Vim as well as the Nailgun server. If you're using console Vim, I suggest running
each in its own console.

In console 1:

```bash
$ lein vimclojure
Starting VimClojure server on localhost, 2113
... more output ...
```

and in console 2

```bash
$ vim
```

If you're using gvim, just start gvim and then run the Nailgun server:

```bash
$ gvim
$ lein vimclojure
Starting VimClojure server on localhost, 2113
... more output ...
```

Now we can start editing code (see [Editing](#editing) below for tips on effectively editing Clojure code in Vim).
Let's add a simple test. Execute `:e test/command_line_args/core_test.clj`, enter
insert mode and add the following to the file:

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

_Note that the first time you open a Clojure file with VimClojure, there will be a slight pause while stuff
is initialized in the Nailgun server._

To run the test, save the file, `:w`, and now we'll invoke our first VimClojure command, `\rt`. This
reloads the current buffer/namespace and runs all tests it contains. A result window will appear
with the result of the test. You can close the result window from any VimClojure buffer with `\p`.

In this case, the code doesn't even compile because `command-line-args.core/parse-args`
hasn't been defined yet. Let's fix that. Execute `:e src/command_line_args/core.clj`, and add the following
definition:

```clojure
(defn parse-args [args]
  {})
```

_tip: Use `ctrl-w s` (ctrl-w followed by the s key) to split the view into two windows._

Save the file, `:w`, tell VimClojure to evaluate it, `\ef`, and then switch back to `test/command_line_args/core_test.clj`
and re-run the test with `\rt`. Note that we know three VimClojure commands now:

* `\ef` - Evaluate the contents of the current buffer
* `\rt` - Reload the contents of the current buffer and execute any tests
* `\p` - Close the VimClojure result window from any buffer

In both cases, the results of the evaluation, including anything printed to stdout, are displayed in
the VimClojure result window. Running multiple commands will simply replace the previous contents of
the result window.

Running the test again, we'll see output something like this in the result window:

```clojure
FAIL in (pairs-of-values) (core_test.clj:9)
expected: (= {:server "localhost", :port "8080", :environment "production"} (parse-args args))
  actual: (not (= {:server "localhost", :environment "production", :port "8080"} {}))
```

Now we can fix that:

```clojure
(defn parse-args [args]
  (apply hash-map args))
```

Running our tests again (save and eval `core.clj` and then `\rt` in the test file) and we now get
another error:

```clojure
FAIL in (pairs-of-values) (core_test.clj:9)
expected: (= {:server "localhost", :port "8080", :environment "production"} (parse-args args))
  actual: (not (= {:server "localhost", :environment "production", :port "8080"}
                  {"--port" "8080", "--server" "localhost", "--environment" "production"}))
```

We need to strip off those dashes and turn them into keywords. Back to `core.clj` again:

```clojure
(defn parse-args [args]
  (into {} (map (fn [[k v]] [(keyword (.replace k "--" "")) v])
                (partition 2 args))))
```

and the test passes.

Tip: You may notice that the even if you delete the dummy test, `a-test` that's generated by Leiningen,
it still runs. Since VimClojure simply evaluates the file, the definition of that test hangs around.
You have a few options:

* Add `(def a-test nil)` to your file and re-run the tests. That will kill the test definition.
* Restart the Nailgun server. VimClojure wil re-initialize the next time you run a command.

## The REPL

VimClojure includes an integrated REPL. There are a few ways to open it:

* If you're not in a Clojure buffer, execute the command `:ClojureRepl`.
* `\sr` will open a new REPL in the `user` namespace
* `\sR` will open a new REPL in the namespace of the current buffer

Once you're in the REPL, it's fairly simple to use. Enter commands in insert mode and hit enter (at the end of the line) to execute.
`ctrl-up` and `ctrl-down` move through the command history. Since it's Vim, you can escape into normal
mode anytime to edit/copy/paste/whatever commands. `ctrl-enter` is a shortcut for executing the current
command without first moving to the end of the line.

You can open as many REPL buffers as you like in VimClojure. To close a REPL, just close the REPL buffer
like any other Vim buffer.

By default the REPL prints results in a fairly plain way. You can toggle pretty printing of results
using the `,toggle-pprint` command in the REPL:

```clojure
user=> (def m {:a-map "with" :very-very-very "long long long" :contents "that don't fit"})
#'user/m
user=> m
{:very-very-very "long long long", :a-map "with", :contents "that don't fit"}
user=> ,toggle-pprint
true
user=> m
{:very-very-very "long long long",
 :a-map "with",
 :contents "that don't fit"}
```

### The REPL and Long-Running Operations
As noted above, Clojure executes commands synchronously, locking up the Vim process while they
execute. Usually operations execute very quickly so there's no problem, but you may accidentally
create an infinite loop, or actually want to run a longer operation without interrupting your
editing session.

If you find you're stuck in an infinite loop, unfortunately, your best bet is to kill and restart
the nailgun server.

If you intentionally want to run a long operation, one approach is to start the Nailgun server
with the repl option set:

```bash
$ lein vimclojure :repl true
Starting VimClojure server on localhost, 2113
nREPL server started on port 57695
REPL-y 0.1.0-beta10
Clojure 1.4.0
    Exit: Control+D or (exit) or (quit)
Commands: (user/help)
    Docs: (doc function-name-here)
          (find-doc "part-of-name-here")
  Source: (source function-name-here)
          (user/sourcery function-name-here)
 Javadoc: (javadoc java-object-or-class-here)
Examples from clojuredocs.org: [clojuredocs or cdoc]
          (user/clojuredocs name-here)
          (user/clojuredocs "ns-here" "name-here")
user=>
```

This starts the Nailgun server as usual, but also starts a Leiningen/reply REPL in the same
process. So, you can switch over to the REPL, run your long-running operation, and then
go back to Vim and continue editing while it executes.

## Editing
This section includes some tips on basic Clojure code editing in Vim.

### "Words"
The VimClojure plugin makes some minor adjustments to Vim's settings to improve the editing experience. In particular, it extends the notion of a "word" (`:help word`) to include additional characters like `-` (hyphen), `.` (dot), etc. This changes has a number of effects:

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
Some code completion is available with VimClojure. It is built using Vim's `omni-complete`
system. When typing a symbol, hit `ctrl-x ctrl-o` to start omni-complete. A popup with a
list of suggestions will appear. Use `ctrl-n` and `ctrl-p` to change the selection and hit
enter to expand a selection into the buffer.

Note that Vim's built-in code completion, `ctrl-n` and `ctrl-p` in insert mode, also work fine while editing Clojure code.

## Getting Documentation

VimClojure has handy shortcuts for getting documentation for Clojure functions:

* `\lw`: Lookup the doc string for the symbol under the cursor (lookup word)
* `\li`: Lookup the doc string for a symbol entered in a prompt (lookup interactive)
* `\jw`: Open the Javadoc for the Java symbol under the cursor in a browser window
* `\ji`: Open the Javadoc for a Java symbol entered in a prompt

Notice how most of the shortcuts start with `l`, a mnemonic for "lookup"

And since source code is often the best/only documentation in Clojure:

* `\sw`: display the source for the symbol under the cursor
* `\si`: display the source code for a symbol entered in a prompt

## Cheatsheet

Here's a brief list of some essential VimClojure shortcuts:

* `\p`: Close the VimClojure result window
* `\ef`: Evaluate the current buffer
* `\et`: Evaluate the top-level form enclosing the cursor
* `\el`: Evaluate the current line
* `\rt`: Evaluate the current buffer and run tests
* `\me`: Macro expand the innermost S-expression currently containing the cursor
* `\m1`: Same as `\me`, but use `macroexpand-1`

More shortcuts are covered in the VimClojure help files, but these cover most usage.

## Other Resources

* [vimclojure-easy](https://github.com/daveray/vimclojure-easy) is a bare bones setup for VimClojure aimed
at just getting something working.

## Contributors

[Dave Ray](http://darevay.com), 2012 (original author)
