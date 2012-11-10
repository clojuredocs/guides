---
title: "Parsing XML"
layout: article
---

## Overview

Try as you might, XML is difficult to avoid. This is particularly true
in the Java ecosystem. This guide will show you how to parse XML with
the minimum amount of pain using the excellent tools available in
Clojure.

## What Version of Clojure Does This Guide Cover?

This guide covers Clojure 1.4, and Leiningen 2.x.

## Parsing NZB files

For the purpose of the tutorial I have chosen a simple and fairly well
known XML file format: NZB. An NZB file is used to describe files to
download from NNTP servers. In this tutorial we will take a basic NZB
document and turn it into a Clojure map.

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
"system for filtering trees, and XML trees in particular". 

Make a dir called `dev-resources` at the root of your project, and
create a file named `example.nzb` inside of it. This will be the file
we use to test our code (taken from
[wikipedia](http://en.wikipedia.org/wiki/NZB)). `dev-resources` is by
convention the location to store file resources you use during
development / testing.

Put the following XML in the example.nzb file:

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

_*Note* The eagle eyed among you will notice that I have commented out the
DOCTYPE declaration, as this causes an Exception to be thrown. I will
show you how to get around this towards the end of the tutorial._

Let's write a high level test to illustrate more clearly what we are
trying to do. Open up the `test/nzb/core_test.clj` file and make enter
the following:

```clojure
(ns nzb.core-test
  (:use clojure.test
        nzb.core)
  (:require [clojure.java.io :as io]))

(deftest test-nzb->map
  (let [input (io/resource "example.nzb")]
    (is (= {:meta {:title "Your File!"
                   :tag "Example"}
            :files [{:poster "Joe Bloggs <bloggs@nowhere.example>"
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

This should be fairly self-explanatory, I have directly translated the
XML into Clojure data structures of maps and vectors. If we were to
just use the `clojure.xml` library to parse the NZB file, we get a
tree based representation. For example:

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
familiar with zippers).

### A Simple Example

Zippers allow you to easily traverse a data structure. Let's play with
it in a REPL and start with the root node of our NZB file:

```clojure
(require '[clojure.java.io :as io])
(require '[clojure.xml :as xml])
(require '[clojure.zip :as zip])
(require '[clojure.data.zip.xml :as zip-xml])

(def root (-> "example.nzb" io/resource io/file xml/parse zip/xml-zip))
```

Now we have a zipper for the root element of our document, we can
start traversing it for information. The two main functions we will
use for this are `xml->` and `xml1->`. The former returns a sequence
of items based on the predicates given to it, the latter returning the
first matching item. As an example, let's get the meta data from the NZB
document `root` and create a Clojure map:

```clojure
(into {}
      (for [m (zip-xml/xml-> root :head :meta)]
        [(keyword (zip-xml/attr m :type))
         (zip-xml/text m)]))
;; => {:title "Your File!", :tag "Example"}
```

A couple of things are happening here. First of all we use `xml->` to
return a sequence of `<meta>` tags that live under the `<head>` tag:

```clojure
(zip-xml/xml-> root :head :meta)
```

We use the `for` list comprehension macro to evaluate each item in the
sequence. For each item we find the contents of the `:type` attribute
using the `attr` function:

```clojure
(keyword (zip-xml/attr m :type))
```

This returns us the contents of the attribute as a string, which we
turn into a `keyword` to use as the key in the map. We then use the
`text` function to get the textual contents of the meta tag:

```clojure
(zip-xml/text m)
```

We make a tuple of these values, and pass the resulting sequence to
`into` to build the map.

## Putting It Together

Using only these functions, we can parse the raw XML into the Clojure
data structure from our unit test. If you like, open
`./src/nzb/core.clj`, and make the changes as you read along.

First let's define our `nzb->map` function from the test, and pull in
the code we have already written for parsing the metadata of the NZB:

```clojure
(ns nzb.core
  (:require [clojure.xml :as xml]
            [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]))

(defn meta->map
  [root]
  (into {}
        (for [m (zip-xml/xml-> root :head :meta)]
          [(keyword (zip-xml/attr m :type))
           (zip-xml/text m)])))

(defn file->map
  [file]
  ;; TODO
)

(defn nzb->map
  [input]
  (let [root (-> input
                 io/input-stream
                 xml/parse
                 zip/xml-zip)]
    {:meta  (meta->map root)
     :files (mapv file->map (zip-xml/xml-> root :file))}))
```

The only new thing here is the use of `io/input-stream` to allow us to
use anything as `input` that the `io/input-stream` supports. These are
currently `OutputStream`, `File`, `URI`, `URL`, `Socket`, `byte
array`, and `String` arguments. See the
[clojure.java.io](http://clojure.github.com/clojure/clojure.java.io-api.html)
docs for details.

Now let's fill in the `file->map` function:

```clojure
(defn segment->map
  [seg]
  {:bytes  (Long/valueOf (zip-xml/attr seg :bytes))
   :number (Integer/valueOf (zip-xml/attr seg :number))
   :id     (zip-xml/xml1-> seg zip-xml/text)})

(defn file->map
  [file]
  {:poster   (zip-xml/attr file :poster)
   :date     (Long/valueOf (zip-xml/attr file :date))
   :subject  (zip-xml/attr file :subject)
   :groups   (vec (zip-xml/xml-> file :groups :group zip-xml/text))
   :segments (mapv segment->map
                   (zip-xml/xml-> file :segments :segment))})
```

Again, nothing new. We simply pick out the pieces of the document we
wish to process using a combination of the `xml1->`, `xml->`, `attr`,
and `text` functions. Run the test, and it should pass.

### Prevent Parsing the DTD

Interestingly, if we uncomment the DTD declaration in the
`example.nzb` file, our code now explodes with an Exception:

```
org.xml.sax.SAXParseException: The markup declarations contained or pointed to by the document type declaration must be well-formed
```

We can fix this by swapping out the `SAXParserFactory` and setting a
feature to not validate the DTD. Here's how:

Update the `ns` declaration to include some required classes:

```clojure
(ns nzb.core
  (:require [clojure.xml :as xml]
            [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml])
  (:import (javax.xml.parsers SAXParser SAXParserFactory)))
```

Define a function to switch out the SAXParserFactory:

```clojure
(defn startparse-sax
  "Don't validate the DTDs, they are usually messed up."
  [s ch]
  (let [factory (SAXParserFactory/newInstance)]
    (.setFeature factory "http://apache.org/xml/features/nonvalidating/load-external-dtd" false)
    (let [^SAXParser parser (.newSAXParser factory)]
      (.parse parser s ch))))
```

Update our nzb->map definition to use it:

```clojure
(defn nzb->map
  [input]
  (let [root (-> input
                 io/input-stream
                 (xml/parse startparse-sax)
                 zip/xml-zip)]
    {:meta  (meta->map root)
     :files (mapv file->map (zip-xml/xml-> root :file))}))
```

Yay, our test passes again.

## Query Predicates

There are a few other useful functions in the `clojure.data.zip.xml`
ns we haven't yet looked at, namely: `text=`, `attr=`, and `tag=`.
These functions allow you to construct query predicates to run against
a given node. As an example, let's pull out the first file segment
from the `example.nzb` file using the `attr=` function:

```clojure
(zip-xml/xml1-> root
                :file
                :segments
                :segment
                (zip-xml/attr= :number "1"))
                zip-xml/text)
"123456789abcdef@news.newzbin.com"
```

From the root node of the document we reach down into `:file`,
`:segments`, and `:segment` in turn, then use the `attr=` query
predicate to match a `:segment` with a value of `"1"`.

Interestingly enough, the other two query predicates have shortcuts
for their use. You have already been using the `tag=` query predicate
every time you use a keyword to locate a tag. To use the `text=`
predicate easily, just use a string. For example, to retrieve the
second `:segment` based on its content of
`987654321fedbca@news.newzbin.com`:

```clojure
(zip-xml/xml1-> root
                :file
                :segments
                :segment
                "987654321fedbca@news.newzbin.com")
;; ... the resulting node
```

Finally, you can combine these query predicates to match multiple
things on a given node by using a vector:

```clojure
(zip-xml/xml1-> root
                :file
                :segments
                :segment
                [(zip-xml/attr= :number "1")
                 (zip-xml/attr= :bytes "102394")]
                zip-xml/text)
"123456789abcdef@news.newzbin.com"
```

Here we are matching on both the `:number` attribute being `"1"`, and
the `:bytes` attribute being `"102394"`. Obviously, you can use
strings here to match against content too.

## Creating New Predicates

OK, now let's suppose we want to use some kind of numerical comparison
in our XML (like we might do with XPath). As it stands, we have no way
to do that with the built-in functions but we can easily define our
own.

Let's start with a general function for comparing attribute values:

```clojure
(defn attr-fn
  [attrname f test-val & [conv-fn]]
  (fn [loc]
    (let [conv-fn (or conv-fn identity)
          val (conv-fn (zip-xml/attr loc attrname))]
      (f val test-val))))
```

This function takes an attribute name (`attrname`), a function for
making a comparison (`f`), a value to test agains (`test-val`) and
optionally a conversion function. Imagine our `example.nzb` file had
100 segments, and we only wanted to get segments over 75. We could now
achieve this using our general function:

```clojure
(zip-xml/xml-> root
               :file
               :segments
               :segment
               (attr-fn :number > 75 #(Long/valueOf %))
               zip-xml/text)
```

Let's provide a helper for this to make the syntax clearer:

```clojure
(defn attr>
  [attrname val]
  (attr-fn attrname > val #(Long/valueOf %)))

(zip-xml/xml-> doc
               :file
               :segments
               :segment
               (attr> :number 75)
               zip-xml/text)
```

We could build a whole suite of helper functions for examining XML
nodes, if we are unlucky enough to be required to do so :)

## Conclusion

I hope these simple examples have given you an idea of the ease with
which you can process XML using Clojure, and how simple it is to
extend the tools already provded in interesting directions.

## Contributors

[Gareth Jones](http://blog.gaz-jones.com), 2012 (original author)
