---
title: "How to reuse database connections"
layout: article
---

## Contents

* [Overview][overview]
* [Using SQL][using-sql]
* [Using DDL][using-ddl]
* [Reusing Connections][reusing-connections]

## Reusing Connections

Since you rarely want every database operation to create a new connection,
there are two ways to reuse connections:

* Grouping Operations using `with-db-connection`: If you don't want to deal
  with a connection pooling library, you can use this macro to automatically open a
  connection and maintain it for a body of code, with each operation executed in its
  own transaction, then close the connection.
* Grouping Operations using `with-db-transaction`: If you want to execute multiple
  operations in a single transaction, you can use this macro to automatically open a
  connection, start a transaction, execute multiple operations, commit the transaction,
  and then close the connection.
* Connection Pooling: This is the recommended approach and is fairly
  straightforward, with a number of connection pooling libraries available. See
  *How To Use Connection Pooling* below for more information.

## Using `with-db-connection`

This macro provides the simplest way to reuse connections, without having to
add a dependency on an external connection pooling library:

```clojure
(ns dbexample
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec ... ) ;; see above

(with-db-connection [db-con db-spec]
  (let [;; fetch some rows using this connection
        rows (jdbc/query db-con ["SELECT * FROM table WHERE id = ?" 42])]
    ;; insert a copy of the first row using the same connection
    (jdbc/insert! db-con :table (dissoc (first rows) :id))))
```

The `query` and the `insert!` are each run in their own transaction and committed
if they succeed. If you want to run multiple operations in a single transaction
see the next section about `with-db-transaction`.

## Using `with-db-transaction`

This macro provides a way to reuse connections, committing or rolling back
multiple operations in a single transaction:

```clojure
(ns dbexample
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec ... ) ;; see above

(with-db-transaction [t-con db-spec]
  (let [;; fetch some rows using this connection
        rows (jdbc/query t-con ["SELECT * FROM table WHERE id = ?" 42])]
    ;; insert a copy of the first row using the same connection
    (jdbc/insert! t-con :table (dissoc (first rows) :id))))
```

If any operation inside `with-db-transaction` fails (throws an exception), then
all of the operations performed so far are rolled back. If all the operations
succeed, then the entire transaction is committed.

Transactions are not nested (since not all databases support that) so if this is
used another active transaction, the outer transaction (and connection) are used
as-is. If the isolation levels of the outer and inner transaction do not match
you will get an `IllegalStateException`.

See also `db-set-rollback-only!`, `db-unset-rollback-only!`, and `db-is-rollback-only`
for additional control over the commit/rollback behavior of the enclosing transaction.

## Using Connection Pooling

`java.jdbc` does not provide connection pooling directly but it is relatively
easy to add to your project. There are several connection pooling libraries out
there, but here we will provide instructions for the popular `c3p0` library.

The basic idea is to add your chosen connection pooling library to your
project, import the appropriate class(es), define a function that consumes a
"database spec" and produces a map containing a `:datasource` key whose value
is the constructed pooled `DataSource` object, then use that hash map in place of your
bare `db-spec` variable. You are responsible for creating the pooled data
source object and passing the map containing it into any functions that need a
database connection.

### Using the c3p0 library

For more information on c3p0, consult the [c3p0
documentation](http://www.mchange.com/projects/c3p0/).

If you're using Leiningen, you can add the following to your dependencies:

```clojure
[com.mchange/c3p0 "0.9.5.2"]  ;; check the documentation for latest version
```

For a Maven-based project, you would add:

```xml
<dependency>
  <groupId>com.mchange</groupId>
  <artifactId>c3p0</artifactId>
  <version>0.9.5.2</version>
</dependency>
```

### Create the pooled datasource from your db-spec

Define your `db-spec` using the long form, for example (for MySQL):

```clojure
(def db-spec
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/mydb"
   :user "myaccount"
   :password "secret"})
```

We have to use the long form here because c3p0 operates on the class name, subprotocol,
and subname elements. Of course, you don't really need to define a `db-spec` here
because you're not going to use it with `java.jdbc` directly, only with c3p0.

Import the c3p0 class as part of your namespace declaration, for example:

```clojure
(ns example.db
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource)))
```

Define a function that creates a pooled datasource:

```clojure
(defn pool
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))
```

Now you can create a single connection pool:

```clojure
(def pooled-db (delay (pool db-spec)))

(defn db-connection [] @pooled-db)
```

And then call `(db-connection)` wherever you need access to it. If you're using
a [component](https://github.com/stuartsierra/component) lifecycle for your
application, you won't need `pooled-db` or `db-connection`. You'll just create
`(pool db-spec)` as part of your application's initialization and pass it
around as part of your system configuration.

[overview]: home.html
[using-sql]: using_sql.html
[using-ddl]: using_ddl.html
[reusing-connections]: reusing_connections.html
