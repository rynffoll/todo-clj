(ns todo_clj.api
  (:require [compojure.core :refer :all]
            [todo_clj.db :as db]))

(defn get-todos []
  (db/get-todos))

(defn get-todo-by-id [id]
  (db/get-todo-by-id id))

(defn create-todo [todo]
  (db/create-todo todo))

(defn update-todo [id todo]
  (db/update-todo id todo))

(defn delete-todo-by-id [id]
  (db/delete-todo-by-id id))

(defroutes todo-routes
  (GET "/todos" [] (get-todos))
  (GET "/todos/:id" [id] (get-todo-by-id id))
  (POST "/todos" {todo :body} (create-todo todo))
  (PUT "/todos/:id" [id :as {todo :body}] (update-todo id todo))
  (DELETE "/todos/:id" [id] (delete-todo-by-id id)))
