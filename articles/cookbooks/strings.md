---
title: "Strings"
layout: article
---

This cookbook covers working with strings in Clojure using built-in
functions, standard and contrib libraries, and parts of the JDK via
interoperability.

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons
Attribution 3.0 Unported License</a> (including images &
stylesheets). The source is available [on
Github](https://github.com/clojuredocs/guides).


## Overview

Strings are [plain Java
strings](http://docs.oracle.com/javase/7/docs/api/java/lang/String.html).
This means that you have access to the wealth of tools which operate
on Java strings. Since Java strings are immutable, they're convenient
to use in Clojure.

One downside of Java strings is that you can't add Clojure metadata to
them.

Clojure supports the following convenient notations:

    "foo"    String
    #"\d"    Regex (in this case, one which matches a single digit)
    \f       Character (in this case, the letter 'f')



### Caveat

Human brains and electronic computers aren't always in sync. Java
strings are sequences of [UTF-16
characters](http://docs.oracle.com/javase/7/docs/api/java/lang/Character.html#unicode). But
UTF-16 characters don't always map nicely to user-perceived
characters.

For example, sometimes a single Unicode "code point" may require 2
UTF-16 characters to encode it.

And even then, a full Unicode code point sometimes won't correspond to
a user-perceived character ("grapheme cluster"). For instance, take
Korean's Hangul Jamo, where user-perceived characters are composed
from two or three Unicode code points.


## Preliminaries

Some examples use
[clojure.string](http://clojure.github.io/clojure/clojure.string-api.html). We'll
assume your `ns` macro contains:

``` clojure
(:require [clojure.string :as str]
          [clojure.edn :as edn])
```

or else in the repl you've loaded it:

``` clojure
(require '[clojure.string :as str])
(require '[clojure.edn :as edn])
```


## Recipes

``` clojure
;; Counting
(count "0123")      ;=> 4
(empty? "0123")     ;=> false
(empty? "")         ;=> true
(str/blank? "    ") ;=> true

;; Concatenate
(str "foo" "bar")            ;=> "foobar" 
(str/join ["0" "1" "2"])     ;=> "012"
(str/join "." ["0" "1" "2"]) ;=> "0.1.2"

;; Substring
(subs "0123" 1)       ;=> "123"
(subs "0123" 1 3)     ;=> "12"
(str/trim "  foo  ")  ;=> "foo"
(str/triml "  foo  ") ;=> "foo  "
(str/trimr "  foo  ") ;=> "  foo"

;; Split into sequence
(str/split-lines "foo
bar")                             ;=> ["foo" "bar"]
(str/split "foo/bar/quux" #"/")   ;=> ["foo" "bar" "quux"]
(str/split "foo/bar/quux" #"/" 2) ;=> ["foo" "bar/quux"]

;; Case
(str/lower-case "fOo") ;=> "foo"
(str/upper-case "fOo") ;=> "FOO"
(str/capitalize "fOo") ;=> "Foo"

;; Escaping
(str/escape "foo|bar|quux" {\| "||"}) ;=> "foo||bar||quux"

;; Parsing keywords
(keyword "foo")    ;=> :foo

;; Parsing numbers
(bigint "20000000000000000000000000000") ;=> 20000000000000000000000000000N
(bigdec "20000000000000000000.00000000") ;=> 20000000000000000000.00000000M
(Integer/parseInt "2")                   ;=> 2
(Float/parseFloat "2")                   ;=> 2.0
```

### Format strings

http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html

``` clojure
(format "%1$tY-%1$tm-%1$td" #inst"2000-01-02T00:00:00") ;=> "2000-01-02"

(format "New year: %2$tY. Old year: %1$tY"
        #inst"2000-01-02T00:00:00"
        #inst"2010-01-02T00:00:00")
;=> "New year: 2010. Old year: 2000"



```

### Parsing Clojure data

``` clojure
(edn/read-string "0xffff") ;=> 65535

;; If you really want to load Clojure data which edn doesn't support,
;; to do it safely, ensure *read-eval* is false.
(binding [*read-eval* false]
  (read-string "#\"[abc]\""))
;=> #"[abc]"
```


### Regexes

[Regex
reference.](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html)

Regex groups are useful, when we want to match more than one
substring. (Or refer to matches later.)

In the regex `#"(group-1) (group-2)"`, the 0th group is the whole
match. The 1st group is what's in the first parenthesis, and so
on. When replacing text, you could refer to the 0th group as `$0`, the
1st group as `$1`, etc.

You can nest groups. (The left-most parenthesis represents the 1st
group, the second-left-most parenthesis is the 2nd group, etc. As
before, the whole match is the 0th group.)

#### Matching

```clojure
;; Simple matching
(re-find #"\d+" "foo 123 bar") ;=> "123"

;; Failed matching
(re-find #"\d+" "foobar") ;=> nil

;; Return first matching groups.
(re-matches #"(\w+)\s([.0-9]+)%"
            "Mozambique 19.8%")
;=> ["Mozambique 19.8%" "Mozambique" "19.8"]

;; Return seq of all matching groups.
(re-seq #"(\w+)\s([.0-9]+)%"
        "Bolivia 12.3%,Mozambique 19.8%")
;=> (["Bolivia 12.3%"    "Bolivia"    "12.3"]
;    ["Mozambique 19.8%" "Mozambique" "19.8"])
```

#### Replacing

```clojure
;; Use $0, $1, etc to refer to matched groups.
(str/replace "Bolivia 12.3%,Mozambique 19.8%"
             #"(\w+)\s([.0-9]+)%"
             "$2 ($1)")
;=> "12.3 (Bolivia),19.8 (Mozambique)"


;; A function can generate replacements.
(str/replace "Bolivia 12.3%,Mozambique 19.8%"
             #"(\w+)\s([.0-9]+)%,?"
             (fn [[_ country percent]]
               (let [points (-> percent Float/parseFloat (* 100) Math/round)]
                 (str country " has " points " points of growth.\n"))))
;=> "Bolivia has 1230 points of growth.
;Mozambique has 1980 points of growth.
;"
```

### Streams

``` clojure
;; Redirect standard output (*out*) to string
(let [shrimp-varieties ["shrimp-kabobs" "shrimp creole" "shrimp gumbo"]]
  (with-out-str
    (print "We have ")
    (doseq [name (str/join ", " shrimp-varieties)]
      (print name))
    (print "...")))
;=> "We have shrimp-kabobs, shrimp creole, shrimp gumbo..."
```


## TBD:

* string buffers
* explain javadoc
* format http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html
* cl-format
* can turn it into a seq (useful?)
* surrogate
* streams and strings (println, redirecting stdout...)
* encodings, bytes
* counting unicode code points