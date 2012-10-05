# Tools Used to Create CDS

CDS reuses the [ClojureWerkz docslate](http://github.com/clojurewerkz/docslate) toolchain.



## Install Tools & Dependencies

First, install [Bundler](http://getbundler.com). Ruby 1.9.3 or JRuby are recommended:

    gem install bundler

Then install dependencies

    bundle install --binstubs



## How To Run A Development Server

    ./bin/jekyll --server --auto

The server will be started at `localhost:4000`.



## How To Regenerate The Site

To regenerate the entire site, use

      ./bin/jekyll
