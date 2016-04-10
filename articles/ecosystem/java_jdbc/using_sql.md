---
title: "Manipulating data with SQL"
layout: article
---

Here are some examples of using java.jdbc to manipulate data with SQL.
These examples assume a simple table called fruit (see [Using DDL and Metadata](using_ddl.html)). These examples all assume the following in your `ns` declaration:

    (:require [clojure.java.jdbc :as j])

## Reading and processing rows

java.jdbc provides a simple `query` function to allow you to read rows from tables, as well as optionally performing processing on them at the same time.

### Reading rows

To obtain a fully realized result set as a sequence of maps, you can use `query` with a vector containing the SQL string and any parameters needed by the SQL:

    (j/query db-spec ["SELECT * FROM fruit"])
    ;; ({:id 1 :name "Apple" :appearance "red" :cost 59 :grade 87}
    ;;  {:id 2 :name "Banana" :appearance "yellow" :cost 29 :grade 92.2}
    ;;  ...)

    (j/query db-spec ["SELECT * FROM fruit WHERE cost < ?" 50])
    ;; ({:id 2 :name "Banana" :appearance "yellow" :cost 29 :grade 92.2}
    ;;  ...)

You can also return the result set as a sequence of vectors. The first vector will contain the column names, and each subsequent vector will represent a row of data with values in the same order as the columns.

    (j/query db-spec ["SELECT * FROM fruit WHERE cost < ?" 50]
             {:as-arrays? true})
    ;; ([:id :name :appearance :cost :grade]
    ;;  [2 "Banana" "yellow" 29 92.2]
    ;;  ...)

Note: prior to version 0.5.5 options could be specified as top-level (unrolled) keyword/value arguments but that does not compose well and was deprecated in 0.5.5 (and support will be removed in 0.6.0).

### Processing a result set lazily

Since `query` returns a fully realized result set, it can be difficult to process very large results. Fortunately java.jdbc provides a way to process a large result set lazily while the connection is open, by passing a function via the `:result-set-fn` option. Note that the function you pass must force realization of the result to avoid the connection closing while the result set is still being processed. A `reduce`-based function is a good choice.

    (j/query db-spec ["SELECT * FROM fruit WHERE cost < ?" 50]
             {:result-set-fn (fn [rs]
                               (reduce (fn [total row-map]
                                         (+ total (:cost row-map)))
                               0 rs))})
    ;; produces the total cost of all the cheap fruits

Of course, a simple sum like this could be computed directly in SQL instead:

    (j/query db-spec ["SELECT SUM(cost) FROM fruit WHERE cost < ?" 50]
             {:result-set-fn first})
    ;; {:sum(cost) 437}

We know we will only get one row back so passing `first` to `:result-set-fn` is a quick way to get just that row.

With `:result-set-fn`, we can process very large result sets because the rows are fetched from the database in chunks, as your function realizes the result set sequence.

Remember that if you also specify `:as-arrays? true`, your result set function will be passed a sequence of vectors in which the first vector contains the column names and subsequent vectors represent the values in the rows, matching the order of the column names.

### Processing each row lazily

In addition to processing the entire result set, we can also process each row with the `:row-fn` option. Again, we pass a function but this time it will be invoked on each row, as the result set is realized.

    (j/query db-spec ["SELECT name FROM fruit WHERE cost < ?" 50]
             {:row-fn :name})
    ;; ("Apple" "Banana" ...)

The result is still a fully realized sequence, but each row has been transformed by the `:name` function you passed in.

You can combine this with `:result-set-fn` to simplify processing of result sets:

    (j/query db-spec ["SELECT * FROM fruit WHERE cost < ?" 50]
             {:row-fn :cost
              :result-set-fn (partial reduce +)})
    ;; produces the total cost of all the cheap fruits

or:

    (j/query db-spec ["SELECT SUM(cost) AS total FROM fruit WHERE cost < ?" 50]
             {:row-fn :total
              :result-set-fn first})
    ;; produces the same result, via SQL

