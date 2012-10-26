---
title: "Parsing XML"
layout: article
---

## Overview

Try as you might, XML is difficult to avoid. This is particularly true
in the Java ecosystem. This guide will show you how to parse XML from
a variety of different sources, with the minimum amount of pain using
the excellent tools built in to Clojure.

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4. There are no external dependencies
used.

## Parsing NZB files

For the purpose of the tutorial I have chosen a simple and fairly well
known XML file format: NZB. An NZB file is used to describe segments
of files to download from NNTP servers. In this tutorial we will take
a basic NZB document from either a string, a file (local or remote)
and turn it into a Clojure map. 

Let us start by creating a new project (for details on using
Leiningen, see [this guide](/articles/tutorials/leiningen.html):

```bash
$ lein new nzb
```

Now edit `project.clj` to contain the following:

```clojure
(defproject nzb "0.1.0-SNAPSHOT"
  :description ""
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.zip "0.1.1"]])
```

We are including a dependency on
[clojure.data.zip](http://github.com/clojure/data.zip), which is a
"system for filtering trees, and XML trees in particular". We also

Make a dir called `dev-resources` at the root of your project, and
create a file named `example.nzb` inside of it. This will be the file
we use to test our code (taken from
[wikipedia](http://en.wikipedia.org/wiki/NZB)). Paste the following
XML:

```xml
<?xml version="1.0" encoding="iso-8859-1" ?>
<!-- <!DOCTYPE nzb PUBLIC "-//newzBin//DTD NZB 1.1//EN" "http://www.newzbin.com/DTD/nzb/nzb-1.1.dtd"> -->
<nzb xmlns="http://www.newzbin.com/DTD/2003/nzb">
 <head>
   <meta type="title">Your File!</meta>
   <meta type="tag">Example</meta>
 </head>
 <file poster="Joe Bloggs &lt;bloggs@nowhere.example&gt;" date="1071674882" subject="Here's your file!  abc-mr2a.r01 (1/2)">
   <groups>
     <group>alt.binaries.newzbin</group>
     <group>alt.binaries.mojo</group>
   </groups>
   <segments>
     <segment bytes="102394" number="1">123456789abcdef@news.newzbin.com</segment>
     <segment bytes="4501" number="2">987654321fedbca@news.newzbin.com</segment>
   </segments>
 </file>
</nzb>
```

_The eagle eyed among you will notice that I have commented out the
DOCTYPE declaration, as this causes an Exception to be thrown. I will
show you how to get around this at the end of the tutorial._

Let's write a high level test to illustrate what we are trying to do
clearly. Open up the `test/nzb/core_test.clj` file and make it look
like the following:

```clojure
(ns nzb.core-test
  (:use clojure.test
        nzb.core)
  (:require [clojure.java.io :as io]))

(deftest test-nzb->map
  (let [input (io/resource "example.nzb")]
    (is (= {:meta {:title "Your File!"
                   :tag "Example"}
            :files [{:poster "Joe Bloggs &lt;bloggs@nowhere.example&gt;"
                     :date 1071674882
                     :subject "Here's your file!  abc-mr2a.r01 (1/2)"
                     :groups ["alt.binaries.newzbin"
                              "alt.binaries.mojo"]
                     :segments [{:bytes 102394
                                 :number 1
                                 :id "123456789abcdef@news.newzbin.com"}
                                {:bytes 4501
                                 :number 2
                                 :id "987654321fedbca@news.newzbin.com"}]}]}
           (nzb->map input)))))
```

This should be fairly self-explanatory, I have pretty much directly
translated the XML into Clojure data structures. Remember it is the
process of doing so that is of interest here, not the end result.

If we just use the `clojure.xml` library to parse an XML file, we get
a tree based representation. For example:

```clojure
(-> "example.nzb" io/resource io/file xml/parse)
{:tag :nzb,
 :attrs {:xmlns "http://www.newzbin.com/DTD/2003/nzb"},
 :content
 [{:tag :head,
   :attrs nil,
   :content
   [{:tag :meta, :attrs {:type "title"}, :content ["Your File!"]}
    {:tag :meta, :attrs {:type "tag"}, :content ["Example"]}]}
  {:tag :file,
   :attrs
   {:poster "Joe Bloggs <bloggs@nowhere.example>",
    :date "1071674882",
    :subject "Here's your file!  abc-mr2a.r01 (1/2)"},
   :content
   [{:tag :groups,
     :attrs nil,
     :content
     [{:tag :group, :attrs nil, :content ["alt.binaries.newzbin"]}
      {:tag :group, :attrs nil, :content ["alt.binaries.mojo"]}]}
    {:tag :segments,
     :attrs nil,
     :content
     [{:tag :segment,
       :attrs {:bytes "102394", :number "1"},
       :content ["123456789abcdef@news.newzbin.com"]}
      {:tag :segment,
       :attrs {:bytes "4501", :number "2"},
       :content ["987654321fedbca@news.newzbin.com"]}]}]}]}
```

That's great, and can sometimes be enough. But I would rather work
with the representation I have in the test. To do that, we need a way
of traversing this tree and picking out the pieces of information we
require. The `clojure.zip` and `clojure.data.zip` libraries are
perfect for this. The
[documentation](http://clojure.github.com/data.zip/) for the
`data.zip` library on github is nice, but it initially left me a
little confused as to how to go about using the library (not being
familiar with zippers in general). Hopefully a few examples will make
things clearer.