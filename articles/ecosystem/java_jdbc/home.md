---
title: "Using java.jdbc"
layout: article
---

This guide is intended to help you use Clojure's JDBC wrapper, the java.jdbc
contrib library.

## An Overview

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

## Higher-level DSL and Migration Libraries

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

## A Quick java.jdbc Walkthrough

### Setting Up A Data Source

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

If you want to manage connections yourself, you can use a db-spec containing
the key `:connection` which will be reused for each operation.

### Manipulating Data With SQL

Reading all data from a table can be as simple as:

```clojure
(ns dbexample
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec ... ) ;; see above

(jdbc/query db-spec ["SELECT * FROM table"])
```

You can follow the SQL string with any parameters for that SQL:

```clojure
(jdbc/query db-spec ["SELECT col1, col2 FROM table WHERE status = ?" 1])
```

The `query` function returns a fully-realized sequence of rows from the
database. Under the hood, `query` converts the JDBC `ResultSet` into a (lazy)
sequence of rows and then realizes that sequence. Two hooks are provided for
you to process that sequence of rows:

* You can process each row inside the `query` function by passing `:row-fn f`.
  This will call `f` on each row as the underlying `ResultSet` is processed.
  The result of `query` will be the sequence of the result of `f` applied to
  each row in turn. The default for `:row-fn` is `identity`.
* You can also process the entire `ResultSet` inside the `query` function by
  passing `:result-set-fn g`. This will call `g` on the entire (lazy) sequence
  of processed rows. The result of `query` will be the result of that call to
  `g`. To avoid the connection being closed before the result of `query` is
  fully consumed, `g` should be an eager function. The default for
  `:result-set-fn` is `doall`.

By default, `query` converts all of the column names in the `ResultSet` to
lowercase keywords in the maps. This can be controlled by an optional
`:identifiers` argument which is described, along with other options for
`query`, in [Using SQL][using-sql].

The four basic CRUD operations are:

```clojure
(jdbc/insert! db-spec :table {:col1 42 :col2 "123"}) ;; Create
(jdbc/query   db-spec ["SELECT * FROM table WHERE id = ?" 13]) ;; Read
(jdbc/update! db-spec :table {:col1 77 :col2 "456"} ["id = ?" 13]) ;; Update
(jdbc/delete! db-spec :table ["id = ?" 13]) ;; Delete
```

The table name can be specified as a string or a keyword.

`insert!` can take multiple maps to insert multiple rows. It can also take a
vector of column names (as strings or keywords), followed by one or more
vectors of column values to insert into those respective columns, much like an
`INSERT` statement in SQL. Entries in the map that have the value `nil` will
cause `NULL` values to be inserted into the corresponding columns.

`update!` takes a map of columns to update, with their new values, and a SQL
clause used to select which rows to update (prepended by `WHERE` in the
generated SQL). As with `insert!`, `nil` values in the map cause the
corresponding columns to be set to `NULL`.

`delete!` takes a SQL clause used to select which rows to delete, just like
`update!`.

By default, the table name and column names are used as-is in the underlying
SQL. That can be controlled by an optional `:entities` argument which is
described in [Using SQL][using-sql].

In addition, there is a more general operation to run SQL commands:

```clojure
(jdbc/execute! db-spec ["UPDATE table SET col1 = NOW() WHERE id = ?" 77])
```

For more detail, read the [Using SQL][using-sql] guide.

### Manipulating Tables With DDL

java.jdbc provides `create-table-ddl` and `drop-table-ddl` to generate basic
`CREATE TABLE` and `DROP TABLE` DDL strings. Anything beyond that can be
constructed manually as a string. DDL can be executed using `db-do-commands`:

```clojure
(ns dbexample
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec ... ) ;; see above

(jdbc/db-do-commands db-spec
                     (jdbc/create-table-ddl :fruit
                                            [[:name "varchar(32)"]
                                             [:appearance "varchar(32)"]
                                             [:cost :int]
                                             [:grade :real]]))
(jdbc/db-do-commands db-spec "CREATE INDEX name_ix ON fruit ( name )")
```

For more detail, read the [Using DDL and Metadata][using-ddl] guide.

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
