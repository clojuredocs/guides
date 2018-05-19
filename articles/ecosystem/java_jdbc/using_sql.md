---
title: "Manipulating data with SQL"
layout: article
---

## Contents

* [Overview][overview]
* [Using SQL][using-sql]
* [Using DDL][using-ddl]
* [Reusing Connections][reusing-connections]

## Using SQL

Here are some examples of using `java.jdbc` to manipulate data with SQL.
These examples assume a simple table called `fruit` (see [Using DDL and
Metadata](using_ddl.html)). These examples all assume the following in your
`ns` declaration:

```clojure
(:require [clojure.java.jdbc :as j])
```

## Reading and processing rows

`java.jdbc` provides a simple `query` function to allow you to read rows from
tables, as well as optionally performing processing on them at the same time.

### Reading rows

To obtain a fully realized result set as a sequence of maps, you can use
`query` with a vector containing the SQL string and any parameters needed by
the SQL:

```clojure
(j/query db-spec ["SELECT * FROM fruit"])
;; ({:id 1 :name "Apple" :appearance "red" :cost 59 :grade 87}
;;  {:id 2 :name "Banana" :appearance "yellow" :cost 29 :grade 92.2}
;;  ...)

(j/query db-spec ["SELECT * FROM fruit WHERE cost < ?" 50])
;; ({:id 2 :name "Banana" :appearance "yellow" :cost 29 :grade 92.2}
;;  ...)
```

You can also return the result set as a sequence of vectors. The first vector
will contain the column names, and each subsequent vector will represent a row
of data with values in the same order as the columns.

```clojure
(j/query db-spec ["SELECT * FROM fruit WHERE cost < ?" 50]
         {:as-arrays? true})
;; ([:id :name :appearance :cost :grade]
;;  [2 "Banana" "yellow" 29 92.2]
;;  ...)
```

### Processing a result set lazily

Since `query` returns a fully realized result set, it can be difficult to
process very large results. Fortunately, `java.jdbc` provides a number of ways to process a
large result set lazily while the connection is open, either by passing a function via
the `:result-set-fn` option or, since release 0.7.0, via `reducible-query`.

**`query` and `:result-set-fn`**

_If you are using release 0.7.0 or later, consider using `reducible-query` instead -- see below._

For `:result-set-fn`, the function you pass must force
realization of the result to avoid the connection closing while the result set
is still being processed. A `reduce`-based function is a good choice.

```clojure
(j/query db-spec ["SELECT * FROM fruit WHERE cost < ?" 50]
         {:result-set-fn (fn [rs]
                           (reduce (fn [total row-map]
                                     (+ total (:cost row-map)))
                           0 rs))})
;; produces the total cost of all the cheap fruits: 437
```

Of course, a simple sum like this could be computed directly in SQL instead:

```clojure
(j/query db-spec ["SELECT SUM(cost) FROM fruit WHERE cost < ?" 50]
         {:result-set-fn first})
;; {:sum(cost) 437}
```

We know we will only get one row back so passing `first` to `:result-set-fn` is
a quick way to get just that row.

Remember that if you also specify `:as-arrays? true`, your result set function
will be passed a sequence of vectors in which the first vector contains the
column names and subsequent vectors represent the values in the rows, matching
the order of the column names.

**`reducible-query`**

This is the recommended approach since release 0.7.0 but it does come with a few
restrictions:

You cannot use any of the following options that `query` accepts:
`as-arrays?`, `:explain`, `:explain-fn`, `:result-set-fn`, or `:row-fn`.

On the other hand, you have access to a much faster way to process result sets:
you can specify `:raw? true` and no conversion from Java's `ResultSet` to
Clojure's sequence of hash maps will be performed. In particular, it's as if you
specified `:identifiers identity :keywordize? false :qualifier nil`, and the
sequence representation of each row is not available. That means no `keys`,
no `vals`, no `seq` calls, just simple key lookup (for convenience, you can
still use keyword lookup for columns, but you can also call `get` with either a
string or a keyword).

