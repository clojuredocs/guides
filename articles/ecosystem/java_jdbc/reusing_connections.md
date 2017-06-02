---
title: "How to reuse database connections"
layout: article
---

Since you rarely want every database operation to create a new connection,
there are two ways to reuse connections:

* Grouping Operations using `with-db-connection` - If you don't want to deal
  with a connection pooling library, you use this macro to automatically open a
  connection and maintain it for a body of code.
* Connection Pooling - This is the recommended approach and is fairly
  straightforward, with a number of connection pooling libraries available. See
  *How To Use Connection Pooling* below for more information).

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

## Using Connection Pooling

java.jdbc does not provide connection pooling directly but it is relatively
easy to add to your project. There are several connection pooling libraries out
there, but here we will provide instructions for using two of the most popular,
`c3p0` and `BoneCP`.

The basic idea is to add your chosen connection pooling library to your
project, import the appropriate class(es), define a function that consumes a
"database spec" and produces a map containing a `:datasource` key whose value
is the constructed pooled `DataSource` object, then use that in place of your
bare `db-spec` variable. You are responsible for creating the pooled data
source object and passing the map containing it into any functions that need a
database connection.

### Using the c3p0 library

For more information on c3p0, consult the [c3p0
documentation](http://www.mchange.com/projects/c3p0/).

If you're using Leiningen, you can just add the following to your dependencies:

```clojure
[com.mchange/c3p0 "0.9.2.1"]
```

In Maven, it would be:

```xml
<dependency>
  <groupId>com.mchange</groupId>
  <artifactId>c3p0</artifactId>
  <version>0.9.2.1</version>
</dependency>
```

### Create the pooled datasource from your db-spec

Define your `db-spec` as usual, for example (for MySQL):

```clojure
(def db-spec
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/mydb"
   :user "myaccount"
   :password "secret"})
```

Import the c3p0 class as part of your namespace declaration, for example:

```clojure
(ns example.db
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))
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
a component lifecycle for your application, such as Stuart Sierra has
advocated, you won't need `pooled-db` or `db-connection`, you'll just create
`(pool db-spec)` as part of your application's initialization and pass it
around as part of your system configuration.

## Using the BoneCP library

For more information on BoneCP, consult the [BoneCP
documentation](http://jolbox.com).

If you're using Leiningen, you can just add the following to your dependencies:

```clojure
[com.jolbox/bonecp "0.7.1.RELEASE"]
```

You will also need to add a dependency for the SLF4J adapter that matches the
logging system you use. For example, for `log4j` and the `"0.7.1.RELEASE"` of
BoneCP, you would need to add:

```clojure
[org.slf4j/slf4j-log4j12 "1.5.0"]
```

The adapter version must match the version of SLF4J that BoneCP brings in as a
transitive dependency. Note: BoneCP also brings in Guava as a dependency.

In Maven, it would be:

```xml
<dependency>
  <groupId>com.jolbox</groupId>
  <artifactId>bonecp</artifactId>
  <version>0.7.1.RELEASE</version>
</dependency>
```

and whatever logging system you chose.

### Create the pooled datasource from your db-spec

Define your `db-spec` as usual, for example (for MySQL):

```clojure
(def db-spec
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/mydb"
   :user "myaccount"
   :password "secret"})
```

Import the BoneCP class as part of your namespace declaration, for example:

```clojure
(ns example.db
  (:import com.jolbox.bonecp.BoneCPDataSource))
```

Define a function that creates a pooled datasource:

```clojure
(defn pool
  [spec]
  (let [partitions 3
        cpds (doto (BoneCPDataSource.)
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUsername (:user spec))
               (.setPassword (:password spec))
               (.setMinConnectionsPerPartition (inc (int (/ min-pool partitions))))
               (.setMaxConnectionsPerPartition (inc (int (/ max-pool partitions))))
               (.setPartitionCount partitions)
               (.setStatisticsEnabled true)
               ;; test connections every 25 mins (default is 240):
               (.setIdleConnectionTestPeriodInMinutes 25)
               ;; allow connections to be idle for 3 hours (default is 60 minutes):
               (.setIdleMaxAgeInMinutes (* 3 60))
               ;; consult the BoneCP documentation for your database:
               (.setConnectionTestStatement "/* ping *\\/ SELECT 1"))]
    {:datasource cpds}))
```

Now you can create a single connection pool:

```clojure
(def pooled-db (delay (pool db-spec)))

(defn db-connection [] @pooled-db)
```

And then call `(db-connection)` wherever you need access to it. As above,
adjust for your application lifecycle.
