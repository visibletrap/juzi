(ns juzi.import
  (:require [clojure.data.csv :as c]))

(def header->data-key {"chinese" :zh "english" :en "pinyin" :pi})

(defn csv->edn* [csv]
  (let [[h & t] (c/read-csv csv)
        data-key (map header->data-key h)]
    (->> t
         (map #(zipmap data-key %))
         (mapv #(assoc % :score 0)))))

(defn csv->edn [csv-path edn-path]
  (spit edn-path (pr-str (csv->edn* (slurp csv-path)))))
