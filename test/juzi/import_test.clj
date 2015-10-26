(ns juzi.import-test
  (:require [clojure.test :refer :all]
            [juzi.import :refer :all]
            [clojure.java.io :refer :all]))

(deftest csv->edn*-test
  (testing "import from blank"
    (let [csv "chinese,english,pinyin\n床,bed,chuáng"
          edn [{:en "bed" :zh "床" :pi "chuáng" :score 0}]]
      (is (= edn (csv->edn* csv))))))

(deftest csv->edn-test
  (let [in "test/resources/import.csv"
        out "test/resources/import.edn"]
    (delete-file out :silently true)
    (csv->edn in out)
    (is (= (read-string (slurp out))
           (csv->edn* (slurp in))))))
