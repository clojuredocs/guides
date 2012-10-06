---
title: "Working with Files and Directories in Clojure"
layout: article
---

## What This Cookbook Covers

This cookbook covers working with files and directories from Clojure, using functions in the `clojure.java.io` namespace
as well as parts of the JDK via interoperability.


## Overview

TBD

Note that for the examples below, "io" is an alias for
clojure.java.io. That is, it's assumed your `ns` macro contains:

{% highlight clojure %}
(:require [clojure.java.io :as io])
{% endhighlight %}

or else in the repl you've loaded it:

{% highlight clojure %}
(require '[clojure.java.io :as io])
{% endhighlight %}


**Read a file into one long string:**

{% highlight clojure %}
(def a-long-string (slurp "foo.txt"))
{% endhighlight %}

Note, you can pass urls to `slurp` as well. See also [slurp at
Clojuredocs](http://clojuredocs.org/clojure_core/clojure.core/slurp).


**Read a file one line at a time:**

Suppose you'd like to call `my-func` on every line in a file,
and return the resulting sequence:

{% highlight clojure %}
(with-open [rdr (io/reader "foo.txt")]
  (doall (map my-func (line-seq rdr))))
{% endhighlight %}

The `doall` is needed because the `map` call is lazy. The lines that
`line-seq` gives you have no trailing newlines (and empty lines in the
file will yield empty strings ("")).


**Write a long string out to a new file:**

{% highlight clojure %}
(spit "foo.txt"
      "A long
multi-line string.
Bye.")
{% endhighlight %}

Overwrites the file if it already exists. To append, use

{% highlight clojure %}
(spit "foo.txt" "file content" :append true)
{% endhighlight %}


**Write a file one line at a time:**

Suppose you'd like to write out every item in a vector, one item per
line:

{% highlight clojure %}
(with-open [wrtr (io/writer "foo.txt")]
  (doseq [i my-vec]
    (.write wrtr (str i "\n"))))
{% endhighlight %}


**Check if a file exists:**

{% highlight clojure %}
(.exists (io/file "filename.txt"))
{% endhighlight %}

**Is it a directory?**

{% highlight clojure %}
(.isDirectory (io/file "path/to/something"))
{% endhighlight %}

An io/file is a java.io.File object (a file or a directory). You can
call a number of functions on it, including:

    exists        Does the file exist?
    isDirectory   Is the File object a directory?
    getName       The basename of the file.
    getParent     The dirname of the file.
    getPath       Filename with directory.
    mkdir         Create this directory on disk.

To read about more available methods, see [the java.io.File
docs](http://docs.oracle.com/javase/7/docs/api/java/io/File.html).

**Get a list of the files and dirs --- as `File` objects --- in a
given directory:**

{% highlight clojure %}
(.listFiles (io/file "path/to/some-dir"))
{% endhighlight %}

Same, but just the *names* (strings), not File objects:

{% highlight clojure %}
(.list (io/file "path/to/some-dir"))
{% endhighlight %}

The results of those calls are seqable.
