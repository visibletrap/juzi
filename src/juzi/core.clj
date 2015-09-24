(ns juzi.core
  (:require [clojure.java.shell :refer [sh]])
  (:import [jline.console ConsoleReader]))

(defn make-session []
  (->> "resources/data.edn"
       (slurp)
       (read-string)
       (apply concat)
       (map #(zipmap [:en :zh] %))
       (map vector (map inc (range)))
       (map (fn [[id e]] [id (merge {:id id :passed false} e)]))
       (into {})))

(defn next-word [session]
  (->> session
       (map val)
       (remove :passed)
       (shuffle)
       (first)))

(defn mark-passed [session id]
  (assoc-in session [id :passed] true))

(defn clear-screen []
  (print (str (char 27) "[2J"))
  (print (str (char 27) "[;H")))

(defn start []
  (clear-screen)
  (let [cr (ConsoleReader.)]
    (println "Press 'y' if you know how to say Chinese sentence for this following English sentence, press something else otherwise.")
    (println "Ready? Press any key to continue ...")
    (.readCharacter cr)
    (clear-screen)
    (loop [s (make-session)]
      (if-let [w (next-word s)]
        (let [{:keys [id en zh]} w]
          (println en)
          (flush)
          (let [i (.readCharacter cr)]
            (if (= \y (char i))
              (do (clear-screen)
                  (recur (mark-passed s id)))
              (do (println "ans:" zh)
                  (.readCharacter cr)
                  (clear-screen)
                  (recur s)))))
        (println "You have passed all the words! Gōngxǐ. Zàijiàn.")))))
