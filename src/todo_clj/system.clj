(ns todo_clj.system
  (:gen-class)
  (:require [hikari-cp.core :as h]
            [integrant.core :as ig]
            [migratus.core :as m]
            [ring.adapter.jetty :as jetty]
            [todo_clj.api :as api]))

(def config
  (ig/read-string (slurp "resources/config.edn")))

(defmethod ig/init-key :db [_ opts]
  {:datasource (h/make-datasource opts)})

(defmethod ig/halt-key! :db [_ {db :datasource}]
  (h/close-datasource db))

(defmethod ig/init-key :handler [_ {:keys [db] :as opts}]
  (api/handler db))

(defmethod ig/init-key :server [_ {:keys [handler] :as opts}]
  (jetty/run-jetty handler (dissoc opts :handler)))

(defmethod ig/halt-key! :server [_ server]
  (.stop server))

(defmethod ig/init-key :migrations [_ opts]
  (m/migrate opts))

(comment
  (def system (atom nil))

  (reset! system (ig/init config [:db]))

  (reset! system (ig/init config [:migrations]))

  (reset! system (ig/init config))

  (ig/halt! @system)

  )

