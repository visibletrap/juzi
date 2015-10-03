(ns juzi.sen)

(defn make-session [source]
  (->> source
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
