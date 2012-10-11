---
title: "Clojure Development Cycle with Emacs"
layout: article
---

This tutorial aims to get you from crawling to walking with Emacs for
Clojure development. No knowledge of Emacs is assumed, other than a
working installation. For help on this see the tutorial on [setting up
Emacs for Clojure
development](/articles/tutorials/setting_up_emacs_for_clojure_development.html).

If you followed the tutorial, you hopefully have a working Emacs
installation based on the emacs-starter-kit. There are a number of
awesome things in the emacs-starter-kit that you otherwise wouldn't
get in straight up emacs so bear that in mind as I list them.

Start up Emacs:

{% highlight bash %}
$ emacs -nw
{% endhighlight %}

and follow along!

### Basics ###

The first thing you should do without question, is to go through the
built-in Emacs tutorial. To do this press `C-h t` or hold down Control
and press `h` and then press `t`.

With that in mind, these are the basic keystrokes you're going to be
using most often:

{% highlight text %}
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
{% endhighlight %}

I should also mention the help commands:

{% highlight text %}
C-h t     Tutorial (goes over the basics)
C-h b     Describe all current key bindings
C-h m     Describe the current mode
C-h a     Apropos - search the help for a term
C-h k     Describe key
{% endhighlight %}

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

Note: _This tutorial uses Leiningen 1.x and swank-clojure, it will be
updated when Leiningen 2.x and nrepl become mature and stable._

Create a new project:

{% highlight bash %}
$ lein new command-line-args
$ cd command-line-args
{% endhighlight %}

Start up Emacs and `M-x cd` to the command-line-args folder. Open and
edit your project.clj to look like this:

{% highlight clojure %}
(defproject command-line-args "1.0.0-SNAPSHOT"
 :description "FIXME: write"    
 :dependencies [[org.clojure/clojure "1.3.0"]])
{% endhighlight %}

A full reference for what can go in this file lives in the Leiningen
project
[here](https://github.com/technomancy/leiningen/blob/1.x/sample.project.clj)
Now to get those depenedencies in your lib folder:

{% highlight bash %}
$ lein deps
{% endhighlight %}

At this point we are ready to start coding. One thing to note is the
folder structure of your project is set up for you in the style of a
maven project:

{% highlight text %}
- README
- project.clj
+ lib
  - clojure-1.3.0.jar
+ src
  + command_line_args
    - core.clj
+ test
  + command_line_args
    + test
      - core.clj
{% endhighlight %}

Should be fairly self-explanatory, namespaces in clojure are
represented as folders in the filesystem (like packages in java) and
the test structure mirrors that of the src structure. I'm assuming you
have clojure-mode and clojure-test-mode installed as elpa packages. If
not `M-x package-list-packages` and install them now (or add them to
your init.el as described in [setting up Emacs for Clojure
development](/articles/tutorials/setting_up_emacs_for_clojure_development.html)).

Lets start up a swank server and connect to it from Emacs using clojure-mode:

{% highlight text %}
M-x clojure-jack-in
{% endhighlight %}

This should open up a new window looking at our \*slime-repl nil\*
buffer.

First thing to do is add a simple test (in fact the only test we will
be adding because by default, we get it right first time). Open the
`core.clj` file inside of the test folder. Replace the test that is
there with the following:

{% highlight clojure %}
(deftest pairs-of-values
   (let [args '("--server" "localhost" 
                "--port" "8080" 
                "--environment" "production")]
      (is (= {:server "localhost"
           :port "8080"
           :environment "production"}
           (parse-args args)))))
{% endhighlight %}

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

{% highlight clojure %}
(defn parse-args [args]
  {})
{% endhighlight %}

Save the file (`C-x C-s`), switch back to the test buffer (`C-x b
ENTER`) and run the test again (`C-c ,`). This time you should get a
message in the mini-buffer (the small line at the bottom of you
screen) telling you one test has failed AND you should have a red bar
across the `is` assertion. To check what the problem was, we can move
our cursor over the red bar and press `C-c '`. This shows the problem
with the assertion in the mini-buffer:

{% highlight clojure %}
(not (= {:server "localhost", 
      :port "8080", 
      :environment "production"} 
      {}))
{% endhighlight %}

Awesome! So our map was empty as expected. Lets fix that:

{% highlight clojure %}
(defn parse-args [args]
  (apply hash-map args))
{% endhighlight %}

Running our tests again we now get another error:

{% highlight clojure %}
(not (= {:server "localhost", 
         :port "8080", 
         :environment "production"} 
        {"--port" "8080", 
         "--server" "localhost", 
         "--environment" "production"}))
{% endhighlight %}

Whoops our keys are just strings with the dashes still in place. We
need to strip those off and turn them into keywords:

{% highlight clojure %}
(defn parse-args [args]
  (into {} (map (fn [[k v]] [(keyword (.replace k "--" "")) v])
                (partition 2 args))))
{% endhighlight %}

And re-running the tests in the test buffer we are all happy. If we
had multiple test files we can just do:

{% highlight bash %}
$ lein test
{% endhighlight %}

from the command line and it will run everything in the project.

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
use the arrow keys to move around windows or use `C-x o` to switch to
other). Change the namespace you are in:

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

{% highlight clojure %}
(defn keywordize [kvp]
  (let [[k v] kvp]
    [(keyword (.replace k "--" "")) v]))

(defn parse-args [args]
  (into {} (map keywordize (partition 2 args))))
{% endhighlight %}

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

## Creating a library jar ##

Now you have a `parse-args` function, how could you go about actually
using it in an application? Leiningen comes with a very handy `jar`
command.

    lein jar

This will create a jar file from your project using the version
information inside of your `project.clj` file that you can then re-use
in other projects. If you are creating a library that you want to be
publicly available, you should create a [clojars](http://clojars.org/)
account and publish it there. Again, Leiningen provides a command to
make this trivial however you need to specifically reference it in
your `dev-dependencies`:

{% highlight clojure %}
:dev-dependencies [[lein-clojars "0.7.0"]]
{% endhighlight %}

This gives you a `push` command. See the documentation
[here](https://github.com/technomancy/lein-clojars) for more
details. Having pushed to clojars, it is simply a matter of adding the
dependency to your new project, for example:

{% highlight clojure %}
:dependencies [[command-line-args "1.0.0"]]
{% endhighlight %}

And running:

{% highlight bash %}
$ lein deps
{% endhighlight %}

to fetch it.

If this is a private project, you will need to publish the jar to your
local maven repository (local as in the maven repo your devs
use). This can be easily achieved by `lein install` on your build
server on a RC build for example.

## Summary ##

There are a whole lot more things you can do with Leiningen, and [the
official
tutorial](https://github.com/technomancy/leiningen/blob/1.x/TUTORIAL.md)
goes through things in more detail. Hopefully I have given a taste of
why it is such a great tool.

## Appendix ##

Here are all the commands used in the post that are not documented at
the beginning (thanks to Mikael Sundberg for this suggestion):

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

Gareth Jones <gareth.e.jones@gmail.com>, 2012 (original author)

Thanks to [Phil Hagelberg](http://technomancy.us/), [Mikael
Sundberg](http://cleancode.se/), and [Jake
McCrary](http://jakemccrary.com/) for suggestions for improvements to
this tutorial.
