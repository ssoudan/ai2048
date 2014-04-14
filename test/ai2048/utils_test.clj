(ns ai2048.utils_test
  (:require [clojure.test :refer :all]
            [ai2048.utils :refer :all]))


(deftest power-test-0
  (testing "FIXME, I fail."
    (is (= (power 2 0)
           0))))

(deftest power-test-1
  (testing "FIXME, I fail."
    (is (= (power 2 4)
           16))))

(deftest power-test-2
  (testing "FIXME, I fail."
    (is (= (power 0 4)
           1))))

(deftest abs-test-0
  (testing "FIXME, I fail."
    (is (= (abs 4)
           4))))

(deftest abs-test-1
  (testing "FIXME, I fail."
    (is (= (abs 0)
           0))))

(deftest abs-test-2
  (testing "FIXME, I fail."
    (is (= (abs 4)
           4))))

(deftest abs-test-2
  (testing "FIXME, I fail."
    (is (= (abs -4)
           4))))


(deftest manhattan-distance-test-0
  (testing "FIXME, I fail."
    (is (= (manhattan-distance {:x 0 :y 0} {:x 0 :y 0})
           0))))

(deftest manhattan-distance-test-1
  (testing "FIXME, I fail."
    (is (= (manhattan-distance {:x 1 :y 0} {:x 0 :y 0})
           1))))

(deftest manhattan-distance-test-2
  (testing "FIXME, I fail."
    (is (= (manhattan-distance {:x 1 :y 1} {:x 0 :y 0})
           2))))

(deftest manhattan-distance-test-3
  (testing "FIXME, I fail."
    (is (= (manhattan-distance {:x 1 :y 0} {:x 0 :y 1})
           2))))

(deftest mean-test-0
  (testing "FIXME, I fail."
    (is (= (mean [-4 4])
           0))))

(deftest mean-test-1
  (testing "FIXME, I fail."
    (is (= (mean [])
           0))))

(deftest mean-test-2
  (testing "FIXME, I fail."
    (is (= (mean [4 4])
           4))))

(deftest mean-test-3
  (testing "FIXME, I fail."
    (is (= (mean [1 2 3])
           2))))

(deftest fmap-test-0
  (testing "FIXME, I fail."
    (is (= (fmap (fn [k v] {v k}) {1 2 3 4})
           '({4 3} {2 1})))))

(deftest fmap-test-1
  (testing "FIXME, I fail."
    (is (= (fmap (fn [k v] {v k}) [])
           '()))))
