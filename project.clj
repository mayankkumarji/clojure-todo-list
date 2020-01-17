(defproject todo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [ring "1.8.0"]
                 [compojure "1.6.1"]
                 [com.datomic/datomic-free "0.9.5697"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.postgresql/postgresql "42.2.9"]
                 [hiccup "1.0.5"]]
  :repl-options {:init-ns todo.core}
  :main todo.core
  :datomic {:schemas ["resources/datomic" ["schema.edn"]]}
  :profiles {:dev
             {:main todo.core/-dev-main
              :datomic {:config "resources/datomic/free-transactor-template.properties"
                        :db-uri "datomic:free://localhost:4334/todo"}}})
