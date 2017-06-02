---
title: "How to use connection pooling"
layout: article
---

java.jdbc does not provide connection pooling directly but it is relatively
easy to add to your project. The following example shows how to configure
connection pooling use c3p0. Below, we show how to use BoneCP instead.

## Add the c3p0 dependency

For more information on c3p0, consult the [c3p0
documentation](http://www.mchange.com/projects/c3p0/).

If you're using Leiningen, you can just add the following to your dependencies:

    [com.mchange/c3p0 "0.9.2.1"]

In Maven, it would be:

    <dependency>
      <groupId>com.mchange</groupId>
      <artifactId>c3p0</artifactId>
      <version>0.9.2.1</version>
    </dependency>

## Create the pooled datasource from your db-spec

Define your `db-spec` as usual, for example (for MySQL):

    (def db-spec
      {:classname "com.mysql.jdbc.Driver"
       :subprotocol "mysql"
       :subname "//127.0.0.1:3306/mydb"
       :user "myaccount"
       :password "secret"})

Import the c3p0 class as part of your namespace declaration, for example:

    (ns example.db
      (:import com.mchange.v2.c3p0.ComboPooledDataSource))

Define a function that creates a pooled datasource:

    (defn pool
      [spec]
      (let [cpds (doto (ComboPooledDataSource.)
                   (.setDriverClass (:classname spec))
                   (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
                   (.setUser (:user spec))
                   (.setPassword (:password spec))
                   ;; expire excess connections after 30 minutes of inactivity:
                   (.setMaxIdleTimeExcessConnections (* 30 60))
                   ;; expire connections after 3 hours of inactivity:
                   (.setMaxIdleTime (* 3 60 60)))]
        {:datasource cpds}))

Now you can create a single connection pool:

    (def pooled-db (delay (pool db-spec)))

    (defn db-connection [] @pooled-db)

And then call `(db-connection)` wherever you need access to it. If you're using
a component lifecycle for your application, such as Stuart Sierra has
advocated, you won't need `pooled-db` or `db-connection`, you'll just create
`(pool db-spec)` as part of your application's initialization and pass it
around as part of your system configuration.

## Add the BoneCP dependencies

For more information on BoneCP, consult the [BoneCP
documentation](http://jolbox.com).

If you're using Leiningen, you can just add the following to your dependencies:

    [com.jolbox/bonecp "0.7.1.RELEASE"]

You will also need to add a dependency for the SLF4J adapter that matches the
logging system you use. For example, for `log4j` and the `"0.7.1.RELEASE"` of
BoneCP, you would need to add:

    [org.slf4j/slf4j-log4j12 "1.5.0"]

The adapter version must match the version of SLF4J that BoneCP brings in as a
transitive dependency. Note: BoneCP also brings in Guava as a dependency.

In Maven, it would be:

    <dependency>
      <groupId>com.jolbox</groupId>
      <artifactId>bonecp</artifactId>
      <version>0.7.1.RELEASE</version>
    </dependency>

and whatever logging system you chose.

## Create the pooled datasource from your db-spec

Define your `db-spec` as usual, for example (for MySQL):

    (def db-spec
      {:classname "com.mysql.jdbc.Driver"
       :subprotocol "mysql"
       :subname "//127.0.0.1:3306/mydb"
       :user "myaccount"
       :password "secret"})

Import the BoneCP class as part of your namespace declaration, for example:

    (ns example.db
      (:import com.jolbox.bonecp.BoneCPDataSource))

Define a function that creates a pooled datasource:

    (defn pool
      [spec]
      (let [partitions 3
            cpds (doto (BoneCPDataSource.)
                   (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
                   (.setUsername (:user spec))
                   (.setPassword (:password spec))
                   (.setMinConnectionsPerPartition (inc (int (/ min-pool partitions))))
                   (.setMaxConnectionsPerPartition (inc (int (/ max-pool partitions))))
                   (.setPartitionCount partitions)
                   (.setStatisticsEnabled true)
                   ;; test connections every 25 mins (default is 240):
                   (.setIdleConnectionTestPeriodInMinutes 25)
                   ;; allow connections to be idle for 3 hours (default is 60 minutes):
                   (.setIdleMaxAgeInMinutes (* 3 60))
                   ;; consult the BoneCP documentation for your database:
                   (.setConnectionTestStatement "/* ping *\\/ SELECT 1"))]
        {:datasource cpds}))

Now you can create a single connection pool:

    (def pooled-db (delay (pool db-spec)))

    (defn db-connection [] @pooled-db)

And then call `(db-connection)` wherever you need access to it. As above,
adjust for your application lifecycle.
