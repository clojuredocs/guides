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
So you have access to the wealth of tools which operate on Java
strings. Since Java strings are immutable, they're convenient to use
in Clojure.

One downside of Java strings is that you can't add Clojure metadata to
them.

Clojure supports some convenient notations:

    "foo"    String
    #"\d"    Regex (in this case, one which matches a single digit)
    \f       Character (in this case, the letter 'f')



### Caveat

Natural language offers no guarantees for easy or efficient
programming, as human brains and electronic computers are rather
different devices. Java strings (sequences of [UTF-16
characters](http://docs.oracle.com/javase/7/docs/api/java/lang/Character.html#unicode))
don't always map nicely to user-perceived characters.

For instance, it would be nice if a single Unicode "code point"
corresponded to a user-perceived character. They often do. But alas,
one counterexample is Korean's Jamo, where user-perceived characters
are composed from two or three Unicode code points.

As another complication, sometimes a single Unicode "code point" may
require 2 UTF-16 characters to encode it. So, the more you care about
strings-as-natural-language-interface, the more care you should take.


## Preliminaries

Some examples use
[clojure.string](http://clojure.github.io/clojure/clojure.string-api.html). We'll
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

;; Parsing keywords
(keyword "foo")    ;=> :foo

;; Parsing numbers
(bigint "20000000000000000000000000000") ;=> 20000000000000000000000000000N
(bigdec "20000000000000000000.00000000") ;=> 20000000000000000000.00000000M
(Integer/parseInt "2")                   ;=> 2
(Float/parseFloat "2")                   ;=> 2.0
```

### Parsing more exotic Clojure data

[edn](https://github.com/edn-format/edn) is a useful subset of Clojure
data, useful for communicating with other computers. A bit like JSON,
but in many ways more principled. (For instance, you can tag values,
so you're not always stuffing random date formats into strings or
whatever, leaving your users to figure out how to parse it.)

The sledgehammer is `read-string`. Lets you parse any valid Clojure
data. Its power is a **major security risk**, as it can run arbitrary
code.  If you must use it, remember to set `*read-eval*` to `false`.

``` clojure
(edn/read-string "0xffff") ;=> 65535

(binding [*read-eval* false]  ;; Security
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


### Context-free grammars

[Instaparse](https://github.com/Engelberg/instaparse) makes many
things easier to parse.

``` clojure
;; This will need to be in your project.clj (this may be an outdated version):
;; :dependencies [[instaparse "1.2.4"]]
;;
;;  We'll assume your ns macro contains:
;;   (:require [instaparse.core :as insta])
;; or else in the repl you've loaded it:
;;   (require '[instaparse.core :as insta])
;;

;; Quick implementation of http://www.json.org/
(def untested-json-parser
  (insta/parser
   "Object = <'{'> <w*> (members <w*>)* <'}'>
    <members> = pair (<w*> <','> <w*> members)*
    <pair> = string <w*> <':'> <w*> value
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

(json-untested "{\"foo\": {\"bar\": 10e-9, \"quux\": [1,2,3]}}")
;=> [:Object
;    [:string "foo"]
;    [:Object
;     [:string "bar"]
;     [:number "10" [:exp "-" "9"]]
;     [:string "quux"]
;     [:array [:number "1"] [:number "2"] [:number "3"]]]]
```


### Leveraging streams

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


### Format strings

[Reference.](http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html)
A well-known templating language to print text.

``` clojure
(format "%s enjoyed %s%%." "Mozambique" 19.8) ;=> "Mozambique enjoyed 19.8%."

;; The %1$ prefix allows you to keep referring to the first arg.
(format "%1$tY-%1$tm-%1$td" #inst"2000-01-02T00:00:00") ;=> "2000-01-02"

(format "New year: %2$tY. Old year: %1$tY"
        #inst"2000-01-02T00:00:00"
        #inst"2010-01-02T00:00:00")
;=> "New year: 2010. Old year: 2000"
```


### CL-Format

`cl-format` is Common Lisp's notorious, powerful string formatting
mini-language. Entertainingly described in [Practical Common
Lisp](http://www.gigamonkeys.com/book/a-few-format-recipes.html). The
exhaustive reference is [Common Lisp's
Hyperspec](http://www.lispworks.com/documentation/HyperSpec/Body/22_c.htm).

Few Clojure users will be familiar with cl-format, so try not to go
overboard with it. (It's even controversial among Common Lisp users.)
When used sparingly, it's more readable than a big mess of control
flow. On the other hand, there's a reason we don't program in
cl-format's mini-language all the time.

``` clojure
(pp/cl-format nil "~{~{~a had ~s percentage point~:p.~}~^~%~}"
                  {"Bolivia" 12.3
                   "Mozambique" 19.8
                    "US" 1})
;=> "Bolivia had 12.3 percentage points.
;Mozambique had 19.8 percentage points.
;US had 1 percentage point."
```


## TBD:

* encodings, bytes
* counting unicode code points


## Contributors

Tj Gabbour <tjg@simplevalue.de>, 2013 (original author)
