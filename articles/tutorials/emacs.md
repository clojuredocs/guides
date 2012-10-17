---
title: "Clojure with Emacs"
layout: article
---

## Overview

Emacs has traditionally been one of the best development environments
for functional languages and Lisps in particular. This guide will
explain how to get it installed, and give an example of a basic
workflow to use while developing a simple library.

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4 and Emacs 24. Most packages should work
on earlier versions of Emacs, but they will be more work to install
since the package manager is new in 24.

## Installing Emacs ##

### OSX ###

By far the easiest way to get going with Emacs on OSX is to use
[Homebrew](http://mxcl.github.com/homebrew/). Instructions for
installation are
[here](https://github.com/mxcl/homebrew/wiki/installation). You also
need to ensure you have XCode installed for Homebrew to work
correctly.

Once brew is installed, you can install Emacs 24 using:

```bash
$ brew install emacs --cocoa
```

This should install Emacs 24.x, which is required for the
emacs-starter-kit.

After compiling, Emacs will be living happily somewhere in your
cellar. You can check this:

```bash
$ ls /usr/local/Cellar/emacs/24.x
```

You will find that Homebrew has created a Emacs.app for you which you
can copy to your /Applications folder for easy launching. I tried
symlinking, but it didnt work for me.

If you have customizations to your environment (say in `.profile` or
your shell-specific config) you can add [this](https://gist.github.com/3887459) function to fix the path
issues when launching Emacs from the GUI on OS X
([Thanks to Steve Purcell on the Clojure mailing list for this](http://www.mail-archive.com/clojure@googlegroups.com/msg36929.html)):

```scheme
;; fix the PATH variable
(defun set-exec-path-from-shell-PATH ()
  (let ((path-from-shell (shell-command-to-string "$SHELL -i -c 'echo $PATH'")))
    (setenv "PATH" path-from-shell)
    (setq exec-path (split-string path-from-shell path-separator))))

(when window-system (set-exec-path-from-shell-PATH))
```

This makes sure that all of the stuff you have on your PATH actually
gets respected in the GUI Emacs, no matter how you start it.

### Debian/Ubuntu ###

Newer Debian-based systems (post-wheezy) ship Emacs 24 in apt:

```bash
$ sudo aptitude install emacs24
```

On older systems you can add unofficial package sources for
`emacs-snapshot`, either for [Debian](http://emacs.naquadah.org/) or
[Ubuntu](https://launchpad.net/~cassou/+archive/emacs).

## Configuring Emacs ##

So Emacs is installed, but running it now would be a somewhat
barebones experience and not particularly useful for Clojure
development.

Emacs can be configured through a folder in your home folder called
[~/.emacs.d](http://www.emacswiki.org/emacs/DotEmacsDotD), and
configuration options are pretty much endless. To help you through
this, Phil Hagelberg has created a starter kit with lots of sensible
defaults for you called
[emacs-starter-kit](https://github.com/technomancy/emacs-starter-kit).

Installation instructions on the github project are very thorough, so
you should follow those carefully. My personal setup lives in my
[dotfiles
project](https://github.com/gar3thjon3s/dotfiles/tree/master/.emacs.d),
which you can look at for inspiration for Clojure specific things. You
will certainly want to add clojure-mode, and clojure-test-mode to your
list of packages to install:

```scheme
(defvar my-packages '(starter-kit
                      starter-kit-lisp
                      starter-kit-bindings
                      starter-kit-ruby
                      starter-kit-eshell
                      clojure-mode
                      clojure-test-mode)
```
                      
Start emacs:

```bash
$ emacs -nw
```

A lot of messages will likely whizz by as it installs and compiles
packages. Unless you have any actual *errors* this is all fine.

To look at the other packages available for installation you can do
(from inside Emacs):

```
M-x package-list-packages
```

M-x means meta-x, and meta is mapped to the alt key on Ubuntu and
either the alt key or the apple key on the mac. A list of available
packages should load up. To manually install a package, move to the
package with the keyboard and press 'i' for 'install'. After selecting
all the packages you are interested in, press 'x' for 'eXecute' to
install. A better idea is to simply add the package to the var
`my-packages` in your init.el and restart emacs.

Start up Emacs (if it's not already running):

```bash
$ emacs -nw
```

and follow along!

### Basics ###

The first thing you should do without question, is to go through the
built-in Emacs tutorial. To do this press `C-h t` or hold down Control
and press `h` and then press `t`.

With that in mind, these are the basic keystrokes you're going to be
using most often:

```
File/buffer/window commands
C-x C-f     Find file
C-x C-s     Save buffer
C-x s       Save file (like save-as)
C-x b       Switch buffer
C-x k       Kill buffer
C-x 1       Delete other windows
C-x 0       Delete current window
C-x 2       Split window horizontally
C-x 3       Split window vertically

Movement commands
C-a         Beginning of line
C-e         End of line
C-n         Next line (down)
C-p         Previous line (up)
C-b         Back (left)
C-f         Forward (right)
M-f         Forward a word
M-b         Back a word
C-v         Forward a page
M-v         Back a page

Edit commands
C-d         Kill character
M-d         Kill word
M-delete    Kill word backwards (I have this mapped to C-w)

Misc commands
C-s         Regex search forwards
C-r         Regex search backwards
M-%         Query replace
```

I should also mention the help commands:

```
C-h t     Tutorial (goes over the basics)
C-h b     Describe all current key bindings
C-h m     Describe the current mode
C-h a     Apropos - search the help for a term
C-h k     Describe key
```

I recommend going through the tutorial at least once as it will give
you a good understanding of the navigation and movement commands.
Another useful command you will use a lot is `M-x` which allows you to
run any command. And there are a LOT. Apropos is very useful for
searching for something `C-h a`.

So after doing the tutorial (you did do that, RIGHT? O_O) you can move
around, open files, save files, etc and are generally comfortable at
the basics. There is an almost infinite amount of things to learn
about Emacs, but those basics will get you a long way.

## Creating a project ##

Lets go through the process of creating a small sample clojure project
and illustrate how Emacs helps makes us champions in the land of lisp.

The project we will be building is a trivially simple command line
parser, that will take the argument pairs given to it and turn them
into a map of key-value pairs. The functionality is irrelevant and not
particularly useful, it serves purely to illustrate the development
flow.

If you don't have [Leiningen](http://leiningen.org) yet, get it
installed and then use it to create a new project:

```bash
$ lein new command-line-args
$ cd command-line-args
```

Start up Emacs and `M-x cd` to the command-line-args folder. Open and
edit your project.clj to look like this:

```clojure
(defproject command-line-args "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :plugins [[lein-swank "1.4.4"]])
```

We are adding a plugin for `lein-swank` which allows us to keep a
server running in the background to send code to as we write. You can
also add plugins at the user level, see the Leiningen docs for
details. A full reference for what can go in this file lives in the
Leiningen project
[here](https://github.com/technomancy/leiningen/blob/master/sample.project.clj)

Alternatively we could have used `nrepl-jack-in`, but there is
currently no support for `clojure-test-mode` and we will be using
that to run our tests from Emacs.

At this point we are ready to start coding. Take a look at the project
structure:

```
+ doc
  - intro.md
- project.clj
- README.md
+ src
  + command_line_args
    - core.clj
+ test
  + command_line_args
    - core_test.clj
```

Should be fairly self-explanatory, namespaces in clojure are
represented as folders in the filesystem (like packages in java) and
the test structure mirrors that of the src structure. 

Lets start up a server and connect to it from Emacs using
clojure-mode:

```
M-x clojure-jack-in
```

This should open up a new window looking at our \*slime-repl nil\*
buffer.

First thing to do is add a simple test (in fact the only test we will
be adding because by default, we get it right first time). Open the
`core_test.clj` file inside of the test folder. Replace the test that
is there with the following:

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

We are simply assigning a list of arguments as they would arrive from
the command line to a "variable" called args, and asserting that the
return from a function called `parse-args` is equal to those command
line args turned into a simple map.

To run this test, save the file `C-x C-s` and using clojure-test-mode
we can do `C-c ,`. We should get an error in a buffer complaining that
`parse-args` does not exist. To dismiss this buffer, simply press `q`.
The buffer should go away, and the cursor should be back in the code
where you left it (another way of getting this type of compilation
error is to compile the buffer using `C-c C-k`). Lets try and fix the
exception by opening `core.clj` (`C-x C-f`) and adding the following
definition:

```clojure
(defn parse-args [args]
  {})
```

Save the file (`C-x C-s`), switch back to the test buffer (`C-x b
ENTER`) and run the test again (`C-c ,`). This time you should get a
message in the mini-buffer (the small line at the bottom of you
screen) telling you one test has failed AND you should have a red bar
across the `is` assertion. To check what the problem was, we can move
our cursor over the red bar and press `C-c '`. This shows the problem
with the assertion in the mini-buffer:

```clojure
(not (= {:server "localhost", 
      :port "8080", 
      :environment "production"} 
      {}))
```

Awesome! So our map was empty as expected. Lets fix that:

```clojure
(defn parse-args [args]
  (apply hash-map args))
```

Running our tests again we now get another error:

```clojure
(not (= {:server "localhost", 
         :port "8080", 
         :environment "production"} 
        {"--port" "8080", 
         "--server" "localhost", 
         "--environment" "production"}))
```

Whoops our keys are just strings with the dashes still in place. We
need to strip those off and turn them into keywords:

```clojure
(defn parse-args [args]
  (into {} (map (fn [[k v]] [(keyword (.replace k "--" "")) v])
                (partition 2 args))))
```

And re-running the tests in the test buffer we are all happy. If we
had multiple test files we can run them all from the CLI using:

```bash
$ lein test
```

So that is an extremely simple example of a workflow using Emacs with
clojure-mode and clojure-test-mode. 

## Using the REPL ##

One thing we haven't looked at is how useful having an open running
REPL in Emacs can be for development. If you still have your project
open, split the window (`C-x 2` (horizontal) or `C-x 3` (vertical)) in
two so you have the `core.clj` and \*slime-repl nil\* buffers open.
Lets say you are editing the core.clj and you want to play around with
the functions as you define them. Looking at `parse-args` you have
decided you want to pull out the anonymous function to be a named
function `keywordize`.

First load and compile the buffer into the slime process with `C-c
C-k`. Now switch to the REPL window (you can hold the shift key and
use the arrow keys to move around windows if you are using GUI Emacs
or use `C-x o` to switch to other). Change the namespace you are in:

    user> (in-ns 'command-line-args.core)

Your REPL prompt should update to reflect this:

    command-line-args.core>

You now have access to the functions in this namespace (that were
defined when you loaded the file). Try it:

    command-line-args.core> (parse-args '("key" "value"))
    {:key "value"}

A shortcut to moving to this namespace is to be inside the source code
buffer and press `C-c M-p`. Thanks to [Jake
McCrary](http://jakemccrary.com/) for that tip.

Lets go ahead and create our new function in `core.clj`:

```clojure
(defn keywordize [kvp]
  (let [[k v] kvp]
    [(keyword (.replace k "--" "")) v]))

(defn parse-args [args]
  (into {} (map keywordize (partition 2 args))))
```

Now we have a couple of options, we could re-compile the file again
(`C-c C-k`) or we could evaluate each function on its own by going to
the end of the s-exp and using `C-x C-e` which sends the s-exp to the
running REPL. Now switching back to the REPL we can try out our
keywordize function:

    command-line-args.core> (keywordize ["--oh" "hai"])
    [:oh "hai"]

If your REPL is starting to get cluttered you can `C-c M-o` to clear
it which is nice. The ability to continually change the code and play
around with it is one of the things that makes Emacs and a lisp a
great combination for development.

Another incredibly useful command is `C-c I` for inspecting
values. Try this in your repl:

    command-line-args.core> (def foo {:a "a" :b "b"})
    #'command-line-args.core/foo    

Now move your cursor in the REPL of the `foo` symbol and press `C-c
I`. After pressing enter to confirm `foo`, a new buffer pops up
showing you lots of interesting things about the value it contains:

    {:a "a", :b "b"}
    --------------------
    Class: class clojure.lang.PersistentArrayMap
    Count: 2
    Contents: 
      :a = a
      :b = b

You can move your cursor to the values and press enter to drill down
to see more info. Pressing `q` again will dismiss this buffer.

If you find yourself wanting to repeat a command you just typed at the
REPL, you can hold down Control and use the arrow-up key to scroll
back through history. Also, all of the Emacs editing commands are
available in the REPL which is great.

A handy clojure function to use in the REPL is `doc` which gives you
the clojure doc for a given function:

    command-line-args.core> (doc println)
    -------------------------
    clojure.core/println
    ([& more])
      Same as print followed by (newline)
    nil

However there is a shortcut `C-c C-d C-d` when your cursor is over a
function name. This will show the Clojure doc in a new window. If
instead you want to jump to the source of the function you can use
`M-.`, which is awesome. This works on your own functions and the
Clojure functions if available (which they will be if you are using
Leiningen the way described here). Incidentally to see all of symbols
in a file and fuzzy-find the one you are looking for you can use `C-x
C-i`, this is a big time saver. Its like `C-x C-f` but for functions.

Another useful shortcut brought to my attention by my colleague [Jake
McCrary](http://jakemccrary.com/) is `M-p` for auto-completion based
on items in your history. `M-/` will also complete any individual
symbol you are typing (this works in the source code and the REPL).

When you are finished with the repl (or if for some reason it has
gotten into a bad state), you can simply kill the `*slime-repl nil*`
buffer (and re-run `cojure-jack-in` to start another).

## Appendix ##

Here are all the commands I have used that are not documented at the
beginning (thanks to Mikael Sundberg for this suggestion):

    C-c ,          Run tests
    C-c '          View test failure messages
    C-c C-k        Compile and load buffer
    C-x C-e        Evaluate sexp
    S-arrow keys   Move between windows
    C-c M-o        Clear REPL buffer
    M-p            Auto-complete line in REPL
    M-/            Auto-complete word
    C-c I          Evaluate an expression and inspect the result
    q              Dismiss a temporary buffer that has appeared
    C-c C-d C-d    Show docs for function
    C-x C-i        Fuzzy-find all symbols in buffer

## Contributors

[Gareth Jones](http://blog.gaz-jones.com), 2012 (original author)

Thanks to [Phil Hagelberg](http://technomancy.us/), [Mikael
Sundberg](http://cleancode.se/), and [Jake
McCrary](http://jakemccrary.com/) for suggestions for improvements to
the original blog posts from which this guide was created.
