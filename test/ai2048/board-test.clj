(ns ai2048.board-test
  (:require [clojure.test :refer :all]
            [ai2048.board :refer :all]))

(deftest new-board-test-0
  (testing "FIXME, I fail."
    (is (= (new-board) [[0 0 0 0]
                        [0 0 0 0]
                        [0 0 0 0]
                        [0 0 0 0]]))))


; (deftest new-board-test
;   (testing "FIXME, I fail."
;     (is (= (do something) expected ))))



(deftest get-element-test-0
  (testing "FIXME, I fail."
    (is (= (get-element [[ 1 2 3 1 ] 
                         [ 3 3 1 1 ]
                         [ 0 1 1 1 ]
                         [ 3 2 1 1 ]] 2 0) 
           0))))

(deftest get-element-test-1
  (testing "FIXME, I fail."
    (is (= (get-element [[ 1 2 3 1 ] 
                         [ 3 3 1 1 ]
                         [ 0 1 1 1 ]
                         [ 3 2 1 1 ]] 0 0) 
           1))))


(deftest distribute-x-test-0
  (testing "FIXME, I fail."
    (is (= (distribute-x 2 [{:val 1 :y "y"}  {:val 2 :y "y2"}]) 
           '({:x 2, :y "y", :val 1} {:x 2, :y "y2", :val 2}) ))))


(deftest rank-xy-test-0
  (testing "FIXME, I fail."
    (is (= (rank-xy [[0 10 20] [1 11 21] [2 12 22]]) 
           '({:x 0, :y 0, :val 0} 
            {:x 0, :y 1, :val 10} 
            {:x 0, :y 2, :val 20} 
            {:x 1, :y 0, :val 1} 
            {:x 1, :y 1, :val 11} 
            {:x 1, :y 2, :val 21} 
            {:x 2, :y 0, :val 2} 
            {:x 2, :y 1, :val 12} 
            {:x 2, :y 2, :val 22})))))



(deftest blank-elements-test-0
  (testing "FIXME, I fail."
    (is (= (blank-elements (rank-xy [[0 10 20] [1 11 21] [2 12 22]]))
            '({:x 0, :y 0}) ))))

(deftest blank-elements-test-1
  (testing "FIXME, I fail."
    (is (= (blank-elements (rank-xy [[0 10 20] [1 0 21] [2 0 22]]))
            '({:x 0, :y 0} 
              {:x 1, :y 1} 
              {:x 2, :y 1})))))


(deftest board-blank-elements-test-0
  (testing "FIXME, I fail."
    (is (= (board-blank-elements [[ 1 2 3 1 ] 
                                  [ 3 3 1 1 ]
                                  [ 1 1 1 1 ]
                                  [ 3 2 1 1 ]]) 
            '()))))

(deftest board-blank-elements-test-1
  (testing "FIXME, I fail."
    (is (= (board-blank-elements [[ 1 2 3 1 ] 
                                  [ 3 3 1 1 ]
                                  [ 0 1 1 1 ]
                                  [ 3 2 1 1 ]]) 
            '({:x 2, :y 0})))))

(deftest board-blank-elements-test-2
  (testing "FIXME, I fail."
    (is (= (board-blank-elements [[ 1 2 3 1 ] 
                                  [ 3 3 1 1 ]
                                  [ 0 0 0 1 ]
                                  [ 3 2 1 1 ]]) 
            '({:x 2, :y 0} 
              {:x 2, :y 1} 
              {:x 2, :y 2})))))


(deftest rand-board-blank-element-test-0
  (testing "FIXME, I fail."
    (is (= (rand-board-blank-element [[ 1 2 3 1 ] 
                                      [ 3 3 1 1 ]
                                      [ 0 1 1 1 ]
                                      [ 3 2 1 1 ]]) 
            '{:x 2, :y 0}))))


(deftest apply-at-test-0
  (testing "FIXME, I fail."
    (is (= (apply-at [1 2] 1 (fn [x] 0))
           [1 0]))))

(deftest apply-at-test-1
  (testing "FIXME, I fail."
    (is (= (apply-at [[1 2] [3 4]] 1 (fn [x] x))
           [[1 2] [3 4]]))))


(deftest apply-at-xy-test-0
  (testing "FIXME, I fail."
    (is (= (apply-at-xy [[1 2 3 4]
                         [2 3 4 5]
                         [3 4 5 6]
                         [4 5 6 7]] 2 2 (fn [x] 0))
           [[1 2 3 4]
            [2 3 4 5]
            [3 4 0 6]
            [4 5 6 7]]))))

(deftest set-element-test-0
  (testing "FIXME, I fail."
    (is (= (set-element [[1 2 3 4]
                         [2 3 4 5]
                         [3 4 5 6]
                         [4 5 6 7]] 2 2 0)
           [[1 2 3 4]
            [2 3 4 5]
            [3 4 0 6]
            [4 5 6 7]]))))






; (deftest new-board-test
;   (testing "FIXME, I fail."
;     (is (= (do something) expected ))))


; (deftest new-board-test
;   (testing "FIXME, I fail."
;     (is (= (do something) expected ))))


; (deftest new-board-test
;   (testing "FIXME, I fail."
;     (is (= (do something) expected ))))




; (deftest new-board-test
;   (testing "FIXME, I fail."
;     (is (= (do something) expected ))))



