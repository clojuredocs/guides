---
title: "Using DDL and Metadata"
layout: article
---

## Contents

* [Overview][overview]
* [Using SQL][using-sql]
* [Using DDL][using-ddl]
* [Reusing Connections][reusing-connections]

## Using DDL

DDL operations can be executed using the `db-do-commands` function. The general
approach is:

```clojure
(jdbc/db-do-commands db-spec [sql-command-1 sql-command-2 .. sql-command-n])
```

The commands are executed as a single, batched statement, wrapped in a
transaction. If you want to avoid the transaction, use this approach:

```clojure
(jdbc/db-do-commands db-spec false [sql-command-1 sql-command-2 .. sql-command-n])
```

This is necessary for some databases that do not allow DDL operations to be
wrapped in a transaction.

### Creating tables

For the common operations of creating and dropping tables, `java.jdbc` provides a
little assistance that recognizes `:entities` so you can use keywords (or
strings) and have your chosen naming strategy applied, just as you can for
several of the SQL functions.

```clojure
(jdbc/create-table-ddl :fruit
                       [[:name "varchar(32)" :primary :key]
                        [:appearance "varchar(32)"]
                        [:cost :int]
                        [:grade :real]]
                       {:table-spec "ENGINE=InnoDB"
                        :entities clojure.string/upper-case})
```
This will generate:

```clojure
CREATE TABLE FRUIT
    (NAME varchar(32) primary key,
     APPEARANCE varchar(32),
     COST int,
     GRADE real) ENGINE=InnoDB
```

which you can pass to `db-do-commands`.

`create-table-ddl` also supports a `conditional?` option which can be a simple
`Boolean`, which, if `true`, will add `IF NOT EXISTS` before the table name. If
that syntax doesn't work for your database, you can pass a string that will be
used instead. If that isn't enough, you can pass a function of two arguments:
the first argument will be the table name and the second argument will be the
DDL string (this approach is needed for Microsoft SQL Server).

### Dropping tables

Similarly there is a `drop-table-ddl` function which takes a table name and an
optional `:entities` option to generate DDL to drop a table.

```clojure
(jdbc/drop-table-ddl :fruit) ; drop table fruit
(jdbc/drop-table-ddl :fruit {:entities clojure.string/upper-case}) ; drop table FRUIT
```

This will generate:

```clojure
DROP TABLE FRUIT
```

`drop-table-ddl` also supports a `conditional?` option which can be a simple
`Boolean`, which, if `true`, will add `IF EXISTS` before the table name. If
that syntax doesn't work for your database, you can pass a string that will be
used instead. If that isn't enough, you can pass a function of two arguments:
the first argument will be the table name and the second argument will be the
DDL string (this approach is needed for Microsoft SQL Server).

## Accessing metadata

`java.jdbc` provides two functions for working with database metadata:

* `with-db-metadata` for creating an active metadata object backed by an open
  connection
* `metadata-result` for turning metadata results into Clojure data structures

For example:

```clojure
(jdbc/with-db-metadata [md db-spec]
  (jdbc/metadata-result (.getTables md nil nil nil (into-array ["TABLE" "VIEW"]))))
```

This returns a sequence of maps describing all the tables and views in the
current database. `metadata-result` only transforms `ResultSet` objects, other
results are returned as-is. `metadata-result` can also accept an options map
containing `:identifiers` and `:as-arrays?`, like the `query` function,
and those options control how the metatadata is transformed and/or returned.

Both `with-db-metadata` and `metadata-result` can accept an options hash map
which will be passed through various `java.jdbc` functions (`get-connections`
for the former and `result-set-seq` for the latter).

[overview]: home.html
[using-sql]: using_sql.html
[using-ddl]: using_ddl.html
[reusing-connections]: reusing_connections.html
