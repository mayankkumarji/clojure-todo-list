(ns todo.item.handler
(:require [todo.item.model :refer [create-item read-items update-item delete-item]]
            [todo.item.view :refer [items-page]]))

(defn handle-index-items [req]
(let [db (:todo/db req)
      items (read-items db)]
      {
          :status 200
          :headers {}
          :body (items-page items)
        ;   (str "<html><head></head><body><div>"
        ;   (mapv :name items)
        ;   "</div><form method =\"POST\" action=\"/items\">"
        ;   "<input type=\"text\" name= \"name\" placeholder=\"name\">"
        ;   "<input type=\"text\" name= \"description\" placeholder=\"description\">"
        ;   "<input type=\"submit\">"
        ;   "</form</body></html>")
      }))

(defn handle-create-item [req]
(let [name (get-in req [:params "name"])
      description (get-in req [:params "description"])
      db (:todo/db req)
      item-id (create-item db name description)]
      {:status 302
      :headers {"Location" "/items"}
      :body ""}))

(defn handle-delete-item [req]
(let [db (:todo/db req)
      item-id (java.util.UUID/fromString (:item-id (:route-params req)))
      exists? (delete-item db item-id)]
      (if exists?
      {:status 302
      :headers {"Location" "/items"}
      :body ""}
      {:status 404
      :headers {}
      :body "Item not found."})))


(defn handle-update-item [req]
(let [db (:todo/db req)
      item-id (java.util.UUID/fromString (:item-id (:route-params req)))
      checked (get-in req [:params "checked"])
      exists? (update-item db item-id (= "true" checked))]
      (if exists?
      {:status 302
      :headers {"Location" "/items"}
      :body ""}
      {:status 404
      :headers {}
      :body "Item not found."})))