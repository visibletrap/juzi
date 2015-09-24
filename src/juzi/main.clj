(ns juzi.main
  (:require [juzi.core :as core])
  (:gen-class))

(defn -main [&]
  (core/start))
