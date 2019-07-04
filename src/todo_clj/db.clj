(ns todo_clj.db
  (:gen-class)
  (:require [clojure.java.jdbc :as jdbc])
  (:import java.time.format.DateTimeFormatter
           java.time.LocalDateTime))

(defn- date-time-string->date-time [s]
  (LocalDateTime/parse s (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss'Z'")))

(defn get-todos [db]
  (let [sql (str "SELECT id, title, done, date"
                 " FROM todos")]
    (jdbc/query db [sql])))

(defn get-todo-by-id [db id]
  (let [sql (str "SELECT title, done, date"
                 "  FROM todos"
                 " WHERE id = ?")]
    (jdbc/query db [sql (Integer/parseInt id)])))

(defn create-todo [db todo]
  "INSERT INTO todos (title, done, date)
        VALUES (?, ?, ?)
     RETURNING id"
  (let [{:keys [title done date]} todo]
    (jdbc/insert! db
                  :todos
                  {:title title
                   :done done
                   :date (date-time-string->date-time date)})))

(defn update-todo [db id todo]
  "UPDATE todos
      SET title = ?, 
          done  = ?,
          date  = ?
    WHERE id    = ?"
  (let [{:keys [title done date]} todo]
    (jdbc/update! db
                  :todos
                  {:title title
                   :done done
                   :date (date-time-string->date-time date)}
                  ["id = ?" (Integer/parseInt id)])))

(defn delete-todo-by-id [db id]
  "DELETE FROM todos
         WHERE id = ?"
    (jdbc/delete! db :todos ["id = ?" (Integer/parseInt id)]))
