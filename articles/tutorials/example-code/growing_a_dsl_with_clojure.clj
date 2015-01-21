(ns dsl-test.core)


(defmacro with-implementation
    [impl & body]
    `(binding [*current-implementation* ~impl]
           ~@body))

(defmacro script [form]
    `(emit '~form))


(def ^{:dynamic true}
   ;; The current script language implementation to generate
    *current-implementation*)

(derive ::bash ::common)


(defmulti emit
  (fn [form]
        [*current-implementation* (class form)]))

(defmethod emit [::common java.lang.String]
    [form]
    form)

(defmethod emit [::common java.lang.Long]
    [form]
    (str form))

(defmethod emit [::common java.lang.Double]
    [form]
    (str form))

(defmethod emit [::bash clojure.lang.PersistentList]
    [form]
    (case (name (first form))
          "println" (str "echo " (second form))
          nil))


(defn -main
  [& args]

  (println (with-implementation ::bash (script

    (println "a")

  )))

)

