(ns juzi.core
  (:require [clojure.java.shell :refer [sh]]
            [juzi.sen :as s])
  (:import [jline.console ConsoleReader]))

(defn clear-screen []
  (print (str (char 27) "[2J"))
  (print (str (char 27) "[;H")))

(defn source []
  (->> "resources/data.edn" (slurp) (read-string)))

(defn start []
  (clear-screen)
  (let [cr (ConsoleReader.)]
    (println "Press 'y' if you know how to say Chinese sentence for this following English sentence, press something else otherwise.")
    (println "Ready? Press any key to continue ...")
    (.readCharacter cr)
    (clear-screen)
    (loop [s (s/make-session (source))]
      (if-let [w (s/next-word s)]
        (let [{:keys [id en zh]} w]
          (println en)
          (flush)
          (let [i (.readCharacter cr)]
            (if (= \y (char i))
              (do (clear-screen)
                  (recur (s/mark-passed s id)))
              (do (println "ans:" zh)
                  (.readCharacter cr)
                  (clear-screen)
                  (recur s)))))
        (println "You have passed all the words! Gōngxǐ. Zàijiàn.")))))