So how does this work? `reducible-query` produces a `clojure.lang.IReduce` which,
when reduced with a function `f`, performs the query and reduces the `ResultSet`
using `f`, opening and closing the connection and/or transaction during the
reduction. For example:

```clojure
;; our reducing function requires two arguments: we must provide initial val
(reduce (fn [total {:keys [cost]}] (+ total cost))
        0
        (j/reducible-query db-spec
                           ["SELECT * FROM fruit WHERE cost < ?" 50]
                           {:raw? true}))
;; separating the key selection from the reducing function: we can omit val
(transduce (map :cost)
           + ; can be called with 0, 1, or 2 arguments!
           (j/reducible-query db-spec
                              ["SELECT * FROM fruit WHERE cost < ?" 50]
                              {:raw? true}))
;; 437
```

Since `reducible-query` doesn't actually run the query until you reduce its result,
you can create it once and run it as many times as you want. This will avoid the
overhead of option and parameter validation and handling for repeated reductions,
since those are performed just once in the call to `reducible-query`. Note that
the SQL parameters are fixed by that call, so this only works for running the
_identical_ query multiple times.

A reducible companion to `result-set-seq` also exists, in case you already have
a Java `ResultSet` and want to create a `clojure.lang.IReduce`. `reducible-result-set`
accept almost the same options as `result-set-seq`: `identifiers`, `keywordize?`,
`qualifier`, and `read-columns`. It does not accept `as-arrays?` (for the same
reason that `reducible-query` does not). Unlike `result-set-seq`, which produces
a lazy sequence that can be consumed multiple times (with the first pass realizing
it for subsequent passes), `reducible-result-set` is reducible just once: the
underlying `ResultSet` is mutable and is consumed during the first reduction!

It should go without saying that both `reducible-query` and
`reducible-result-set` respect `reduced` / `reduced?`.

**Additional Options?**

Note: some databases require additional options to be passed in to ensure that
result sets are chunked and lazy. In particular, you may need to pass
`:auto-commit?`, set appropriately, as an option to whichever function will open your database
connection (`with-db-connection`, `with-db-transaction`, or the `query` / `reducible-query` itself
if you are passing a bare database spec and expecting `query` / `reducible-query` to open and close
the connection directly). You may also need to specify `:fetch-size`, `:result-type`,
and possibly other options -- consult your database's documentation for the JDBC
driver you are using.

### Processing each row lazily

As seen above, using `reduce`, `transduce`, etc with a `reducible-query` allow
you to easily and efficiently process each row as you process the entire
result set, but sometimes you just want a sequence of transformed rows.

We can process each row with the `:row-fn` option. Again, like with `:result-set-fn`,
we pass a function but this time it will be
invoked on each row, as the result set is realized.

```clojure
(j/query db-spec ["SELECT name FROM fruit WHERE cost < ?" 50]
         {:row-fn :name})
;; ("Apple" "Banana" ...)
```

The result is still a fully realized sequence, but each row has been
transformed by the `:name` function you passed in.

You can combine this with `:result-set-fn` to simplify processing of result
sets:

```clojure
(j/query db-spec ["SELECT * FROM fruit WHERE cost < ?" 50]
         {:row-fn :cost
          :result-set-fn (partial reduce +)})
;; produces the total cost of all the cheap fruits
```

or:

```clojure
(j/query db-spec ["SELECT SUM(cost) AS total FROM fruit WHERE cost < ?" 50]
         {:row-fn :total
          :result-set-fn first})
;; produces the same result, via SQL
```

Here is an example that manipulates rows to add computed columns:

```clojure
(defn add-tax [row] (assoc row :tax (* 0.08 (:cost row))))

(j/query db-spec ["SELECT * FROM fruit"]
         {:row-fn add-tax})
;; produces all the rows with a new :tax column added
```

All of the above can be achieved via `reducible-query` and the appropriate
reducing function and/or transducer, but with those simple row/result set
functions, the result is often longer / uglier:

