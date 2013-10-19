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

* Strings are [plain Java
strings](http://docs.oracle.com/javase/7/docs/api/java/lang/String.html).
You can use anything which operates on them.
* Java strings are immutable, so they're convenient to use in Clojure.
* You can't add metadata to Java strings.
* Clojure supports some convenient notations:

```
    "foo"    java.lang.String
    #"\d"    java.util.regex.Pattern (in this case, one which matches a single digit)
    \f       java.lang.Character (in this case, the letter 'f')
```

* **Caveat:** Human brains and electronic computers are rather different
devices. So Java strings (sequences of [UTF-16
characters](http://docs.oracle.com/javase/7/docs/api/java/lang/Character.html#unicode))
don't always map nicely to user-perceived characters. For example, a
single Unicode "code point" doesn't necessarily equal a user-perceived
character. (Like Korean Hangul Jamo, where user-perceived characters
are composed from two or three Unicode code points.) Also, a Unicode
code point may sometimes require 2 UTF-16 characters to encode it.


## Preliminaries

Some examples use
[clojure.string](http://clojure.github.io/clojure/clojure.string-api.html),
[clojure.edn](https://github.com/edn-format/edn) and
[clojure.pprint](http://clojure.github.io/clojure/clojure.pprint-api.html). We'll
assume your `ns` macro contains:

``` clojure
(:require [clojure.string :as str]
          [clojure.edn :as edn]
          [clojure.pprint :as pp])
```

or else in the repl you've loaded it:

``` clojure
(require '[clojure.string :as str])
(require '[clojure.edn :as edn])
(require '[clojure.pprint :as pp])
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

;; Matching using plain Java methods.
;;
;; You might prefer regexes for these. For instance, failure returns
;; -1, which you have to test for. And characters like \o are
;; instances of java.lang.Character, which you may have to convert to
;; int or String.
(.indexOf "foo" "oo")         ;=> 1
(.indexOf "foo" "x")          ;=> -1
(.lastIndexOf "foo" (int \o)) ;=> 2

;; Substring
(subs "0123" 1)       ;=> "123"
(subs "0123" 1 3)     ;=> "12"
(str/trim "  foo  ")  ;=> "foo"
(str/triml "  foo  ") ;=> "foo  "
(str/trimr "  foo  ") ;=> "  foo"

;; Multiple substrings
(seq "foo")                       ;=> (\f \o \o)
(str/split "foo/bar/quux" #"/")   ;=> ["foo" "bar" "quux"]
(str/split "foo/bar/quux" #"/" 2) ;=> ["foo" "bar/quux"]
(str/split-lines "foo
bar")                             ;=> ["foo" "bar"]

;; Case
(str/lower-case "fOo") ;=> "foo"
(str/upper-case "fOo") ;=> "FOO"
(str/capitalize "fOo") ;=> "Foo"

;; Escaping
(str/escape "foo|bar|quux" {\| "||"}) ;=> "foo||bar||quux"

;; Get byte array of given encoding
(.getBytes "foo" "UTF-8") ;=> #<byte[] [B@3c3660>

;; Parsing keywords
(keyword "foo")    ;=> :foo

;; Parsing numbers
(bigint "20000000000000000000000000000") ;=> 20000000000000000000000000000N
(bigdec "20000000000000000000.00000000") ;=> 20000000000000000000.00000000M
(Integer/parseInt "2")                   ;=> 2
(Float/parseFloat "2")                   ;=> 2.0

;; Parsing edn, a subset of Clojure forms.
(edn/read-string "0xffff") ;=> 65535

;; The sledgehammer approach to reading Clojure forms.
;;
;; SECURITY WARNING: Ensure *read-eval* is false when dealing with
;; strings you don't 100% trust. Even though *read-eval* is false by
;; default since Clojure 1.5, be paranoid and set it to false right
;; before you use it, because anything could've re-bound it to true.
(binding [*read-eval* false]
  (read-string "#\"[abc]\""))
;=> #"[abc]"

;; with-out-str redirects standard output (*out*) to a string,
;; providing an easy way to build strings.
(let [shrimp-varieties ["shrimp-kabobs" "shrimp creole" "shrimp gumbo"]]
  (with-out-str
    (print "We have ")
    (doseq [name (str/join ", " shrimp-varieties)]
      (print name))
    (print "...")))
;=> "We have shrimp-kabobs, shrimp creole, shrimp gumbo..."
```


### Regexes

[Regex
reference.](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html)
Regex groups are useful, when we want to match more than one
substring. (Or refer to matches later.)

**Groups:** In the regex `#"(group-1) (group-2)"`, the 0th group is
the whole match. The 1st group is started by the left-most `(`, the
2nd group is started by the second-left-most `(`, etc. You can even
nest groups. You can refer to groups later using `$0`, `$1`, etc.

#### Matching

```clojure
;; Simple matching
(re-find #"\d+" "foo 123 bar") ;=> "123"

;; Failed matching
(re-find #"\d+" "foobar") ;=> nil

;; Return first matching groups.
(re-matches #"(@\w+)\s([.0-9]+)%"
            "@shanley 19.8%")
;=>["@shanley 19.8%" "@shanley" "19.8"]

;; Return seq of all matching groups.
(re-seq #"(@\w+)\s([.0-9]+)%"
        "@davidgraeber 12.3%,@shanley 19.8%")
;=> (["@davidgraeber 12.3%" "@davidgraeber" "12.3"]
;    ["@shanley 19.8%" "@shanley" "19.8"])
```

#### Replacing

```clojure
;; Use $0, $1, etc to refer to matched groups.
(str/replace "@davidgraeber 12.3%,@shanley 19.8%"
             #"(@\w+)\s([.0-9]+)%"
             "$2 ($1)")
;=> "12.3 (@davidgraeber),19.8 (@shanley)"

;; A function can generate replacements.
(println
  (str/replace "@davidgraeber 12.3%,@shanley 19.8%"
               #"(@\w+)\s([.0-9]+)%,?"
               (fn [[_ person percent]]
                   (let [points (-> percent Float/parseFloat (* 100) Math/round)]
                     (str person "'s followers grew " points " points.\n")))))
;print=> @davidgraeber's followers grew 1230 points.
;print=> @shanley's followers grew 1980 points.
;print=>
```


### Context-free grammars

[Instaparse](https://github.com/Engelberg/instaparse) makes many
things easier to parse. Below is a quick implementation of [JSON's
grammar](http://www.json.org/) which isn't seriously tested. (Use
[data.json](https://github.com/clojure/data.json) instead.)

``` clojure
;; Your project.clj should contain this (you may need to restart your JVM):
;;   :dependencies [[instaparse "1.2.4"]]
;;
;;  We'll assume your ns macro contains:
;;   (:require [instaparse.core :as insta])
;; or else in the repl you've loaded it:
;;   (require '[instaparse.core :as insta])
;;

(def barely-tested-json-parser
  (insta/parser
   "Object = <'{'> <w*> (members <w*>)* <'}'>
    <members> = pair (<w*> <','> <w*> members)*
    pair = string <w*> <':'> <w*> value
    <value> = string | number | Object | array | 'true' | 'false' | 'null'
    array = <'['> elements* <']'>
    <elements> = value <w*> (<','> <w*> elements)*
    number = int frac? exp?
    <int> = '-'? digits
    <frac> = '.' digits
    exp = e digits
    <e> = <('e' | 'E')> (<'+'> | '-')?
    <digits> = #'[0-9]+'
    (* First sketched state machine, then wrote regex syntax,
       then added all the escape-backslashes. *)
    string = <'\\\"'> #'([^\"\\\\]|\\\\.)*' <'\\\"'>
    <w> = #'\\s+'"))

(barely-tested-json-parser "{\"foo\": {\"bar\": 99.9e-9, \"quux\": [1, 2, -3]}}")
;=> [:Object
;     [:pair [:string "foo"]
;            [:Object
;             [:pair [:string "bar"]
;                    [:number "99" "." "9" [:exp "-" "9"]]]
;             [:pair [:string "quux"]
;                    [:array [:number "1"] [:number "2"] [:number "-" "3"]]]]]]
```


### Format strings

[Reference.](http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html)
A well-known templating language to print text.

``` clojure
(format "%s enjoyed %s%%." "Mozambique" 19.8) ;=> "Mozambique enjoyed 19.8%."

;; The 1$ prefix allows you to keep referring to the first arg.
(format "%1$tY-%1$tm-%1$td" #inst"2000-01-02T00:00:00") ;=> "2000-01-02"

(format "New year: %2$tY. Old year: %1$tY"
        #inst"2000-01-02T00:00:00"
        #inst"2010-01-02T00:00:00")
;=> "New year: 2010. Old year: 2000"
```


### CL-Format

`cl-format` is a port of Common Lisp's notorious, powerful string
formatting mini-language. Entertainingly described in [Practical
Common
Lisp](http://www.gigamonkeys.com/book/a-few-format-recipes.html). The
exhaustive reference is [Common Lisp's
Hyperspec](http://www.lispworks.com/documentation/HyperSpec/Body/22_c.htm).

Remember that `cl-format` represents a (potentially unreadable)
language which your audience didn't sign up to learn. So try to only
use it in that sweetspot where it provides high clarity for little
complexity.

``` clojure
;; The first param prints to *out* if true. To string if false.
;; To a stream if it's a stream.
(pp/cl-format true "~{~{~a had ~s percentage point~:p.~}~^~%~}"
              {"@davidgraeber" 12.3
               "@shanley" 19.8
               "@tjgabbour" 1})
;print=> @davidgraeber had 12.3 percentage points.
;print=> @tjgabbour had 1 percentage point.
;print=> @shanley had 19.8 percentage points.

(def format-string "~{~#[~;~a~;~a and ~a~:;~@{~a~#[~;, and ~:;, ~]~}~]~}")
(pp/cl-format nil format-string [])
;=> ""
(pp/cl-format nil format-string ["@shanley"])
;=> "@shanley"
(pp/cl-format nil format-string ["@shanley", "@davidgraeber"])
;=> "@shanley and @davidgraeber"
(pp/cl-format nil format-string ["@shanley", "@davidgraeber", "@sarahkendzior"])
;=> "@shanley, @davidgraeber, and @sarahkendzior"
```

## Contributors

Tj Gabbour <tjg@simplevalue.de>, 2013 (original author)
