# Files and Directories

Note that for the examples below, "io" is an alias for
clojure.java.io. That is, it's assumed your `ns` macro contains:

```clojure
(:require [clojure.java.io :as io])
```

or else in the repl you've loaded it:

    (require '[clojure.java.io :as io])


**Read a file into one long string:**

```clojure
(slurp "foo.txt")
```

**Read a file one line at a time:**

Suppose you'd like to call `my-func` on every line in a file,
and return the resulting sequence:

```clojure
(with-open [rdr (io/reader "foo.txt")]
  (map my-func (line-seq rdr)))
```


**Write a long string out to a new file:**

```clojure
(spit "foo.txt"
      "A long
multi-line string.
Bye.")
```

Overwrites the file if it already exists.


**Write a file one line at a time:**

Suppose you'd like to write out every item in a vector, one item per
line:

```clojure
(with-open [wrtr (io/writer "foo.txt")]
  (doseq [i my-vec]
    (.write wrtr (str i "\n"))))
```


**Check if a file exists:**

```clojure
(.exists (io/file "filename.txt"))
```

**Is it a directory?**

```clojure
(.isDirectory (io/file "path/to/something"))
```

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

**Get a list of the filenames and dirs --- as `File` objects --- in a
given directory:**

```clojure
(.listFiles (io/file "path/to/some-dir"))
```

Same, but just the names (strings), not File objects:

```clojure
(.list (io/file "path/to/some-dir"))
```

The results of those calls are seqable.
