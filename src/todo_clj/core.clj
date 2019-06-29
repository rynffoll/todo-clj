(ns todo_clj.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [todo_clj.api :as api]
            [todo_clj.db :as db]
            [ring.middleware.json :as json]
            [ring.logger :as logger])
  (:gen-class))

(defonce server (atom nil))

(defn start! []
  (swap! server
         (fn [prev]
           (run-jetty
            (-> #'api/todo-routes
                (json/wrap-json-body {:keywords? true})
                json/wrap-json-response
                (logger/wrap-with-logger {:request-keys [:request-method :uri :server-name]
                                          :log-exceptions? false}))
            {:port 3000
             :join? false}))))

(defn stop! []
  (.stop @server))

(defn -main [& args]
  (db/migrate!)
  (start!))

(comment
  (db/migrate!)
  @server
  (start!)
  (stop!)
  )
