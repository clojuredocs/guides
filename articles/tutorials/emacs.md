---
title: "Clojure with Emacs"
layout: article
---

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on
Github](https://github.com/clojuredocs/guides).


## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.5+ and Emacs 24.x. Earlier Clojure and Emacs releases
are not supported.

## Overview

Emacs has traditionally been one of the best development environments
for functional languages and Lisps in particular. This guide will
explain how to get it installed, and give an example of a basic
workflow to use while developing a simple library.


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
$ brew install emacs --with-cocoa --srgb
$ brew linkapps Emacs
```

This should install the latest version of Emacs and symlink `Emacs.app`
to your `~/Applications` folder.

After compiling, Emacs will be living happily somewhere in your
cellar. You can check this:

```bash
$ ls /usr/local/Cellar/emacs/24.x
```

If you have customizations to your environment (say in `.profile` or
your shell-specific config) you can add [this](https://gist.github.com/3887459) function to fix the path
issues when launching Emacs from the GUI on OS X
([Thanks to Steve Purcell on the Clojure mailing list for this](http://www.mail-archive.com/clojure@googlegroups.com/msg36929.html)):

```cl
;; fix the PATH variable
(defun set-exec-path-from-shell-PATH ()
  (let ((path-from-shell (shell-command-to-string "TERM=vt100 $SHELL -i -c 'echo $PATH'")))
    (setenv "PATH" path-from-shell)
    (setq exec-path (split-string path-from-shell path-separator))))

(when window-system (set-exec-path-from-shell-PATH))
```

This makes sure that all of the stuff you have on your PATH actually
gets respected in the GUI Emacs, no matter how you start it.

There's also a package called
[exec-path-from-shell](https://github.com/purcell/exec-path-from-shell)
that automates this. OS X users are advised to install it!

### Debian/Ubuntu ###

Newer Debian-based systems (post-wheezy) ship Emacs 24 in apt:

```bash
$ sudo aptitude install emacs24
```

On older systems you can add unofficial package sources for
`emacs-snapshot`, either for [Debian](http://emacs.naquadah.org/) or
[Ubuntu](https://launchpad.net/~cassou/+archive/emacs).

### MS Windows ###

You can find Emacs for Windows in the [FSF FTP
directory](http://ftp.gnu.org/pub/gnu/emacs/windows/).

Download the file named `emacs-24.1-bin-i386.zip` and unzip it in a new folder.
Avoid folder with spaces in their names such as `C:\Documents and Settings`.
Prefer folder names such as `C:\emacs-24.1`.

[Create an environment variable](http://support.microsoft.com/kb/310519#tocHeadRef)
with name HOME and value equal to the location of your home folder; in Windows
XP, it's `C:\Documents and Settings\YourUsername`, in Windows 7, it's
`C:\Users\YourUsername`. With this variable set, you can use the tilde character
(`~`) to type the name of a file under your home folder and Emacs will expand
its full path.

The following section describes Emacs configuration using the folder `.emacs.d`.
When using Emacs in Windows, you should create this folder under your home
folder. In Windows XP, that will be the folder `C:\Documents and
Settings\YourUsername\.emacs.d`; in Windows 7, that will be the folder
`C:\Users\YourUsername\.emacs.d`.

## Configuring Emacs ##

So Emacs is installed, but running it now would be a somewhat
barebones experience and not particularly useful for Clojure
development.

### Manual setup ###

Emacs can be configured through a folder in your home folder called
[~/.emacs.d](http://www.emacswiki.org/emacs/DotEmacsDotD), and
configuration options are pretty much endless. To help you through
this, Phil Hagelberg has created a small library enables a few
non-intrusive helpful features called
[better-defaults](https://github.com/technomancy/better-defaults)
which might be useful if you are not already an Emacs pro.

Most Emacs packages are kept at [MELPA](http://melpa.milkbox.net),
the community package host. Add this code to your config in
`~/.emacs.d/init.el` to tell Emacs to look there:

For the stable repository:
```cl
(require 'package)
(add-to-list 'package-archives
             '("melpa-stable" . "http://stable.melpa.org/packages/") t)
(package-initialize)
```

For the latest packages:
```cl
(require 'package)
(add-to-list 'package-archives
             '("melpa" . "http://melpa.org/packages/") t)
(package-initialize)
```

Run `M-x package-refresh-contents` to pull in the package listing.

`M-x` means `meta-x`, and meta is mapped to the alt key on most keyboards,
though Mac OS X usually maps it to the command key.

You'll need to install the following packages:

* [clojure-mode](https://github.com/clojure-emacs/clojure-mode) - a major mode for editing Clojure and ClojureScript code
* [CIDER](https://github.com/clojure-emacs/cider) - a Clojure interactive development environment and REPL for Emacs
* [projectile](https://github.com/bbatsov/projectile)(optional) - for navigating inside your projects swiftly

Before continuing any further you should briefly consult their documentation.

You can either install each package one-by-one with `M-x
package-install` or specify all your packages in Emacs Lisp as part of
your configuration file. This is helpful if you take your dotfiles to
a new machine; you don't have to remember everything you've installed
by hand.

```cl
(defvar my-packages '(better-defaults
                      projectile
                      clojure-mode
                      cider))

(dolist (p my-packages)
  (unless (package-installed-p p)
    (package-install p)))
```

Put the code above in `~/.emacs.d/init.el` and run it with `M-x
eval-buffer`.

A lot of warnings will likely whizz by as it installs and compiles
packages. Unless you have any actual *errors* this is all fine.

To look at the other packages available for installation you can
invoke `M-x package-list-packages`. To manually install a package,
move the point to line of the package with the keyboard and press 'i'
for 'install'. After selecting all the packages you are interested in,
press 'x' for 'eXecute' to install.

### Preconfigured setup ###

There are also some ready-made Emacs configurations that are optimized
for Clojure development -
[Prelude](https://github.com/bbatsov/prelude)(developed by the
maintainer of CIDER and clojure-mode) and
[Emacs Live](https://github.com/overtone/emacs-live).

If you want a more powerful Emacs setup you should definitely check them out.

### Basics ###

The first thing you should do without question, is to go through the
built-in Emacs tutorial. To do this press `C-h t` or hold down Control
and press `h` and then press `t` by itself.

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
M-delete    Kill word backwards

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
around, open files, save files, etc., and are generally comfortable at
the basics. There is an almost infinite amount of things to learn
about Emacs, but those basics will get you a long way.

## Creating a project ##

Let's go through the process of creating a small sample clojure project
and illustrate how Emacs helps makes us champions in the land of lisp.

The project we will be building is a trivially simple command line
parser that will take the argument pairs given to it and turn them
into a map of key-value pairs. The functionality is irrelevant and not
particularly useful. It serves purely to illustrate the development
flow.

If you don't have [Leiningen](http://leiningen.org) yet, get it
installed and then use it to create a new project:

```bash
$ lein new command-line-args
$ cd command-line-args
```

Take a look at the project structure:

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

Should be fairly self-explanatory, though Leiningen's built-in
tutorial (available via `lein help tutorial`) provides a detailed
explanation of the project structure.

Before we continue we have to make our project play with CIDER, which
requires a bit of one time setup.  Open the `project.clj` file and add
the `cider-nrepl` plugin there. The file will initially look like this
(more or less):

```clojure
(defproject command-line-args "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]])
```

You need to get to this (keep in mind that by the time you read this `cider-nrepl`
will likely have a newer version):

```clojure
(defproject command-line-args "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:plugins [[cider/cider-nrepl "0.7.0"]]}})
```

Let's start up a live REPL session.

```
M-x cider-jack-in
```

This should open up a new window looking at our `*cider-repl*` buffer.

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
the command line to a local called args, and asserting that the
return value from a function called `parse-args` is equal to those
command line args turned into a simple map.

Compile the file with `C-c C-k`. We should get an error message at the
bottom of the emacs window complaining that clojure can't find
parse-args.  Let's try to fix the exception by opening `core.clj` (`C-x
C-f`) and adding the following definition:

```clojure
(defn parse-args [args]
  {})
```

Compile this with `C-c C-k`, save it (`C-x C-s`), switch back to the
test buffer (`C-x b ENTER`) and try compiling again (`C-c C-k`). This
time it will succeed, so try running the tests with `C-c ,` and you
should get a test report buffer showing some failure information. To check what the problem was, we can
move our cursor over the red bar and press `C-c '`. This shows the
problem with the assertion in the mini-buffer:

```clojure
(not (= {:server "localhost",
         :port "8080",
         :environment "production"}
        {}))
```

Anyway, our map was empty as expected. Let's fix that:

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

Whoops, our keys are just strings with the dashes still in place. We
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

Re-running all the tests from Leiningen can be a good sanity check
before you wrap up work on a feature or branch since there are some
cases where developing from a REPL can give misleading results. For
instance, if you delete a function definition but still call it from
other functions, you won't notice until your process is restarted.

So that is an extremely simple example of a workflow using Emacs with
`clojure-mode` and `cider-test`.

## Using the REPL ##

One thing we haven't looked at is how useful having an open running
REPL in Emacs can be for development. If you still have your project
open, split the window (`C-x 2` (horizontal) or `C-x 3` (vertical)) in
two so you have the `core.clj` and `*cider-repl*` buffers open.
Let's say you are editing the core.clj and you want to play around with
the functions as you define them. Looking at `parse-args` you have
decided you want to pull out the anonymous function to be a named
function `keywordize`.

First load and compile the buffer into the REPL process with `C-c
C-k`. Change the namespace of the REPL buffer to the one of the file
you're in with `C-c M-n`. Now switch to the REPL window with `C-x o`.

You now have access to the functions in this namespace that were
defined when you compiled the file. Try it:

    command-line-args.core> (parse-args '("key" "value"))
    {:key "value"}

Let's go ahead and create our new function in `core.clj`:

```clojure
(defn keywordize [kvp]
  (let [[k v] kvp]
    [(keyword (.replace k "--" "")) v]))

(defn parse-args [args]
  (into {} (map keywordize (partition 2 args))))
```

Now we have a couple of options, we could re-compile the whole file
again (`C-c C-k`) or we could evaluate each function on its own by going
to the end of the s-exp and using `C-x C-e` which sends the s-exp to the
running REPL. Now switching back to the core.clj namespace (`C-c M-n`)
and switching back to the REPL buffer we can try out our keywordize
function:

    command-line-args.core> (keywordize ["--oh" "hai"])
    [:oh "hai"]

If your REPL is starting to get cluttered you can `C-c M-o` to clear
it which is nice. The ability to continually change the code and play
around with it is one of the things that makes Emacs and a lisp a
great combination for development.

If you find yourself wanting to repeat a command you just typed at the
REPL, you can use `M-p` scroll back through history and `M-n` to go
forwards. Also, all of the Emacs editing commands are available in the
REPL, which is great.

A handy clojure function to use in the REPL is `clojure.repl/doc` which
gives you the docstring for a given function:

    command-line-args.core> (use 'clojure.repl)
    nil
    command-line-args.core> (doc println)
    -------------------------
    clojure.core/println
    ([& more])
      Same as print followed by (newline)
    nil

However there is a shortcut `C-c C-d d` when your cursor is over a
function name. This will show the Clojure (or Javadoc) doc in a new window. If
instead you want to jump to the source of the function you can use
`M-.`, which is awesome. This works on your own functions as well as
those which come from third-party libraries. Use `M-,` to pop the
stack and return to where you were. For all the definitions in a
single file you can use `M-x imenu` to list them and jump to one.

When you are finished with the REPL (or if for some reason it has
gotten into a bad state), you can simply kill the `*cider-repl*`
buffer by typing `M-x cider-quit` and re-run `cider-jack-in` to start another.

## Appendix ##

[MELPA documentation](http://stable.melpa.org/#/getting-started)

CIDER keyboard shortcuts can be found in [CIDER documentation](https://github.com/clojure-emacs/cider#keyboard-shortcuts).

## Contributors

[Gareth Jones](http://blog.gaz-jones.com), 2012 (original author)

Thanks to [Phil Hagelberg](http://technomancy.us/), [Mikael
Sundberg](http://cleancode.se/), and [Jake
McCrary](http://jakemccrary.com/) for suggestions for improvements to
the original blog posts from which this guide was created.
