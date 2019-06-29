(ns todo_clj.db
  (:require [hikari-cp.core :as h]
            [clojure.java.jdbc :as jdbc]
            [migratus.core :as m])
  (:import (java.time LocalDateTime)
           (java.time.format DateTimeFormatter))
  (:gen-class))

(defn- date-time-string->date-time [s]
  ;; (.parse (java.text.SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ss'Z'") s)
  (LocalDateTime/parse s (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss'Z'")))

(def ds-options {:maximum-pool-size  10
                 :pool-name          "pg-pool"
                 :adapter            "postgresql"
                 :username           "postgres"
                 :password           "secret"
                 :database-name      "postgres"
                 :server-name        "localhost"
                 :port-number        5432})

(defonce ds
  (delay (h/make-datasource ds-options)))

(defn migrate! []
  (jdbc/with-db-connection [conn {:datasource @ds}]
    (let [config {:store :database
                  :migration-dir "migrations/"
                  :migration-table-name "migrations"
                  :db conn}]
      (m/migrate config))))

(defn get-todos []
  (let [sql (str "SELECT id, title, done, date"
                 " FROM todos")]
    (jdbc/with-db-connection [conn {:datasource @ds}]
      (jdbc/query conn [sql]))))

(defn get-todo-by-id [id]
  (let [sql (str "SELECT title, done, date"
                 "  FROM todos"
                 " WHERE id = ?")]
    (jdbc/with-db-connection [conn {:datasource @ds}]
      (jdbc/query conn [sql (Integer/parseInt id)]))))

(defn create-todo [todo]
  "INSERT INTO todos (title, done, date)
        VALUES (?, ?, ?)
     RETURNING id"
  (let [{:keys [title done date]} todo]
    (jdbc/with-db-connection [conn {:datasource @ds}]
      (jdbc/insert! conn
                    :todos
                    {:title title :done done :date (date-time-string->date-time date)}))))

(defn update-todo [id todo]
  "UPDATE todos
      SET title = ?, 
          done  = ?,
          date  = ?
    WHERE id    = ?"
  (let [{:keys [title done date]} todo]
    (jdbc/with-db-connection [conn {:datasource @ds}]
      (jdbc/update! conn
                    :todos
                    {:title title :done done :date (date-time-string->date-time date)}
                    ["id = ?" (Integer/parseInt id)]))))

(defn delete-todo-by-id [id]
  "DELETE FROM todos
         WHERE id = ?"
  (jdbc/with-db-connection [conn {:datasource @ds}]
    (jdbc/delete! conn
                  :todos
                  ["id = ?" (Integer/parseInt id)])))

(comment
  (migrate!))
