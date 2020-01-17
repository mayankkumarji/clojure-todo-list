(ns todo.core
(:require [todo.item.model :as items]
          [todo.item.handler :refer [handle-index-items
                                     handle-create-item
                                     handle-delete-item
                                     handle-update-item]]
          [todo.item.handler_datomic :refer [handle-todos-list
                                     handle-create-todo
                                     handle-get-todo]])
(:require [datomic.api :as d])
(:require [ring.adapter.jetty :as jetty]
          [ring.middleware.reload :refer [wrap-reload]]
          [ring.middleware.params :refer [wrap-params]]
          [ring.middleware.resource :refer [wrap-resource]]
          [ring.middleware.file-info :refer [wrap-file-info]]
          [compojure.core :refer [defroutes ANY GET POST PUT DELETE ]]
          [compojure.route :refer [not-found]]
          [ring.handler.dump :refer [handle-dump]]))


(def uri "datomic:free://localhost:4334/todo")

(defn create-empty-in-memory-db []
(let [uri "datomic:free://localhost:4334/todo"]
  (d/delete-database uri)
  (d/create-database uri)
  (let [conn (d/connect uri)
        schema (load-file "resources/datomic/schema.edn")]
    (d/transact conn schema)
    conn)))

(def conn (d/connect uri))
(def db "jdbc:postgresql://localhost/todo?user=postgres&password=postgres")
(create-empty-in-memory-db)

(defn greet [req]

{
  :status 200
  :body "Hello, World"
  :headers {}
})

(defn goodbye [req]
{
  :status 200
  :body "Goodbye, Cruel world!"
  :headers {}
})

(defn about [req]
{
  :status 200
  :body "This is our first clojure application!"
  :headers {}
})

(defn yo [req]
(let [name (get-in req [:route-params :name])]
{
  :status 200
  :body (str "yo! " name "!")
  :headers {}
}))

(def ops
{"+" +
"-" -
"*" *
":" /})

(defn calc [req]
(let [a (Integer. (get-in req [:route-params :a]))
      b (Integer. (get-in req [:route-params :b]))
      op (get-in req [:route-params :op])
      f (get ops op)
]
(if f {
  :status 200
  :body (str (f a b))
  :headers {}
}{
  :status 404
  :body (str "Unknown operator: " op)
  :headers {}
})
))

; (defn request [req]
; {
;   :status 200
;   :body (pr-str req)
;   :headers {}
; })

(defroutes routes
(GET "/" [] handle-index-items)
(GET "/goodbye" [] goodbye)
(GET "/yo/:name" [] yo)
(GET "/about" [] about)
(ANY "/request" [] handle-dump)
(GET "/calc/:a/:op/:b" [] calc)
(GET "/items" [] handle-index-items)
(POST "/items" [] handle-create-item)
(DELETE "/items/:item-id" [] handle-delete-item)
(PUT "/items/:item-id" [] handle-update-item)
(not-found "Page not found.")
(GET "/todo" [] handle-todos-list)
(GET "/todo/:todo-id" [] handle-get-todo)
(POST "/todo" [] handle-create-todo))

(defn wrap-db [hdlr]
(fn [req]
(hdlr (assoc req :todo/db db))))

(defn wrap-server [hdlr]
(fn [req]
(assoc-in(hdlr req)[:headers "Server"] "Listronica 9000")))

(def sim-method {"PUT" :put
                  "DELETE" :delete})

(defn wrap-simulated-methods [hdlr]
(fn [req]
(if-let [method (and (= :post (:request-method req))
(sim-method (get-in req [:params "_method"])))]
(hdlr (assoc req :request-method method))
(hdlr req))))

(def app (wrap-server (wrap-file-info (wrap-resource (wrap-db (wrap-params (wrap-simulated-methods routes))) "static"))))

(defn -main [port]
(items/create-table db)
(jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
(items/create-table db)
(jetty/run-jetty (wrap-reload #'app){:port (Integer. port)}))
