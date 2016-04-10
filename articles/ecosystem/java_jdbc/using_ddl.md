---
title: "Using DDL and Metadata"
layout: article
---

## Using DDL

DDL operations can be executed using the `db-do-commands` function. The general approach is:

    (j/db-do-commands db-spec sql-command-1 sql-command-2 .. sql-command-n)

The commands are executed as a single, batched statement, wrapped in a transaction. If you want to avoid the transaction, use this approach:

    (j/db-do-commands db-spec false sql-command-1 sql-command-2 .. sql-command-n)

### Creating tables

For the common operations of creating and dropping tables, java.jdbc provides a little assistance that recognizes `:entities` so you can use keywords (or strings) and have your chosen naming strategy applied, just as you can for several of the SQL functions.

    (j/create-table-ddl :fruit
                        [[:name "varchar(32)" :primary :key]
                         [:appearance "varchar(32)"]
                         [:cost :int]
                         [:grade :real]]
                        {:table-spec "ENGINE=InnoDB"
                         :entities clojure.string/upper-case})

Note that the vector of columns syntax is new in version 0.5.6. Earlier versions just accepted each column spec as a separate argument followed by the keyword arguments unrolled, but that was deprecated in 0.5.5 and will be removed in 0.6.0:

    (j/create-table-ddl :fruit
                        [:name "varchar(32)" :primary :key]
                        [:appearance "varchar(32)"]
                        [:cost :int]
                        [:grade :real]
                        :table-spec "ENGINE=InnoDB"
                        :entities clojure.string/upper-case)

This will generate:

    CREATE TABLE FRUIT
        (NAME varchar(32) primary key,
         APPEARANCE varchar(32),
         COST int,
         GRADE real) ENGINE=InnoDB

which you can pass to `db-do-commands`.

### Dropping tables

Similarly there is a `drop-table-ddl` function which takes a table name and an optional `:entities` option to generate DDL to drop a table.

    (j/drop-table-ddl :fruit) ; drop table fruit
    (j/drop-table-ddl :fruit {:entities clojure.string/upper-case}) ; drop table FRUIT

Prior to version 0.5.0, the following syntax was accepted (but it is now deprecated and will be removed in 0.6.0):

    (j/drop-table-ddl :fruit :entities clojure.string/upper-case)

This will generate:

    DROP TABLE FRUIT

## Accessing metadata

java.jdbc provides two functions for working with database metadata:

* `with-db-metadata` for creating an active metadata object backed by an open connection
* `metadata-result` for turning metadata results into Clojure data structures

For example (for versions prior to 0.3.3 you need to wrap the `metadata-result` with `doall`):

    (j/with-db-metadata [md db-spec]
      (j/metadata-result (.getTables md nil nil nil (into-array ["TABLE" "VIEW"]))))

This returns a sequence of maps describing all the tables and views in the current database. `metadata-result` only transforms `ResultSet` objects, other results are returned as-is. `metadata-result` can also accept an options map containing `:identifiers` and `:as-arrays?`, like the `query` function,
and those options control how the metatadata is transformed and/or returned.
