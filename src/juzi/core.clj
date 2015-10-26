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
  (map #(zipmap [:en :pi :zh] %) data))

(defn say [chinese]
  (future (sh "/usr/bin/say" chinese)))

(defmulti question :mode)
(defmethod question :speak [{en :en}]
  (str "What's Chinese for \"" en "\"?"))
(defmethod question :listen [{:keys [zh]}]
  (future (Thread/sleep 500) (say zh))
  (str "What's the meaning of word(s) you've just heard?"))

(defmulti answer :mode)
(defmethod answer :speak [{:keys [pi zh]}]
  (str pi (when zh (do (say zh) (str " (" zh ")")))))
(defmethod answer :listen [{:keys [pi zh en]}]
  (str "You've heard \"" pi "\" (" zh ") which means \"" en "\"."))

(defn start []
  (clear-screen)
  (let [cr (ConsoleReader.)]
    (clear-screen)
    (loop [s (s/make-session (expand-data (load-sources)))]
      (if-let [w (s/next-word s)]
        (let [m (if (:zh w) (first (shuffle [:speak :listen])) :speak)
              r (assoc w :mode m)]
          (println (question r))
          (println)
          (println "Press any key to see the answer ...")
          (.readCharacter cr)
          (println)
          (println "Answer:" (answer r))
          (println)
          (println "Were you correct ? [y/n]")
          (let [i (.readCharacter cr)]
            (clear-screen)
            (if (= \y (char i))
              (recur (s/mark-passed s (:id r)))
              (recur s))))
        (println "Finish! Gōngxǐ. Zàijiàn.")))))
