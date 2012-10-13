# Tools Used to Create CDS

CDS reuses the [ClojureWerkz docslate](http://github.com/clojurewerkz/docslate) toolchain.
The clojure-doc.org site is generated using jekyll. To run the site generator yourself, you'll need to first have Ruby installed.

If installing Ruby from source, a prerequisite is the libyaml dev package.



## Install Tools & Dependencies

Install [Bundler](http://gembundler.com). Ruby 1.9.3 or JRuby are recommended:

    gem install bundler

Then install dependencies (Jekyll, GitHub-flavored Markdown processors, etc) with Bundler
by running the following in the CDS repository root:

    bundle install --binstubs

Then install Pygments (this assumes you have Python and pip installed):

    pip install pygments


## How To Run A Development Server

    ./bin/jekyll --server --auto

The server will be started at `localhost:4000`.



## How To Regenerate The Site

To regenerate the entire site, use

      ./bin/jekyll
