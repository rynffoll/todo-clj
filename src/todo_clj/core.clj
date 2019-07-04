(ns todo_clj.core
  (:gen-class)
  (:require [integrant.core :as ig]
            [todo_clj.system :as sys]))

(def system (atom nil))

(defn start! []
  (reset! system (ig/init sys/config)))

(defn stop! []
  (ig/halt! @system))

(defn -main [& args]
  (start!))

(comment
  (start!)
  (stop!)
  )
