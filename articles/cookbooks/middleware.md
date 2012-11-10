---
title: "Middleware in Clojure"
layout: article
---

## What is Middleware?

Middleware in Clojure is a common design pattern for threading a
*request* through a series of functions designed to operate on it as
well as threading the *response* through the same series of functions.

Middleware is used in many Clojure projects such as
[Ring](https://github.com/mmcgrana/ring),
[clj-http](https://github.com/dakrone/clj-http) and
[something else here](TODO).

## The `client` function

The base of all middleware in Clojure is the `client` function, which
takes a request object (usually a Clojure map) and returns a response
object (also usually a Clojure map).

For example, let's use a `client` function that pulls some keys out of
a map request and does an HTTP GET on a site:

``` clojure
(ns middleware.example
  (:require [clj-http.client :as http]))

(defn client [request]
  (http/get (:site request) (:options request)))
```

To use the client method, call it like so (response shortened to fit
here):

``` clojure
(client {:site "http://www.aoeu.com" :options {}})
;; ⇒ {:status 200, :headers {...}, :request-time 3057, :body "..."}
```

Now that a client function exists, middleware can be wrapped around it
to change the *request*, the *response*, or both.

Let's start with a middleware function that doesn't do anything. We'll
call it the `no-op` middleware:

``` clojure
;; It is standard convention to name middleware wrap-<something>
(defn wrap-no-op
  ;; the wrapping function takes a client function to be used...
  [client-fn]
  ;; ...and returns a function that takes a request...
  (fn [request]
    ;; ...that calls the client function with the request
    (client-fn request)))
```

So how is this middleware used? First, it must be 'wrapped' around the
existing client function:

``` clojure
(def new-client (wrap-no-op client))

;; Now new-client can be used just like the client function:
(new-client {:site "http://www.aoeu.com" :options {}})
;; ⇒ {:status 200, :headers {...}, :request-time 3057, :body "..."}
```

It works! Now it's not very exiting because it doesn't do anything
yet, so let's add another middleware wrapper that does something more
exiting.

Let's add a middleware function that automatically changes all "HTTP"
requests into "HTTPS" requests. Again, we need a function that returns
another function, so we can end up with a new method to call:

``` clojure
(defn wrap-https
  [client-fn]
  (fn [request]
    (let [site (:site request)
          new-site (.replaceAll site "http:" "https:")
          new-request (assoc request :site new-site)]
      (client-fn new-request))))
```

The `wrap-https` middleware can be tested again by creating a new
client function:

``` clojure
(def https-client (wrap-https client))

;; Notice the :trace-redirects key shows that HTTPS was used instead
;; of HTTP
(https-client {:site "http://www.google.com" :options {}})
;; ⇒ {:trace-redirects ["https://www.google.com"],
;;    :status 200,
;;    :headers {...},
;;    :request-time 3057,
;;    :body "..."}
```

Middleware can be tested independently of the client function by
providing the identity function (or any other function that returns a
map). For example, we can see the `wrap-https` middleware returns the
clojure map with the :site changed from 'http' to 'https':

``` clojure
((wrap-https identity) {:site "http://www.example.com"})
;; ⇒ {:site "https://www.example.com"}
```

## Combining middleware

In the previous example, we showed how to create and use middleware,
but what about using multiple middleware functions? Let's define one
more middleware so we have a total of three to work with. Here's the
source for a middleware function that adds the current data to the
response map:

``` clojure
(defn wrap-add-date
  [client]
  (fn [request]
    (let [response (client request)]
      (assoc response :date (java.util.Date.)))))
```

And again, we can test it without using any other functions using
`identity` as the client function:

``` clojure
((wrap-add-date identity) {})
;; ⇒ {:date #inst "2012-11-09T12:41:05.171-00:00"}
```

Middleware is useful on its own, but where it becomes truly more
useful is in combining middleware together. Here's what a new client
function looks like combining all the middleware:

``` clojure
(def my-client (wrap-add-date (wrap-https (wrap-no-op client))))

(my-client {:site "http://www.google.com"})
;; ⇒ {:date #inst "2012-11-09T12:43:39.451-00:00",
;;    :cookies {...},
;;    :trace-redirects ["https://www.google.com/"],
;;    :request-time 1634,
;;    :status 200,
;;    :headers {...},
;;    :body "..."}
```

(The response map has been edited to take less space where you see
'...')

Here we can see that the `wrap-https` middleware has successfully
turned the request for http://www.google.com into one for
https://www.google.com, additionally the `wrap-add-date` middleware
has added the :date key with the date the request happened. (the
`wrap-no-op` middleware did execute, but since it didn't do anything,
there's no output to tell)

This is a good start, but adding middleware can be expressed in a much
cleaner and clearer way by using Clojure's threading macro, `->`. The
`my-client` definition from above can be expressed like this:

``` clojure
(def my-client
  (-> client
      wrap-no-op
      wrap-https
      wrap-add-date))

(my-client {:site "http://www.google.com"})
;; ⇒ {:date #inst "2012-11-09T12:47:32.130-00:00",
;;    :cookies {...},
;;    :trace-redirects ["https://www.google.com/"],
;;    :request-time 1630,
;;    :status 200,
;;    :headers {...},
;;    :body "..."}
```

Something else to keep in mind is that middleware expressed in this
way will be executed _from the bottom up_, so in this case,
`wrap-add-date` will call `wrap-https`, which in turn calls
`wrap-no-op`, which finally calls the `client` function.

For an example of combining a large amount of middleware, see
[clj-http's
client.clj](https://github.com/dakrone/clj-http/blob/5534950b5ed48e3bc7285f0e956444ea832399da/src/clj_http/client.clj#L542-567)
file
