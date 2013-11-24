---
title: "Using java.jdbc"
layout: article
---

This (incomplete) guide provides detailed information about using Clojure's JDBC wrapper, the java.jdbc contrib library.


## An Overview

java.jdbc is intended to be a low-level wrapper Clojure around various Java JDBC drivers and supports a wide range of databases. Libraries such as [HoneySQL](https://github.com/jkk/honeysql), [SQLingvo](https://github.com/r0man/sqlingvo) and [Korma](http://sqlkorma.com) provide more sophisticated DSLs you can use with java.jdbc if you want.

The API changed substantially between the 0.2.3 release and the 0.3.0 release, to remove dependencies on dynamic global variables and provide a more functional, more idiomatic API. This documentation covers the new API. The old API has moved to java.jdbc.deprecated and is deprecated and provided for backward compatibility only. The deprecated API documentation can be found in the [java.jdbc reference](http://clojure.github.io/java.jdbc/) which is auto-generated from the docstrings in the namespaces.

The general approach with java.jdbc is to set up a data source (see below) as a "database spec" and pass that to the various CRUD - create, read, update, delete - functions that java.jdbc provides. Each operation with open a connection and execute the SQL inside a transaction. You can also choose to manage the connection yourself, and use a "database spec" that contains the open connection (see below for more details).

You can also group several operations inside a transaction (there are some examples under *Manipulating Data With SQL* below).

## Setting Up A Data Source

A "database spec" is a Clojure map that specifies how to access the data source. Mostly commonly, you would specify the driver class name, the subprotocol, the hostname, port and database name (the "subname") and the username and password, e.g.,

    (def db-spec 
      {:classname "com.mysql.jdbc.Driver"
       :subprotocol "mysql"
       :subname "//127.0.0.1:3306/mydb"
       :user "myaccount"
       :password "secret"})

You can also specify a datasource object (with an optional username and password), such as you might obtain from a connection pooling library (see *How To Use Connection Pooling* below):

    (def pooled-db-spec
      {:datasource (make-pool db-spec)})

The library also supports a db-spec containing a `:connection-uri` which provides a raw URI string that is passed directly to the JDBC driver; a JNDI connection (via `:name` and `:environment` keys); and the whole db-spec can also simply be a string representation of a JDBC URI or a Java URI object constructed from such a thing.

If you want to manage connections yourself, you can use a db-spec containing the key `:connection` which will be reused for each operation.

## Manipulating Data With SQL

Reading all data from a table can be as simple as:

    (ns dbexample
      (:require [clojure.java.jdbc :as jdbc]))
    
    (def db-spec ... ) ;; see above
    
    (jdbc/query db-spec
      ["SELECT * FROM table"])

If you don't want raw SQL, take a look at the various DSL libraries listed above. You can follow the SQL string with any parameters for that SQL:

    (jdbc/query db-spec
      ["SELECT col1, col2 FROM table WHERE status = ?" 1])

For more detail, read the [Using SQL](using_sql.html) guide which was originally part of the contrib library repository.

## Manipulating Tables With DDL

java.jdbc provides `create-table` and `drop-table` by way of DDL. Anything beyond that can be done using `db-do-commands` with a DDL string of SQL to manipulate tables.

For more detail, read the [Using DDL](using_ddl.html) guide which was originally part of the contrib library repository.

## How To Use Connection Pooling

Basic [Connection Pooling](connection_pooling.html) Guide (originally from contrib repo).

## How To Use Some Common DSLs With java.jdbc

## Where To Go Beyond java.jdbc
