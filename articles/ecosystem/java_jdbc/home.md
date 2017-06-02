---
title: "Using java.jdbc"
layout: article
---

This guide is intended to help you use Clojure's JDBC wrapper, the java.jdbc
contrib library.

## Overview

java.jdbc is intended to be a low-level Clojure wrapper around various Java
JDBC drivers and supports a wide range of databases. The [java.jdbc source is
on GitHub][github] and there is a dedicated [java.jdbc mailing
list][mailing-list]. The detailed [java.jdbc reference][reference] is
automatically generated from the java.jdbc source.

The general approach with java.jdbc is to set up a data source as a "database
spec" and pass that to the various CRUD (create, read, update, delete)
functions that java.jdbc provides. These operations are detailed within the
[Using SQL][using-sql] page.

By default, each operation opens a connection and executes the SQL inside a
transaction. You can also run multiple operations against the same connection,
either within a transaction or via connection pooling, or just with a shared
connection. You can read more about reusing connections on the [Reusing
Connections][reusing-connections] page.

## Higher-level DSL and migration libraries

If you need more abstraction than the java.jdbc wrapper provides, you may want
to consider using a library that provides a DSL. All of the following libraries
are built on top of java.jdbc and provide such abstraction:

* [HoneySQL](https://github.com/jkk/honeysql)
* [SQLingvo](https://github.com/r0man/sqlingvo)
* [Korma][korma]

In particular, [Korma][korma] goes beyond a SQL DSL to provide "entities" and
"relationships" (in the style of classical Object-Relational Mappers, but
without the pain).

Another common need with SQL is for database migration libraries. Some of the
more popular options are:

* [Drift](https://github.com/macourtney/drift)
* [Migratus](https://github.com/pjstadig/migratus)
* [Ragtime](https://github.com/weavejester/ragtime)

## A brief java.jdbc walkthrough

### Setting up a data source

A "database spec" is a Clojure map that specifies how to access the data
source. Most commonly, you would specify the driver class name, the
subprotocol, the hostname, port and database name (the "subname") and the
username and password, for example

```clojure
(def db-spec
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/mydb"
   :user "myaccount"
   :password "secret"})
```

The library also supports a db-spec containing a `:connection-uri` which
provides a raw URI string that is passed directly to the JDBC driver; a JNDI
connection (via `:name` and `:environment` keys); and the whole db-spec can
also simply be a string representation of a JDBC URI or a Java URI object
constructed from such a thing.

### A "Hello World" Query

Querying the database can be as simple as:

```clojure
(ns dbexample
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec ... ) ;; see above

(jdbc/query db-spec ["SELECT 3*5 AS result"])
=> {:result 15}
```

Of course, we will want to do more with our database than have it perform
simple calculations. Once we can successfully connect to it, we will likely
want to create tables and manipulate data.

### Creating tables

java.jdbc provides `create-table-ddl` and `drop-table-ddl` to generate basic
`CREATE TABLE` and `DROP TABLE` DDL strings. Anything beyond that can be
constructed manually as a string.

```clojure
(ns dbexample
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec ... ) ;; see above

(def fruit-table-ddl
  (jdbc/create-table-ddl :fruit
                         [[:name "varchar(32)"]
                          [:appearance "varchar(32)"]
                          [:cost :int]
                          [:grade :real]]))
```

We can use the function `db-do-commands` to create our table and indexes in a
single transaction:

```clojure
(jdbc/db-do-commands db-spec
                     [fruit-table-ddl
                      "CREATE INDEX name_ix ON fruit ( name )"])
```

For more details on DDL functionality within java.jdbc, see the [Using DDL and
Metadata Guide][using-ddl].

### Querying the database

The four basic CRUD operations java.jdbc provides are:

```clojure
(jdbc/insert! db-spec :table {:col1 42 :col2 "123"})               ;; Create
(jdbc/query   db-spec ["SELECT * FROM table WHERE id = ?" 13])     ;; Read
(jdbc/update! db-spec :table {:col1 77 :col2 "456"} ["id = ?" 13]) ;; Update
(jdbc/delete! db-spec :table ["id = ?" 13])                        ;; Delete
```

The table name can be specified as a string or a keyword.

`insert!` takes a single record to insert. If you wish to insert multiple rows
(in map form) at once, you can use `insert-multi!`. `insert!` can also take a
vector of column names (as strings or keywords), followed by one or more
vectors of column values to insert into those respective columns, much like an
`INSERT` statement in SQL. Entries in the map that have the value `nil` will
cause `NULL` values to be inserted into the corresponding columns.

`query` allows us to run selection queries on the database. Since you provide
the query string directly, you have as much flexibility as you like to perform
complex queries.

`update!` takes a map of columns to update, with their new values, and a SQL
clause used to select which rows to update (prepended by `WHERE` in the
generated SQL). As with `insert!`, `nil` values in the map cause the
corresponding columns to be set to `NULL`.

`delete!` takes a SQL clause used to select which rows to delete, similar to
`update!`.

By default, the table name and column names are converted to strings
corresponding to the keyword names in the underlying SQL. We can control how we
transform keywords into SQL names using an optional `:entities` argument which
is described in more detail in the [Using SQL][using-sql] section.

## More detailed java.jdbc documentation

* Using SQL: a more detailed guide on using SQL with java.jdbc
* Using DDL: how to create your tables using the java.jdbc DDL
* Reusing Connections: how to reuse your database connections

[github]: https://github.com/clojure/java.jdbc/
[mailing-list]: https://groups.google.com/forum/#!forum/clojure-java-jdbc
[reference]: http://clojure.github.io/java.jdbc/
[korma]: http://sqlkorma.com

[using-sql]: using_sql.html
[using-ddl]: using_ddl.html
[reusing-connections]: reusing_connections.html
