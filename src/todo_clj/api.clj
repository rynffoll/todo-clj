(ns todo_clj.api
  (:gen-class)
  (:require [compojure.core :refer :all]
            [ring.logger :as logger]
            [ring.middleware.json :as json]
            [todo_clj.db :as db]
            [iapetos.collector.ring :as ring]))

(defn get-todos [db]
  (db/get-todos db))

(defn get-todo-by-id [db id]
  (db/get-todo-by-id db id))

(defn create-todo [db todo]
  (println todo)
  (db/create-todo db todo))

(defn update-todo [db id todo]
  (db/update-todo db id todo))

(defn delete-todo-by-id [db id]
  (db/delete-todo-by-id db id))

(defn todo-routes [db]
  (defroutes todos
    (GET "/todos" [] (get-todos db))
    (GET "/todos/:id" [id] (get-todo-by-id db id))
    (POST "/todos" {todo :body} (create-todo db todo))
    (PUT "/todos/:id" [id :as {todo :body}] (update-todo db id todo))
    (DELETE "/todos/:id" [id] (delete-todo-by-id db id))))

(defn handler [db registry]
  (-> (todo-routes db)
      (ring/wrap-metrics registry {:path "/metrics"})
      (json/wrap-json-body {:keywords? true})
      json/wrap-json-response
      logger/wrap-with-logger))