```clojure
(into [] (map :name) (j/reducible-query db-spec ["SELECT name FROM fruit WHERE cost < ?" 50]))
(transduce (map :cost) + (j/reducible-query db-spec ["SELECT * FROM fruit WHERE cost < ?" 50]))
;; :row-fn :total :result-set-fn first left as an exercise for the reader!
(into [] (map add-tax) (j/reducible-query db-spec ["SELECT * FROM fruit"]))
```

If the result set is likely to be large and the reduction can use a `:raw? true`
result set, `reducible-query` may be worth the verbosity for the performance gain.

## Inserting data

Rows (and partial rows) can be inserted easily using the `insert!` function.
You can insert a single row, or multiple rows. Depending on how you call
`insert!`, the insertion will be done either through multiple SQL statements or
through a single, batched SQL statement. That will also determine whether or
not you get back any generated keys.

### Inserting a row

If you want to insert a single row (or partial row) and get back the generated
keys, you can use `insert!` and specify the columns and their values as a map.
This performs a single insert statement. A single-element sequence containing a
map of the generated keys will be returned.

```clojure
(j/insert! db-spec :fruit {:name "Pear" :appearance "green" :cost 99})
;; returns a database-specific map as the only element of a sequence, e.g.,
;; ({:generated_key 50}) might be returned for MySQL
```

Not all databases are able to return generated keys from an insert.

### Inserting multiple rows

There are two ways to insert multiple rows: as a sequence of maps, or as a
sequence of vectors. In the former case, multiple inserts will be performed and
a map of the generated keys will be returned for each insert (as a sequence).
In the latter case, a single, batched insert will be performed and a sequence
of row insert counts will be returned (generally a sequence of ones).

If you use `insert-multi!` and specify each row as a map of columns and their values,
then you can specify a mixture of complete and partial rows, and you will get
back the generated keys for each row (assuming the database has that
capability).

```clojure
(j/insert-multi! db-spec :fruit
                 [{:name "Pomegranate" :appearance "fresh" :cost 585}
                  {:name "Kiwifruit" :grade 93}])
;; returns a sequence of database-specific maps, e.g., for MySQL:
;; ({generated_key 51} {generated_key 52})
```

If you use `insert-multi!` and specify the columns you wish to insert followed by
each row as a vector of column values, then you must specify the same columns
in each row, and you will not get generated keys back, just row counts. If you
wish to insert complete rows, you may omit the column name vector (passing
`nil` instead) but your rows must match the natural order of columns in your
table so be careful!

```clojure
(j/insert-multi! db-spec :fruit
                 nil ; column names not supplied
                 [[1 "Apple" "red" 59 87]
                  [2 "Banana" "yellow" 29 92.2]
                  [3 "Peach" "fuzzy" 139 90.0]
                  [4 "Orange" "juicy" 89 88.6]])
;; (1 1 1 1) - row counts modified
```

It is generally safer to specify the columns you wish to insert so you can
control the order, and choose to omit certain columns:

```clojure
(j/insert-multi! db-spec :fruit
                 [:name :cost]
                 [["Mango" 722]
                  ["Feijoa" 441]])
;; (1 1) - row counts modified
```

## Updating rows

If you want to update simple column values in one or more rows based on a
simple SQL predicate, you can use `update!` with a map, representing the column
values to set, and a SQL predicate with parameters. If you need a more complex
form of update, you can use the `execute!` function with arbitrary SQL (and
parameters).

```clojure
;; update fruit set cost = 49 where grade < ?
(j/update! db-spec :fruit
           {:cost 49}
           ["grade < ?" 75])
;; produces a sequence of the number of rows updated, e.g., (2)
```

For a more complex update:

```clojure
(j/execute! db-spec
            ["update fruit set cost = ( 2 * grade ) where grade > ?" 50.0])
;; produces a sequence of the number of rows updated, e.g., (3)
```

## Deleting rows

