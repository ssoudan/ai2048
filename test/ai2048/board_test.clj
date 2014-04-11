(ns ai2048.board_test
  (:require [clojure.test :refer :all]
            [ai2048.board :refer :all]))


(deftest new-board-test-0
  (testing "FIXME, I fail."
    (is (= (new-board) [[0 0 0 0]
                        [0 0 0 0]
                        [0 0 0 0]
                        [0 0 0 0]]))))


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


(deftest evolve-row-test-0
  (testing "FIXME, I fail."
    (is (= (do (evolve-row [ 1 1 0 1 ])) 
           [0 0 1 2]))))


(deftest evolve-row-test-1
  (testing "FIXME, I fail."
    (is (= (do (evolve-row [ 1024 1024 1 1 ])) 
           [0 0 2048 2]))))


(deftest evolve-row-test-2
  (testing "FIXME, I fail."
    (is (= (do (evolve-row [ 1 1 2 0 ])) 
           [0 0 2 2]))))


(deftest rotate-test-0
  (testing "FIXME, I fail."
    (is (= (do (rotate (rotate [[1 1 1]
                                [2 2 2]
                                [3 3 3]]))) [[1 1 1]
                                             [2 2 2]
                                             [3 3 3]]))))

(deftest reverse-board-test-0
  (testing "FIXME, I fail."
    (is (= (do (reverse-board [[1 2 3]
                               [4 5 6]
                               [7 8 9]]))) [[7 8 9]
                                            [4 5 6]
                                            [1 2 3]])))

(deftest to-up-test-0
  (testing "FIXME, I fail."
    (is (= (do (to-up [[1 2 3]
                       [4 5 6]
                       [7 8 9]]))) 
        [[7 4 1]
         [8 5 2]
         [9 6 3]])))


(deftest to-down-test-0
  (testing "FIXME, I fail."
    (is (= (do (to-down [[1 2 3]
                         [4 5 6]
                         [7 8 9]]))) 
        [[1 4 7]
         [2 5 8]
         [3 6 9]])))


(deftest to-left-test-0
  (testing "FIXME, I fail."
    (is (= (do (to-left [[1 2 3]
                         [4 5 6]
                         [7 8 9]]))) 
        [[3 2 1]
         [6 5 4]
         [9 8 7]])))


(deftest to-right-test-0
  (testing "FIXME, I fail."
    (is (= (do (to-right [[1 2 3]
                          [4 5 6]
                          [7 8 9]]))) 
        [[1 2 3]
         [4 5 6]
         [7 8 9]])))


(deftest from-to-left-test
  (testing "FIXME, I fail."
    (is (= (do 
             (from-left (to-left [[1 2 3]
                                  [4 5 6]
                                  [7 8 9]]))) 
           [[1 2 3]
            [4 5 6]
            [7 8 9]]))))


(deftest from-to-right-test
  (testing "FIXME, I fail."
    (is (= (do 
             (from-right (to-right [[1 2 3]
                                    [4 5 6]
                                    [7 8 9]]))) 
           [[1 2 3]
            [4 5 6]
            [7 8 9]]))))


(deftest from-to-up-test
  (testing "FIXME, I fail."
    (is (= (do 
             (from-up (to-up [[1 2 3]
                              [4 5 6]
                              [7 8 9]]))) 
           [[1 2 3]
            [4 5 6]
            [7 8 9]]))))


(deftest from-to-up-test
  (testing "FIXME, I fail."
    (is (= (do 
             (from-up (to-up [[1 2 3]
                              [4 5 6]
                              [7 8 9]]))) 
           [[1 2 3]
            [4 5 6]
            [7 8 9]]))))


(deftest evolve-board-test
  (testing "FIXME, I fail."
    (is (= (do (evolve-board [[1 1 2 0]
                              [1 1 2 0]
                              [1 1 2 0]
                              [1 1 2 0]])) 
           '([0 0 2 2]
             [0 0 2 2]
             [0 0 2 2]
             [0 0 2 2])))))


(deftest play-right-test
  (testing "FIXME, I fail."
    (is (= (do (play-right [[1 1 2 0]
                            [1 1 2 0]
                            [1 1 2 0]
                            [1 1 2 0]])) [[0 0 2 2]
                                          [0 0 2 2]
                                          [0 0 2 2]
                                          [0 0 2 2]]))))


