(ns todo_clj.system
  (:gen-class)
  (:require [hikari-cp.core :as h]
            [integrant.core :as ig]
            [migratus.core :as m]
            [ring.adapter.jetty :as jetty]
            [todo_clj.api :as api]
            [iapetos.core :as prometheus]
            [iapetos.registry :as r]
            [iapetos.collector.ring :as ring]
            [iapetos.collector.jvm :as jvm])
  (:import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory))

(def config
  (ig/read-string (slurp "resources/config.edn")))

(defmethod ig/init-key :db [_ {:keys [registry] :as opts}]
  {:datasource (h/make-datasource
                (merge (dissoc opts :registry)
                       {:metrics-tracker-factory (PrometheusMetricsTrackerFactory. (r/raw registry))}))})

(defmethod ig/halt-key! :db [_ {db :datasource}]
  (h/close-datasource db))

(defmethod ig/init-key :handler [_ {:keys [db registry] :as opts}]
  (api/handler db registry))

(defmethod ig/init-key :server [_ {:keys [handler registry] :as opts}]
  (jetty/run-jetty handler (-> opts (dissoc :handler))))

(defmethod ig/halt-key! :server [_ server]
  (.stop server))

(defmethod ig/init-key :migrations [_ opts]
  (m/migrate opts))

(defmethod ig/init-key :registry [_ opts]
  (-> (prometheus/collector-registry)
      (ring/initialize)
      (jvm/initialize)))

(comment
  (def system (atom nil))

  (reset! system (ig/init config [:db]))

  (reset! system (ig/init config [:migrations]))

  (reset! system (ig/init config))

  (ig/halt! @system))

