(ns juzi.session-test
  (require [clojure.test :refer :all]
           [juzi.session :as s]))

(deftest make-session-test
  (let [out (s/make-session [{:en "English1" :zh "Translation1"}
                             {:en "English2" :zh "Translation2"}])]
    (is (= out {1 {:id 1 :passed false :en "English1" :zh "Translation1"}
                2 {:id 2 :passed false :en "English2" :zh "Translation2"}}))))

(deftest next-word-test
  (testing "get one of the value"
    (let [session {:key :val}]
      (is (= :val (s/next-word session)))))
  (testing "get only unpassed word"
    (let [session {:passed-key   {:passed true :any-data 1}
                   :unpassed-key {:passed false :any-data 2}}]
      (is (= {:passed false :any-data 2} (s/next-word session))))))

(deftest mark-passed-test
  (let [session {:key {:passed false}}]
    (is (:passed (:key (s/mark-passed session :key))))))

(deftest mark-unpassed-test
  (let [session {:key {}}]
    (is (not (:passed (:key (s/mark-unpassed session :key)))))))

(deftest no-more-unpassed-word
  (let [session (s/make-session [{:en "English1" :zh "Translation1"}
                                 {:en "English2" :zh "Translation2"}])
        {first-id :id} (s/next-word session)
        session (s/mark-passed session first-id)
        {second-id :id} (s/next-word session)
        session (s/mark-passed session second-id)]
    (is (nil? (s/next-word session)))))