Here is an example that manipulates rows to add computed columns:

    (defn add-tax [row] (assoc row :tax (* 0.08 (:cost row))))
    
    (j/query db-spec ["SELECT * FROM fruit"]
             {:row-fn add-tax})
    ;; produces all the rows with a new :tax column added

## Inserting data

Rows (and partial rows) can be inserted easily using the `insert!` function. You can insert a single row, or multiple rows. Depending on how you call `insert!`, the insertion will be done either through multiple SQL statements or through a single, batched SQL statement. That will also determine whether or not you get back any generated keys.

### Inserting a row

If you want to insert a single row (or partial row) and get back the generated keys, you can use `insert!` and specify the columns and their values as a map. This performs a single insert statement. A single-element sequence containing a map of the generated keys will be returned.

    (j/insert! db-spec :fruit {:name "Pear" :appearance "green" :cost 99})
    ;; returns a database-specific map as the only element of a sequence, e.g.,
    ;; ({:generated_key 50}) might be returned for MySQL

Not all databases are able to return generated keys from an insert.

### Inserting multiple rows

There are two ways to insert multiple rows: as a sequence of maps, or as a sequence of vectors. In the former case, multiple inserts will be performed and a map of the generated keys will be returned for each insert (as a sequence). In the latter case, a single, batched insert will be performed and a sequence of row insert counts will be returned (generally a sequence of ones).

If you use `insert!` and specify each row as a map of columns and their values, then you can specify a mixture of complete and partial rows, and you will get back the generated keys for each row (assuming the database has that capability).

    (j/insert! db-spec :fruit
               {:name "Pomegranate" :appearance "fresh" :cost 585}
               {:name "Kiwifruit" :grade 93})
    ;; returns a sequence of database-specific maps, e.g., for MySQL:
    ;; ({generated_key 51} {generated_key 52})

If you use `insert!` and specify the columns you wish to insert followed by each row as a vector of column values, then you must specify the same columns in each row, and you will not get generated keys back, just row counts. If you wish to insert complete rows, you may omit the column name vector (passing `nil` instead) but your rows must match the natural order of columns in your table so be careful!

      (j/insert! db-spec :fruit
                 nil ; column names not supplied
                 [1 "Apple" "red" 59 87]
                 [2 "Banana" "yellow" 29 92.2]
                 [3 "Peach" "fuzzy" 139 90.0]
                 [4 "Orange" "juicy" 89 88.6])
    ;; (1 1 1 1) - row counts modified

It is generally safer to specify the columns you wish to insert so you can control the order, and choose to omit certain columns:

    (j/insert! db-spec :fruit
               [:name :cost]
               ["Mango" 722]
               ["Feijoa" 441])
    ;; (1 1) - row counts modified

## Updating rows

If you want to update simple column values in one or more rows based on a simple SQL predicate, you can use `update!` with a map, representing the column values to set, and a SQL predicate with parameters. If you need a more complex form of update, you can use the `execute!` function with arbitrary SQL (and parameters).

    ;; update fruit set cost = 49 where grade < ?
    (j/update! db-spec :fruit
               {:cost 49}
               ["grade < ?" 75])
    ;; produces a sequence of the number of rows updated, e.g., (2)

For a more complex update:

    (j/execute! db-spec
                ["update fruit set cost = ( 2 * grade ) where grade > ?" 50.0])
    ;; produces a sequence of the number of rows updated, e.g., (3)

## Deleting rows

If you want to delete any rows from a table that match a simple predicate, the `delete!` function can be used.

    (j/delete! db-spec :fruit ["grade < ?" 25.0])
    ;; produces a sequence of the new of rows deleted, e.g., (1)

You can also use `execute!` for deleting rows:

    (j/execute! db-spec ["DELETE FROM fruit WHERE grade < ?" 25.0])
    ;; produces a sequence of the new of rows deleted, e.g., (1)

## Using transactions

You can write multiple operations in a transaction to ensure they are either all performed, or all rolled back.

    (j/with-db-transaction [t-con db-spec]
      (j/update! t-con :fruit
                 {:cost 49}
                 ["grade < ?" 75])
      (j/execute! t-con
                  ["update fruit set cost = ( 2 * grade ) where grade > ?" 50.0]))

