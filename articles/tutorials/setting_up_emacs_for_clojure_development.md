---
title: "Setting up Emacs for Clojure Development"
layout: article
---

In my opinion the best environment for developing Clojure on any \*nix
platform is Emacs. This guide will explain how to set it up. Im not
really covering how to use Emacs here, see the [Clojure development
cycle with Emacs](/articles/tutorials/clojure_development_cycle_with_emacs.html)
tutorial for usage.

## Installing Emacs ##

### OSX ###

By far the easiest way to get going with Emacs on OSX is to use
[Homebrew](http://mxcl.github.com/homebrew/). Instructions for
installation are
[here](https://github.com/mxcl/homebrew/wiki/installation). You also
need to ensure you have XCode installed for Homebrew to work
correctly.

Once brew is installed, you can install Emacs 24 using:

{% highlight bash %}
$ brew install emacs --HEAD --use-git-head --cocoa
{% endhighlight %}

It is important to install v24 as the emacs-starter-kit v2 which we
will be using requires it. Although it is not yet an official release,
I have been using it for a long time without any issues.

After compiling, Emacs will be living happily somewhere in your
cellar. You can check this:

{% highlight bash %}
$ ls /usr/local/Cellar/emacs/HEAD
{% endhighlight %}

You will find that Homebrew has created a Emacs.app for you which you
can copy to your /Applications folder for easy launching. I tried
symlinking, but it didnt work for me.

### Ubuntu ###

{% highlight bash %}
$ sudo aptitude install emacs24
{% endhighlight %}

or

{% highlight bash %}
$ sudo apt-get install emacs24
{% endhighlight %}

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
[dotfiles project](https://github.com/gar3thjon3s/dotfiles/tree/master/.emacs.d),
which you can look at for inspiration for OSX and Clojure specific
things. You will want to add clojure-mode, and clojure-test-mode to
your list of packages to install:

{% highlight scheme %}
(defvar my-packages '(starter-kit
                      starter-kit-lisp
                      starter-kit-bindings
                      starter-kit-ruby
                      starter-kit-eshell
                      clojure-mode
                      clojure-test-mode)
{% endhighlight %}
                      
Something you really should add to your init.el file is a function to
fix the path issues when running the GUI Emacs ([Thanks to Steve
Purcell on the Clojure mailing list for
this](http://www.mail-archive.com/clojure@googlegroups.com/msg36929.html)):

{% highlight scheme %}
;; fix the PATH variable
(defun set-exec-path-from-shell-PATH ()
  (let ((path-from-shell (shell-command-to-string "$SHELL -i -c 'echo $PATH'")))
    (setenv "PATH" path-from-shell)
    (setq exec-path (split-string path-from-shell path-separator))))

(if window-system (set-exec-path-from-shell-PATH))
{% endhighlight %}

This makes sure that all of the stuff you have on your PATH actually
gets respected in the GUI Emacs, no matter how you start it.

Start emacs:

{% highlight bash %}
$ emacs -nw
{% endhighlight %}

A lot of messages will likely whizz by as it installs and compiles
packages. Unless you have any actual *errors* this is all fine.

To look at the other packages available for installation you can do
(from inside Emacs):

{% highlight scheme %}
M-x package-list-packages
{% endhighlight %}

M-x means meta-x, and meta is mapped to the apple key on the mac
(usually - at least if you have used my init.el). A list of available
packages should load up. To manually install a package, move to the
package with the keyboard and press 'i' for 'install'. After selecting
all the packages you are interested in, press 'x' for 'eXecute' to
install. A better idea is to simply add the package to the var
`my-packages` in your init.el and restart emacs.

## Installing Leiningen ##

Leiningen is the answer to your Clojure build / dependency problems,
even though building a tool on top of Maven and Ant sounds like the
likely *cause* of all your build / dependency problems. It was also
written by Phil Hagelberg. At the time of writing, Leiningen is
undergoing a rewrite and version 2 is currently in preview release. I
have covered 1.x here, and will update the tutorial when 2.x ships as
an actual release.

It lives [here](https://github.com/technomancy/leiningen) and
can be installed with the following commands:

{% highlight bash %}
$ curl https://github.com/technomancy/leiningen/raw/stable/bin/lein -o /usr/local/bin/lein
$ chmod +x /usr/local/bin/lein
{% endhighlight %}

When you run your first lein command it will download and install a
bunch of other stuff it requires so there is no specific installation
command.

### Creating a basic project ###

With Leiningen installed you can now easily create a new project:

{% highlight bash %}
$ lein new testproject
{% endhighlight %}

Looking in this folder you will see a project.clj file that contains
what is essentially metadata for your project. Its a lot like a
gemspec for a rubygem in some respects. If you want to add
dependencies to other libraries, you would do so here. You can also
add dev-dependencies which is occasionally useful. 

Your example project.clj will look a little like this:

{% highlight clojure %}
(defproject testproject "1.0.0-SNAPSHOT"
 :description ""
 :dependencies [[org.clojure/clojure "1.3.0"]])
{% endhighlight %}

And run the following command inside your project folder:

{% highlight bash %}
$ cd testproject
$ lein deps
{% endhighlight %}

This will go ahead and download Clojure and put it in the lib folder
of your project. A common source of confusion among people new to
Clojure is that it is in fact just a library JAR file, not a language
that you _install_ as such. It is technically not necessary to run
`lein deps` as it gets run for you when you run other commands.

Start emacs (if it is not already running):

{% highlight bash %}
$ emacs -nw
{% endhighlight %}

You can now start a REPL and connect to it from Emacs with the
`clojure-jack-in` function:

{% highlight scheme %}
M-x clojure-jack-in
{% endhighlight %}

Give it some time, and you should see a message telling you it has
connected, and a new buffer appear with a REPL prompt. This will turn
out to be awesome.

Similar to the Leiningen 2.x situation, the code behind
`clojure-jack-in` is no longer being actively maintained and is
superceded by a tool called nrepl. Again, when this reaches maturity I
will come back and update the tutorial.

## Summary ##

So you now have a basic working setup of Emacs for Clojure
development, and we very quickly looked at how easy it is to start a
new project and connect a running REPL to it in Emacs. Look at the
tutorial on [Clojure development cycle with
Emacs](/articles/tutorials/clojure_development_cycle_with_emacs.html)
to learn more on how to use Emacs to develop Clojure code.

## Contributors

Gareth Jones <gareth.e.jones@gmail.com>, 2012 (original author)