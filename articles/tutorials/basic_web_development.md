---
title: "Basic Web Development"
layout: article
---


This guide covers building a simple web-application using common
Clojure libraries. When you're done working through it, you'll have a
little webapp that displays some (x, y) locations from a database,
letting you add more locations as well.

It's assumed that you're already a little familiar with Clojure. If
not, see the [Getting Started](getting_started.html) and
[Introduction](introduction.html) guides.

This guide uses Clojure 1.4, as well as current versions of the
component libraries noted below.



## Conceptual Overview of Components

We'll use four major components (briefly described below) for our
little webapp:

  * Ring
  * Compojure
  * Hiccup
  * SQLite



### Ring

[Ring](https://github.com/ring-clojure/ring) ([at
clojars](https://clojars.org/ring)) is a foundational Clojure web
application library. It:

  * sets things up such that an http request comes into your webapp as
    as a regular Clojure hashmap, and likewise makes it so that you
    can return a response as a hashmap.

  * provides [a
    spec](https://github.com/ring-clojure/ring/blob/master/SPEC)
    describing exactly what those request and response maps should
    look like.

  * brings along a web server
    ([Jetty](http://jetty.codehaus.org/jetty/)) and connects your
    webapp to it.

For this tutorial, we won't actually need to deal with these maps
by-hand, as you'll soon see.

For more info, see

  * [the Ring readme](https://github.com/ring-clojure/ring#readme)
  * [its wiki docs](https://github.com/ring-clojure/ring/wiki)
  * [its API docs](http://ring-clojure.github.com/ring/)



### Compojure

If we were using only Ring, we'd have to write one single function to
take that incoming request map and then delegate to various functions
depending upon which page was requested.
[Compojure](https://github.com/weavejester/compojure) ([at
clojars](https://clojars.org/compojure)) provides some handy features
to take care of this for us such that we can associate url paths with
corresponding functions, all in one place.

For more info, see

  * [the Compojure readme](https://github.com/weavejester/compojure#readme)
  * [its wiki docs](https://github.com/weavejester/compojure/wiki)
  * [its API docs](http://weavejester.github.com/compojure/).



### Hiccup

[Hiccup](https://github.com/weavejester/hiccup) ([at
clojars](https://clojars.org/hiccup)) provides a quick and easy way to
generate html. It converts regular Clojure data structures right into
html. For example,

```clojure
[:p "Hello, " [:i "doctor"] " Jones."]
```

becomes

```html
<p>Hello, <i>doctor</i> Jones.</p>
```

but it also does two extra handy bits of magic:

  * it provides some CSS-like shortcuts for specifying id and class,
    and

  * it automatically unpacks seqs for you, for example:

    ```clojure
    [:p '("a" "b" "c")]
    ;; expands to (and so, is the same as if you wrote)
    [:p "a" "b" "c"]
    ```

For more info, see:

  * [the Hiccup readme](https://github.com/weavejester/hiccup#readme)
  * [its wiki docs](https://github.com/weavejester/hiccup/wiki)
  * [its API docs](http://weavejester.github.com/hiccup/)



### SQLite

[SQLite](http://sqlite.org/) is a small, fast, and reliable SQL
database program that uses a sigle flat file for storage and is
serverless. If you already know and use Postgres, go and use that
instead. If you prefer something written in Java, by all means check
out [Apache Derby](http://db.apache.org/derby/) or
[H2](http://www.h2database.com/html/main.html). But this tutorial uses
SQLite.

Make sure you've got sqlite installed. On Debian-based OS's:

    sudo apt-get install sqlite3



## Create and set up your project

Create your new webapp project like so:

```bash
lein new compojure my-webapp
cd my-webapp
```

Add the following extra dependencies to your project.clj's
:dependencies vector:

```clojure
[hiccup "1.0.1"]
[org.clojure/java.jdbc "0.2.3"]
[org.xerial/sqlite-jdbc "3.7.2"]
```


## Add some styling

```bash
mkdir -p resources/public/css
touch resources/public/css/styles.css
```

and put into that file something like:

```css
body {
    background-color: Cornsilk;
}

#header-links {
    background-color: BurlyWood;
    padding: 10px;
}

h1 {
    color: CornflowerBlue;
}
```



## Set up your database

```bash
mkdir db
cd db
sqlite3 my-webapp.db
```

This will create a new my-webapp.db database file, and also put
you at the `sqlite>` prompt. Create a table we'll use for our
webapp, and add one record to start us off with:

```
sqlite> create table locations (id integer primary key, x integer, y integer);
sqlite> .tables
locations
sqlite> .schema locations
CREATE TABLE locations (id integer primary key, x integer, y integer);
sqlite> insert into locations (x, y) values (8, 9);
sqlite> select * from locations;
1|8|9
```

and hit ctrl-d to exit. Note that sqlite-specific commands start with
a dot and don't end with a semicolon.

`cd ..` back to the root of your project directory.



## Set up your routes

```bash
cd src/my_webapp
```

In the default handler.clj file you're provided, we specify our
webapp's *routes* inside the `defroutes` macro. That is, we assign a
function to handle each of the url paths we'd like to support, and
then at the end provide a "not found" page for any other url paths.

Make your handler.clj file look like this:

```clojure
(ns my-webapp.handler
  (:require [my-webapp.views :as views]
            [compojure.core :as cc]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(cc/defroutes app-routes
  (cc/GET "/"
       []
       (views/home-page))
  (cc/GET "/add-location"
       []
       (views/add-location-page))
  (cc/POST "/add-location"
        {params :params}
        (views/add-location-results-page params))
  (cc/GET "/location/:loc-id"
       [loc-id]
       (views/location-page loc-id))
  (cc/GET "/all-locations"
       []
       (views/all-locations-page))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
```

Each of those expressions in `defroutes` like "(GET ...)" or "(POST
...)" are so-called "routes". They each evaluate to a function that
takes a ring request hashmap and returns a response hashmap. Your
views/foo function's job is to return that response hashmap, but note
that Compojure is kind enough to make a suitable response map out of
any html you return.

So, all you actually need to do now is write your views functions to
return some html.

Incidentally, note the special destructuring that Compojure does for
you in each of those routes. It can pull out url query (and body)
parameters, as well as pieces of the url path requested, and hand them
to your views functions. Read more about that at [Compojure
destructuring](https://github.com/weavejester/compojure/wiki/Destructuring-Syntax).




## Create your Views

Create a src/my_webapp/views.clj file and make it look like:

```clojure
(ns my-webapp.views
  (:require [my-webapp.db :as db]
            [clojure.string :as str]
            [hiccup.page :as hic-p]))


(defn gen-page-head
  [title]
  [:head
   [:title (str "Locations: " title)]
   (hic-p/include-css "/css/styles.css")])

(def header-links
  [:div#header-links
   "[ "
   [:a {:href "/"} "Home"]
   " | "
   [:a {:href "/add-location"} "Add a Location"]
   " | "
   [:a {:href "/all-locations"} "View All Locations"]
   " ]"])

(defn home-page
  []
  (hic-p/html5
   (gen-page-head "Home")
   header-links
   [:h1 "Home"]
   [:p "Webapp to store and display some 2D (x,y) locations."]))

(defn add-location-page
  []
  (hic-p/html5
   (gen-page-head "Add a Location")
   header-links
   [:h1 "Add a Location"]
   [:form {:action "/add-location" :method "POST"}
    [:p "x value: " [:input {:type "text" :name "x"}]]
    [:p "y value: " [:input {:type "text" :name "y"}]]
    [:p [:input {:type "submit" :value "submit location"}]]]))

(defn add-location-results-page
  [{:keys [x y]}]
  (let [id (db/add-location-to-db x y)]
    (hic-p/html5
     (gen-page-head "Added a Location")
     header-links
     [:h1 "Added a Location"]
     [:p "Added [" x ", " y "] (id: " id ") to the db. "
      [:a {:href (str "/location/" id)} "See for yourself"]
      "."])))

(defn location-page
  [loc-id]
  (let [{x :x y :y} (db/get-xy loc-id)]
    (hic-p/html5
     (gen-page-head (str "Location " loc-id))
     header-links
     [:h1 "A Single Location"]
     [:p "id: " loc-id]
     [:p "x: " x]
     [:p "y: " y])))

(defn all-locations-page
  []
  (let [all-locs (db/get-all-locations)]
    (hic-p/html5
     (gen-page-head "All Locations in the db")
     header-links
     [:h1 "All Locations"]
     [:table
      [:tr [:th "id"] [:th "x"] [:th "y"]]
      (for [loc all-locs]
        [:tr [:td (:id loc)] [:td (:x loc)] [:td (:y loc)]])])))
```

Here we've implemented each function used in handler.clj.

Again, note that each of the functions with names ending in "-page"
(the ones being called in handler.clj) is returning just a plain
string consisting of html markup. In handler.clj's defroutes,
Compojure is helpfully taking care of placing that into a response
hashmap for us.

Rather than clog up this file with database-related calls, we've put
them all into their own db.clj file (described next).



## Create some db access functions

Create a src/my_webapp/db.clj file and make it look like:

```clojure
(ns my-webapp.db
  (:require [clojure.java.jdbc :as sql]))


(def db-spec {:classname "org.sqlite.JDBC"
              :subprotocol "sqlite"
              :subname "db/my-webapp.db"})


(defn add-location-to-db
  [x y]
  (let [results (sql/with-connection db-spec
                  (sql/insert-record :locations
                                     {:x x :y y}))]
    (assert (= (count results) 1))
    (first (vals results))))

(defn get-xy
  [loc-id]
  (let [results (sql/with-connection db-spec
                  (sql/with-query-results res
                    ["select x, y from locations where id = ?" loc-id]
                    (doall res)))]
    (assert (= (count results) 1))
    (first results)))

(defn get-all-locations
  []
  (let [results (sql/with-connection db-spec
                  (sql/with-query-results res
                    ["select id, x, y from locations"]
                    (doall res)))]
    results))
```

Note that sql/with-query-results returns a seq of maps. Each map
entry's key is a column name (as a Clojure keyword), and its value is
the value for that column.

You'll also notice that we put results from db queries into a `doall`.
This is because sql/with-query-results returns a lazy sequence, and we
want to fully-realize it before leaving the sql/with-connection
expression.

For more about how to use the the database functions, see the
[clojure.java.jdbc](https://github.com/clojure/java.jdbc) readme and
docs.

Of course, you can try out all these calls yourself in the repl,
if you like:

    ~/temp/my-webapp$ lein repl
    ...
    user=> (require 'my-webapp.db)
    nil
    user=> (ns my-webapp.db)
    nil
    my-webapp.db=> (sql/with-connection db-spec
              #_=>   (sql/with-query-results res
              #_=>     ["select x, y from locations where id = 1"]
              #_=>     (doall res)))
    ({:y 9, :x 8})


## Run the webapp

Run your webapp like so:

    lein ring server

It should start up and also open a browser window for you pointed at
<http://localhost:3000>. If you don't want it to automatically open a
browser window, run it like so:

    lein ring server-headless




## Contributors

John Gabriele <jmg3000@gmail.com> (original author)
