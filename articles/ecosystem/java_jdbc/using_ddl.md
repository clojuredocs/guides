---
title: "Manipulating tables with DDL"
layout: article
---

Currently you can create and drop tables using clojure.java.jdbc. To see how to manipulate data with SQL, see [Manipulating data with SQL](using_sql.html).

For DDL operations, use the *db-do-commands* function, passing in the db-spec and the SQL string to be executed.

## Creating a table
To create a table, use *create-table* to generate the DDL with the table name and a vector for each column spec. Currently, table-level specifications are not supported.

    (ns ddl-example
      (:require [clojure.java.jdbc :as jdbc]
                [clojure.java.jdbc.ddl :as ddl]))
    
    (def db-spec ... )
    
    (defn create-fruit
      "Create a table"
      [db-spec]
      (jdbc/db-do-commands db-spec
        (ddl/create-table
          :fruit
          [:name "varchar(32)" "PRIMARY KEY"]
          [:appearance "varchar(32)"]
          [:cost :int]
          [:grade :real])))

## Dropping a table
To drop a table, use *drop-table* to generate the DDL with the table name.

    (defn drop-fruit
      "Drop a table"
      [db-spec]
      (try
        (jdbc/db-do-commands db-spec
          (ddl/drop-table :fruit))
        (catch Exception _)))

## Accessing table metadata
To retrieve the metadata for a table, you can operate on the connection itself. In future, functions may be added to make this easier.

    (defn db-get-tables
      "Demonstrate getting table info"
      [db]
      (into []
            (j/result-set-seq
               (-> (j/get-connection db)
                   (.getMetaData)
                   (.getTables nil nil nil (into-array ["TABLE" "VIEW"]))))))
