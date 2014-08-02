# Tools Used to Create CDS

CDS reuses the [ClojureWerkz docslate](http://github.com/clojurewerkz/docslate) toolchain.
The clojure-doc.org site is generated using jekyll. To run the site generator yourself, you'll need to first have Ruby installed. Python is also required, as syntax-highlighting of code blocks is handled by pygments.

If installing Ruby from source, a prerequisite is the libyaml dev package.


## Install Tools & Dependencies

Install [Bundler](http://gembundler.com). Ruby 1.9.3 or JRuby are recommended:

    gem install bundler

Then install dependencies (Jekyll, GitHub-flavored Markdown processors, etc) with Bundler
by running the following in the CDS repository root:

    bundle install --binstubs

> As an alternative to bundler, if you like you can manually
> (via `gem install`) install all the dependencies listed in
> path/to/cds/Gemfile, and then later (from the cds directory)
> run the gem-installed `jekyll` to view your local version of
> the site.


## How To Run A Development Server

Before you run `jekyll` the first time, you will need to create an empty folder called
`_site` in the CDS repository root.

    mkdir -p _site
    ./bin/jekyll serve --watch

The server will be started at [localhost:4000](http://localhost:4000).


## How To Regenerate The Site

To regenerate the entire site, use

      ./bin/jekyll build