The `with-db-transaction` macro creates a transaction-aware connection from the database specification and that should be used in the body of the transaction code.

You can specify the transaction isolation level as part of the `with-db-transction` binding:

    (j/with-db-transaction [t-con db-spec {:isolation :serializable}]
      ...)

Possible values for `:isolation` are `:none`, `:read-committed`, `:read-uncommitted`, `:repeatable-read`, and `:serializable`. Be aware that not all databases support all isolation levels.

Note: prior to version 0.5.5, the `:isolation` option could be specified without `{ }` but that was deprecated in 0.5.5 (and will be removed in 0.6.0).

In addition, you can also set the current transaction-aware connection to rollback, and reset that setting, as well as test whether the connection is currently set to rollback, using the following functions:

    (j/db-set-rollback-only! t-con)   ; this transaction will rollback instead of commit
    (j/db-unset-rollback-only! t-con) ; this transaction commit if successful
    (j/db-is-rollback-only t-con)     ; returns true if transaction is set to rollback

## Updating or Inserting rows conditionally

java.jdbc does not provide a built-in function for updating existing rows or inserting a new row (the older API supported this but the logic was too simplistic to be generally useful). If you need that functionality, it can easily be done like this:

    (defn update-or-insert!
      "Updates columns or inserts a new row in the specified table"
      [db table row where-clause]
      (j/with-db-transaction [t-con db]
        (let [result (j/update! t-con table row where-clause)]
          (if (zero? (first result))
            (j/insert! t-con table row)
            result))))
    
    (update-or-insert! mysql-db :fruit
                       {:name "Cactus" :appearance "Spiky" :cost 2000}
                       ["name = ?" "Cactus"])
    ;; inserts Cactus (assuming none exists)
    (update-or-insert! mysql-db :fruit
                       {:name "Cactus" :appearance "Spiky" :cost 2500}
                       ["name = ?" "Cactus"])
    ;; updates the Cactus we just inserted

If the `where-clause` does not uniquely identify a single row, this will update multiple rows which might not be what you want, so be careful!

## Exception Handling and Transaction Rollback

Transactions are rolled back if an exception is thrown, as shown in these examples.

    (j/with-db-transaction [t-con db-spec]
      (j/insert! t-con :fruit
                 [:name :appearance]
                 ["Grape" "yummy"]
                 ["Pear" "bruised"])
      ;; At this point the insert! call is complete, but the transaction is
      ;; not. The exception will cause it to roll back leaving the database
      ;; untouched.
      (throw (Exception. "sql/test exception")))

As noted above, transactions can also be set explicitly to rollback instead of commit:

    (j/with-db-transaction [t-con db-spec]
      (prn "is-rollback-only" (j/db-is-rollback-only t-con))
      ;; is-rollback-only false
      (j/db-set-rollback-only! t-con)
      ;; the following insert will be rolled back when the transaction ends:
      (j/insert! t-con :fruit
                 [:name :appearance]
                 ["Grape" "yummy"]
                 ["Pear" "bruised"])
      (prn "is-rollback-only" (j/db-is-rollback-only t-con))
      ;; is-rollback-only true
      ;; the following will display the inserted rows:
      (j/query t-con ["SELECT * FROM fruit"]
               :row-fn println))
    (prn)
    ;; outside the transaction, the following will show the original rows
    ;; without those two inserted inside the (rolled-back) transaction:
    (j/query db-spec ["SELECT * FROM fruit"]
             :row-fn println)

## Clojure identifiers and SQL entities

As hinted at above, java.jdbc converts SQL entity names in result sets to keywords in Clojure by making them lowercase, and converts strings and keywords that specify table and column names (in maps) to SQL entities *as-is* by default.

You can override this behavior by specifying an options map, containing `:identifiers` on the `query` and `metadata-result` functions or `:entities` on the `delete!`, `insert!`, `update!`, `create-table-ddl`, and `drop-table-ddl` functions.

