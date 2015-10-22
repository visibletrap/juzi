(ns juzi.core
  (:require [clojure.java.shell :refer [sh]]
            [juzi.session :as s])
  (:import [jline.console ConsoleReader]))

(defn clear-screen []
  (print (str (char 27) "[2J"))
  (print (str (char 27) "[;H")))

(defn load-source [path]
  (->> path
       (slurp)
       (read-string)))

(defn load-sources []
  (mapcat load-source ["resources/word.edn" "resources/sentence.edn"]))

(defn expand-data [data]
  (map #(zipmap [:en :zh] %) data))

(defn start []
  (clear-screen)
  (let [cr (ConsoleReader.)]
    (clear-screen)
    (loop [s (s/make-session (expand-data (load-sources)))]
      (if-let [w (s/next-word s)]
        (let [{:keys [id en zh]} w]
          (println (str "What's Chinese for \"" en "\"?" " Make sure you get the tones right!" ))
          (println)
          (println "Press any key to see the answer ...")
          (.readCharacter cr)
          (println)
          (println "answer:" zh)
          (println)
          (println "Were you correct? [y/n]")
          (let [i (.readCharacter cr)]
            (clear-screen)
            (if (= \y (char i))
              (recur (s/mark-passed s id))
              (recur s))))
        (println "You have passed all the words! Gōngxǐ. Zàijiàn.")))))
