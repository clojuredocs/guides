---
title: "Using java.jdbc"
layout: article
---

This guide is intended to help you use Clojure's JDBC wrapper, the java.jdbc
contrib library.

## Contents

* [Overview][overview]
* [Using SQL][using-sql]
* [Using DDL][using-ddl]
* [Reusing Connections][reusing-connections]

## Overview

java.jdbc is intended to be a low-level Clojure wrapper around various Java
JDBC drivers and supports a wide range of databases. The [java.jdbc source is
on GitHub][github] and there is a dedicated [java.jdbc mailing
list][mailing-list]. The detailed [java.jdbc reference][reference] is
automatically generated from the java.jdbc source.

Generally, when using java.jdbc, you will set up a data source as a "database
spec" and pass that to the various CRUD (create, read, update, delete)
functions that java.jdbc provides. These operations are detailed within the
[Using SQL][using-sql] page, but a quick overview is provided by the
walkthrough below.

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
source. Most commonly, you specify the database type, the the database name,
and the username and password. For example,

```clojure
(def db-spec
  {:dbtype "mysql"
   :dbname "mydb"
   :user "myaccount"
   :password "secret"})
```

Some DBs require a different format for the "database spec".  Here is an example that will 
work for the [H2 database](http://www.h2database.com):

```
(def db-spec
  {:classname   "org.h2.Driver"
   :subprotocol "h2:mem"                  ; the prefix `jdbc:` is added automatically
   :subname     "demo;DB_CLOSE_DELAY=-1"  ; `;DB_CLOSE_DELAY=-1` very important!!!
                    ; http://www.h2database.com/html/features.html#in_memory_databases
   :user        "sa"                      ; default "system admin" user
   :password    ""                        ; default password => empty string
  })
```

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
                      "CREATE INDEX name_ix ON fruit ( name );"])
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

`insert!` takes a single record in hashmap form to insert. `insert!` can also
take a vector of column names (as strings or keywords), followed by a vector of
column values to insert into those respective columns, much like an `INSERT`
statement in SQL. Entries in the map that have the value `nil` will cause
`NULL` values to be inserted into the corresponding columns.

If you wish to insert multiple rows (in hashmap form) at once, you can use
`insert-multi!`; however, `insert-multi!` will write a separate insertion
statement for each row, so it is suggested you use the column-based form of
`insert-multi!` over the row-based form. Passing multiple column values to
`insert-multi!` will generate a single batched insertion statement and yield
better performance.

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

### Dropping our tables

To clean out the database from our example, we can generate a the command to
drop the fruit table:

```clojure
(def drop-fruit-table-ddl (jdbc/drop-table-ddl :fruit))
```

Ensure you tear down your tables and indexes in the opposite order of creation:

```clojure
(jdbc/db-do-commands db-spec
                     ["DROP INDEX name_ix;"
                      drop-fruit-table-ddl])
```

These are all the commands we need to write a simple migration for our database!

## More detailed java.jdbc documentation

* [Using SQL:][using-sql] a more detailed guide on using SQL with java.jdbc
* [Using DDL:][using-ddl] how to create your tables using the java.jdbc DDL
* [Reusing Connections:][reusing-connections] how to reuse your database
  connections

[github]: https://github.com/clojure/java.jdbc/
[mailing-list]: https://groups.google.com/forum/#!forum/clojure-java-jdbc
[reference]: http://clojure.github.io/java.jdbc/
[korma]: http://sqlkorma.com

[overview]: home.html
[using-sql]: using_sql.html
[using-ddl]: using_ddl.html
[reusing-connections]: reusing_connections.html