* `:identifiers` is for converting `ResultSet` column names to keywords. It defaults to `clojure.string/lower-case`.
* `:entities` is for converting Clojure keywords/string to SQL entity names. It defaults to `identity`.

If you want to prevent java.jdbc's conversion of SQL entity names to lowercase in a `query` result, you can specify `:identifiers identity`:

    (j/query db-spec ["SELECT * FROM mixedTable"]
             {:identifiers identity})
    ;; produces result set with column names exactly as they appear in the DB

It you're working with a database that has underscores in column names, you might want to specify a function that converts those to dashes in Clojure keywords:

    (j/query db-spec ["SELECT * FROM mixedTable"]
             {:identifiers #(.replace % \_ \-)})

For several databases, you will often want entities to be quoted in some way (sometimes referred to as "stropping"). A utility function `quoted` is provided that accepts either a single character or a vector pair of characters, and returns a function suitable for use with the `:entities` option.

For example:

    (j/insert! db-spec :fruit
               {:name "Apple" :appearance "Round" :cost 99}
               :options {:entities (j/quoted \`)})

will execute:

    INSERT INTO `fruit` ( `name`, `appearance`, `cost` )
        VALUES ( ?, ?, ? )

with the parameters `"Apple", "Round", "99"` whereas:

    (j/insert! db-spec :fruit
               {:name "Apple" :appearance "Round" :cost 99}
               :options {:entities (j/quoted [\[ \]])})

will execute:

    INSERT INTO [fruit] ( [name], [appearance], [cost] )
        VALUES ( ?, ?, ? )

with the parameters `"Apple", "Round", "99"`.

Note that `insert!` and `create-table-ddl` are the only functions in version 0.5.5 that require `:options` as a "flag" to introduce the options map. In all the other functions, the options map is simply the last argument in the call (and can be omitted when the defaults are acceptable).

## Protocol extensions for transforming values

By default, java.jdbc leaves it up to Java interop and the JDBC driver library to perform the appropriate transformations of Clojure values to SQL values and vice versa. When Clojure values are passed through to the JDBC driver, java.jdbc uses `PreparedStatement/setObject` for all values by default. When Clojure values are read from a `ResultSet` they are left untransformed, except that `Boolean` values are coerced to canonical `true` / `false` values in Clojure (some driver / data type combinations can produce `(Boolean. false)` values otherwise, which do not behave like `false` in all situations).

java.jdbc provides three protocols that you can extend, in order to modify these behaviors.

* `ISQLValue` / `sql-value` - simple transformations of Clojure values to SQL values
* `ISQLParameter` / `set-parameter` - a more sophisticated transformation of Clojure values to SQL values that lets you override how the value is stored in the `PreparedStatement`
* `IResultSetReadColumn` / `result-set-read-column` - simple transformations of SQL values to Clojure values when processing a `ResultSet` object

If you are using a database that returns certain SQL types as custom Java types (e.g., PostgreSQL), you can extend `IResultSetReadColumn` to that type and define `result-set-read-column` to perform the necessary conversion to a usable Clojure data structure. The `result-set-read-column` function is called with three arguments:

* The SQL value itself
* The `ResultSet` metadata object
* The index of the column in the row / metadata

By default `result-set-read-column` just returns its first argument (the `Boolean` implementation ensure the result is either `true` or `false`).

If you are using a database that requires special treatment of null values, e.g., TeraData, you can extend `ISQLParameter` to `nil` (and `Object`) and define `set-parameter` to use `.setNull` instead of `.setObject`. The `set-parameter` function is called with three arguments:

* The Clojure value itself
* The `PreparedStatement` object
* The index of the parameter being set

By default `set-parameter` calls `sql-value` on the Clojure value and then calls `.setObject` to store the result of that call into the specified parameter in the SQL statement.

For general transformations of Clojure values to SQL values, extending `ISQLValue` and defining `sql-value` may be sufficient. The `sql-value` function is called with a single argument: the Clojure value. By default `sql-value` just returns its argument.
