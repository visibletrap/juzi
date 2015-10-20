(ns juzi.sen-test
  (require [clojure.test :refer :all]
           [juzi.sen :refer [make-session next-word mark-passed]]))

(deftest make-session-test
  (let [out (make-session [["English1" "Translation1"] ["English2" "Translation2"]])]
    (is (= out {1 {:id 1 :passed false :en "English1" :zh "Translation1"}
                2 {:id 2 :passed false :en "English2" :zh "Translation2"}}))))

(deftest next-word-test
  (testing "get one of the value"
    (let [session {:key :val}]
      (is (= :val (next-word session)))))
  (testing "get only unpassed word"
    (let [session {:passed-key   {:passed true :any-data 1}
                   :unpassed-key {:passed false :any-data 2}}]
      (is (= {:passed false :any-data 2} (next-word session))))))

(deftest mark-passed-test
  (let [session {:key {:passed false}}]
    (is (:passed (:key (mark-passed session :key))))))

(deftest no-more-unpassed-word
  (let [session (make-session [[["English11" "Translation1"]] [["English21" "Translation2"]]])
        {first-id :id} (next-word session)
        session (mark-passed session first-id)
        {second-id :id} (next-word session)
        session (mark-passed session second-id)]
    (is (nil? (next-word session)))))
