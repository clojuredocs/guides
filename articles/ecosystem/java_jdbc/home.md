---
title: "Using java.jdbc"
layout: article
---

This (incomplete) guide provides detailed information about using Clojure's JDBC wrapper, the java.jdbc contrib library.


## An Overview

java.jdbc is intended to be a low-level wrapper Clojure around various Java JDBC drivers and supports a wide range of databases. java.jdbc.sql provides a basic DSL for generating simple SQL statements (`insert`, `select`, `update`, `delete`) with basic `where` clauses, simple `join`s and some `order-by` functionality. Libraries such as [HoneySQL](https://github.com/jkk/honeysql) and [Korma](http://sqlkorma.com) provide more sophisticated DSLs you can use with java.jdbc if you want. java.jdbc.ddl provides a basic DSL for generating simple SQL DDL statements (`create-table`, `drop-table`).

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
      (:require [clojure.java.jdbc :as jdbc]
                [clojure.java.jdbc.sql :as sql]))
    
    (def db-spec ... ) ;; see above
    
    (jdbc/query db-spec
      (sql/select * :table))

If you don't want to use the basic SQL DSL, you can provide a vector containing the SQL string as the first element followed by any parameters for that SQL:

    (jdbc/query db-spec
      ["SELECT col1, col2 FROM table WHERE status = ?" 1])

For more detail, read the [Using SQL](using_sql.html) guide which was originally part of the contrib library repository.

## Manipulating Tables With DDL

The java.jdbc.ddl namespace provides a basic DSL for simple SQL DDL operations to create and drop tables.

For more detail, read the [Using DDL](using_ddl.html) guide which was originally part of the contrib library repository.

## Mapping Between SQL Entities And Clojure Identifiers

Basic [Name Mapping](name_mapping.html) Guide (originally from contrib repo).

## How To Use Connection Pooling

Basic [Connection Pooling](connection_pooling.html) Guide (originally from contrib repo).

## How To Use The Basic SQL DSL In java.jdbc.sql

## Where To Go Beyond java.jdbc