If you want to delete any rows from a table that match a simple predicate, the
`delete!` function can be used.

```clojure
(j/delete! db-spec :fruit ["grade < ?" 25.0])
;; produces a sequence of the number of rows deleted, e.g., (1)
```

You can also use `execute!` for deleting rows:

```clojure
(j/execute! db-spec ["DELETE FROM fruit WHERE grade < ?" 25.0])
;; produces a sequence of the number of rows deleted, e.g., (1)
```

## Using transactions

You can write multiple operations in a transaction to ensure they are either
all performed, or all rolled back.

```clojure
(j/with-db-transaction [t-con db-spec]
  (j/update! t-con :fruit
             {:cost 49}
             ["grade < ?" 75])
  (j/execute! t-con
              ["update fruit set cost = ( 2 * grade ) where grade > ?" 50.0]))
```

The `with-db-transaction` macro creates a transaction-aware connection from the
database specification and that should be used in the body of the transaction
code.

You can specify the transaction isolation level as part of the
`with-db-transction` binding:

```clojure
(j/with-db-transaction [t-con db-spec {:isolation :serializable}]
  ...)
```

Possible values for `:isolation` are `:none`, `:read-committed`,
`:read-uncommitted`, `:repeatable-read`, and `:serializable`. Be aware that not
all databases support all isolation levels.

In addition, you can also set the current transaction-aware connection to
rollback, and reset that setting, as well as test whether the connection is
currently set to rollback, using the following functions:

```clojure
(j/db-set-rollback-only! t-con)   ; this transaction will rollback instead of commit
(j/db-unset-rollback-only! t-con) ; this transaction will commit if successful
(j/db-is-rollback-only t-con)     ; returns true if transaction is set to rollback
```

## Updating or Inserting rows conditionally

`java.jdbc` does not provide a built-in function for updating existing rows or
inserting a new row (the older API supported this but the logic was too
simplistic to be generally useful). If you need that functionality, it can
sometimes be done like this:

```clojure
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
```

If the `where-clause` does not uniquely identify a single row, this will update
multiple rows which might not be what you want, so be careful!

## Exception Handling and Transaction Rollback

Transactions are rolled back if an exception is thrown, as shown in these
examples.

```clojure
(j/with-db-transaction [t-con db-spec]
  (j/insert-multi! t-con :fruit
                   [:name :appearance]
                   [["Grape" "yummy"]
                    ["Pear" "bruised"]])
  ;; At this point the insert! call is complete, but the transaction is
  ;; not. The exception will cause it to roll back leaving the database
  ;; untouched.
  (throw (Exception. "sql/test exception")))
```

As noted above, transactions can also be set explicitly to rollback instead of
commit:

```clojure
(j/with-db-transaction [t-con db-spec]
  (prn "is-rollback-only" (j/db-is-rollback-only t-con))
  ;; is-rollback-only false
  (j/db-set-rollback-only! t-con)
  ;; the following insert will be rolled back when the transaction ends:
  (j/insert!-multi t-con :fruit
                   [:name :appearance]
                   [["Grape" "yummy"]
                    ["Pear" "bruised"]])
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
```

## Clojure identifiers and SQL entities

As hinted at above, `java.jdbc` converts SQL entity names in result sets to
keywords in Clojure by making them lowercase, and converts strings and keywords
that specify table and column names (in maps) to SQL entities *as-is* by
default.

You can override this behavior by specifying an options map, containing
`:identifiers` on the `query` and `metadata-result` functions or `:entities` on
the `delete!`, `insert!`, `update!`, `create-table-ddl`, and `drop-table-ddl`
functions.

* `:identifiers` is for converting `ResultSet` column names to keywords. It
  defaults to `clojure.string/lower-case`.
* `:entities` is for converting Clojure keywords/string to SQL entity names. It
  defaults to `identity`.

If you want to prevent `java.jdbc`'s conversion of SQL entity names to lowercase
in a `query` result, you can specify `:identifiers identity`:

