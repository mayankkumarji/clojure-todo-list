(ns todo.item.handler_datomic
  (:require [datomic.api :as d]))

(def conn nil)

(defn handle-create-todo [title description]
  @(d/transact conn [{:db/id (d/tempid :db.part/items)
                      :todo/title title
                      :todo/description description}]))

(defn handle-get-todo [title]
  (ffirst (d/q '[:find ?eid
                 :in $ ?title
                 :where [?eid :todo/title ? title]]
               (d/db conn)
               title)))


(defn handle-todos-list []
  (d/q '[:find ?title
         :where [_ :todo/title ?title]]
       (d/db conn)))
