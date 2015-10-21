(ns juzi.session)

(defn next-word [session]
  (->> session
       (map val)
       (remove :passed)
       (shuffle)
       (first)))

(defn mark-unpassed [session id]
  (assoc-in session [id :passed] false))

(defn mark-passed [session id]
  (assoc-in session [id :passed] true))

(defn- gen-ids []
  (map inc (range)))

(defn- index [data]
  (->> data
       (map vector (gen-ids))
       (map (fn [[id d]] [id (assoc d :id id)]))
       (into {})))

(defn- mark-unpassed-all [session]
  (reduce mark-unpassed session (keys session)))

(defn make-session [data]
  (-> data
      (index)
      (mark-unpassed-all)))