```clojure
(j/query db-spec ["SELECT * FROM mixedTable"]
         {:identifiers identity})
;; produces result set with column names exactly as they appear in the DB
```

It you're working with a database that has underscores in column names, you
might want to specify a function that converts those to dashes in Clojure
keywords:

```clojure
(j/query db-spec ["SELECT * FROM mixedTable"]
         {:identifiers #(.replace % \_ \-)})
```

For several databases, you will often want entities to be quoted in some way
(sometimes referred to as "stropping"). A utility function `quoted` is provided
that accepts either a single character, a vector pair of characters, or a keyword
as a symbolic name for the type of quoting you want (`:ansi`, `:mysql`,
`:oracle`, `:sqlserver`), and
returns a function suitable for use with the `:entities` option.

For example:

```clojure
(j/insert! db-spec :fruit
           {:name "Apple" :appearance "Round" :cost 99}
           {:entities (j/quoted \`)}) ; or (j/quoted :mysql)
```

will execute:

```clojure
INSERT INTO `fruit` ( `name`, `appearance`, `cost` )
    VALUES ( ?, ?, ? )
```

with the parameters `"Apple", "Round", "99"` whereas:

```clojure
(j/insert! db-spec :fruit
           {:name "Apple" :appearance "Round" :cost 99}
           {:entities (j/quoted [\[ \]])}) ; or (j/quoted :sqlserver)
```

will execute:

```clojure
INSERT INTO [fruit] ( [name], [appearance], [cost] )
    VALUES ( ?, ?, ? )
```

with the parameters `"Apple", "Round", "99"`.

## Protocol extensions for transforming values

By default, `java.jdbc` leaves it up to Java interop and the JDBC driver library
to perform the appropriate transformations of Clojure values to SQL values and
vice versa. When Clojure values are passed through to the JDBC driver,
`java.jdbc` uses `PreparedStatement/setObject` for all values by default. When
Clojure values are read from a `ResultSet` they are left untransformed, except
that `Boolean` values are coerced to canonical `true` / `false` values in
Clojure (some driver / data type combinations can produce `(Boolean. false)`
values otherwise, which do not behave like `false` in all situations).

`java.jdbc` provides three protocols that you can extend, in order to modify
these behaviors.

* `ISQLValue` / `sql-value` - simple transformations of Clojure values to SQL
  values
* `ISQLParameter` / `set-parameter` - a more sophisticated transformation of
  Clojure values to SQL values that lets you override how the value is stored
  in the `PreparedStatement`
* `IResultSetReadColumn` / `result-set-read-column` - simple transformations of
  SQL values to Clojure values when processing a `ResultSet` object

If you are using a database that returns certain SQL types as custom Java types
(e.g., PostgreSQL), you can extend `IResultSetReadColumn` to that type and
define `result-set-read-column` to perform the necessary conversion to a usable
Clojure data structure. The `result-set-read-column` function is called with
three arguments:

* The SQL value itself
* The `ResultSet` metadata object
* The index of the column in the row / metadata

By default `result-set-read-column` just returns its first argument (the
`Boolean` implementation ensure the result is either `true` or `false`).

If you are using a database that requires special treatment of null values,
e.g., TeraData, you can extend `ISQLParameter` to `nil` (and `Object`) and
define `set-parameter` to use `.setNull` instead of `.setObject`. The
`set-parameter` function is called with three arguments:

* The Clojure value itself
* The `PreparedStatement` object
* The index of the parameter being set

By default `set-parameter` calls `sql-value` on the Clojure value and then
calls `.setObject` to store the result of that call into the specified
parameter in the SQL statement.

For general transformations of Clojure values to SQL values, extending
`ISQLValue` and defining `sql-value` may be sufficient. The `sql-value`
function is called with a single argument: the Clojure value. By default
`sql-value` just returns its argument.

[overview]: home.html
[using-sql]: using_sql.html
[using-ddl]: using_ddl.html
[reusing-connections]: reusing_connections.html