(deftest play-up-test
  (testing "FIXME, I fail."
    (is (= (do (play-up [[1 1 2 0]
                         [1 1 2 0]
                         [1 1 2 0]
                         [1 1 2 0]])) 
           [[2 2 4 0]
            [2 2 4 0]
            [0 0 0 0]
            [0 0 0 0]]))))


(deftest play-down-test
  (testing "FIXME, I fail."
    (is (= (do (play-down [[1 1 2 0]
                           [1 1 2 0]
                           [1 1 2 0]
                           [1 1 2 0]])) 
           [[0 0 0 0]
            [0 0 0 0]
            [2 2 4 0]
            [2 2 4 0]]))))


(deftest play-left-test
  (testing "FIXME, I fail."
    (is (= (do (play-left [[1 1 2 0]
                           [1 1 2 0]
                           [1 1 2 0]
                           [1 1 2 0]])) 
           [[2 2 0 0]
            [2 2 0 0]
            [2 2 0 0]
            [2 2 0 0]]))))


(deftest move-pieces-test-0
  (testing "FIXME, I fail."
    (is (= (do (move-pieces [[1 1 2 0]
                             [1 1 2 0]
                             [1 1 2 0]
                             [1 1 2 0]] :left)) 
           [[2 2 0 0]
            [2 2 0 0]
            [2 2 0 0]
            [2 2 0 0]]))))


(deftest move-pieces-test-1
  (testing "FIXME, I fail."
    (is (= (do (move-pieces [[1 1 2 0]
                             [1 1 2 0]
                             [1 1 2 0]
                             [1 1 2 0]] :right)) 
           [[0 0 2 2]
            [0 0 2 2]
            [0 0 2 2]
            [0 0 2 2]]))))


(deftest move-pieces-test-2
  (testing "FIXME, I fail."
    (is (= (do (move-pieces [[1 1 2 0]
                             [1 1 2 0]
                             [1 1 2 0]
                             [1 1 2 0]] :up)) 
           [[2 2 4 0]
            [2 2 4 0]
            [0 0 0 0]
            [0 0 0 0]]))))


(deftest move-pieces-test-3
  (testing "FIXME, I fail."
    (is (= (do (move-pieces [[1 1 2 0]
                             [1 1 2 0]
                             [1 1 2 0]
                             [1 1 2 0]] :down)) 
           [[0 0 0 0]
            [0 0 0 0]
            [2 2 4 0]
            [2 2 4 0]]))))


(deftest play-sequence-test
  (testing "FIXME, I fail."
    (is (= (do (play-sequence [[1 1 2 0]
                               [1 1 2 0]
                               [1 1 2 0]
                               [1 1 2 0]] [:left :left :up :up])) 
           [[16 0 0 0]
            [ 0 0 0 0]
            [ 0 0 0 0]
            [ 0 0 0 0]]))))


(deftest neighbors-test-0
  (testing "FIXME, I fail."
    (is (= (do (neighbors [[1 2 3 4]
                           [5 6 7 8]
                           [9 10 11 12]
                           [13 14 15 16]] 3 3)) 
           '(15 12)))))


(deftest neighbors-test-1
  (testing "FIXME, I fail."
    (is (= (do (neighbors [[1 2 3 4]
                           [5 6 7 8]
                           [9 10 11 12]
                           [13 14 15 16]] 0 0)) 
           '(2 5)))))


(deftest neighbors-test-2
  (testing "FIXME, I fail."
    (is (= (do (neighbors [[1 2 3 4]
                           [5 6 7 8]
                           [9 10 11 12]
                           [13 14 15 16]] 0 2)) 
           '(2 4 7)))))


(deftest neighbors-test-3
  (testing "FIXME, I fail."
    (is (= (do (neighbors [[1 2 3 4]
                           [5 6 7 8]
                           [9 10 11 12]
                           [13 14 15 16]] 2 2)) 
           '(10 12 7 15)))))


(deftest test-board-test
  (testing "FIXME, I fail."
    (is (= (do (move-pieces test-board :up)) [[0 4 0 0]
                                              [0 0 0 0]
                                              [0 0 0 0]
                                              [0 0 0 0]]))))
