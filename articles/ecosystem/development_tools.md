---
title: "Development Tools"
layout: article
---

## About this guide

This guide includes overview information regarding the various
Clojure development tools available.

This work is licensed under a <a rel="license"
href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution
3.0 Unported License</a> (including images & stylesheets). The source is
available [on Github](https://github.com/clojuredocs/cds).


## Editors & IDEs

Editors and IDEs are listed below in alphabetical order.



### Eclipse

[Eclipse](http://www.eclipse.org/) is an industrial-strength Java-based IDE.

See our [Eclipse tutorial](../tutorials/eclipse.html) for more info.



### Emacs

[GNU/Emacs](http://www.gnu.org/software/emacs/emacs.html) is a full-featured
programmable editor.

See our [Emacs tutorial](../tutorials/emacs.html) to get started.



### jEdit

[jEdit](http://www.jedit.org/) is an easy-to-use GUI text editor written in Java.
Although it comes out of the box with some Clojure support, you'll need to
install the [LispIndent](https://github.com/odyssomay/LispIndent) plug-in to get
good indentation support.

The easiest way this author has found to install jEdit is to just download and
run the Java-based installer (jar file). After that, follow the excellent
instructions in the LispIndent README and you'll be good to go.

Two configuration recommendations for after you've installed LispIndent: In
"Plugins --> Plugin Options --> LispIndent pane":

  * in the "File Ending Options" area, make sure the box is checked to only
    use the plug-in for clj/cljs files, and
  * in the "Indent Options" area, select the "Indent to function arguments by
    default" radio button, check the "Indent two spaces if operator matches"
    checkbox, and in the text-entry box put
    "def|defn|fn|let|ns|with-open|for|loop|doseq|dotimes|if-let|when-let" (no
    quotes).



### Vim

[Vim](http://www.vim.org/) is an efficient and full-featured text editor.

See our [Vim tutorial](../tutorials/vim.html) for more info.

