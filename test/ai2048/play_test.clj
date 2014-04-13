(ns ai2048.play-test
  (:require [clojure.test :refer :all]
            [ai2048.play :refer :all]
            [ai2048.board :as board]))

(deftest play-a-turn-test
  (testing "FIXME, I fail."
    (is (= (count (board/board-blank-elements (play-a-turn [[0 2 0 0] [0 0 2 0] [0 0 0 0] [0 0 0 0]] :up))) 
           13))))

(deftest eval-option-test-0
  (testing "FIXME, I fail."
    (is (= (eval-option board/test-board #(count (board/board-blank-elements %)) :up)
           {:board [[0 4 0 0] [0 0 0 0] [0 0 0 0] [0 0 0 0]], :score 15, :direction :up}))))

(deftest eval-option-test-1
  (testing "FIXME, I fail."
    (is (= (eval-option board/test-board #(count (board/board-blank-elements %)) :down)
           {:board [[0 0 0 0] [0 0 0 0] [0 0 0 0] [0 4 0 0]], :score 15, :direction :down}))))

(deftest eval-option-test-2
  (testing "FIXME, I fail."
    (is (= (eval-option board/test-board #(count (board/board-blank-elements %)) :left)
           {:board [[2 0 0 0] [2 0 0 0] [0 0 0 0] [0 0 0 0]], :score 14, :direction :left}))))

(deftest eval-option-test-3
  (testing "FIXME, I fail."
    (is (= (eval-option board/test-board #(count (board/board-blank-elements %)) :right)
           {:board [[0 0 0 2] [0 0 0 2] [0 0 0 0] [0 0 0 0]], :score 14, :direction :right}))))

(deftest n-biggest-elements-test-0
  (testing "FIXME, I fail."
    (is (= (n-biggest-elements 16 [[1 2 2 4]
                        [5 0 0 8]
                        [2 2 2 12]
                        [13 14 15 16]]) 
           '({:x 0, :y 0, :val 1} {:x 0, :y 1, :val 2} {:x 0, :y 2, :val 2} {:x 2, :y 0, :val 2} {:x 2, :y 1, :val 2} {:x 2, :y 2, :val 2} {:x 0, :y 3, :val 4} {:x 1, :y 0, :val 5} {:x 1, :y 3, :val 8} {:x 2, :y 3, :val 12} {:x 3, :y 0, :val 13} {:x 3, :y 1, :val 14} {:x 3, :y 2, :val 15} {:x 3, :y 3, :val 16})))))

(deftest payoff-fn-test-0
  (testing "FIXME, I fail."
    (is (= (payoff-fn [[0 0 0 0] [0 8 0 16] [4 4 32 2] [4 8 32 32]]) 24))))

(deftest payoff-fn-test-1
  (testing "FIXME, I fail."
    (is (= (payoff-fn [[2 2 2 2] [2 8 2 16] [4 4 32 2] [4 8 32 32]]) 0))))


(deftest options-move-test
  (testing "FIXME, I fail."
    (let [options (options-move [[0 0 0 0] [2 0 0 0] [8 0 0 0] [8 4 0 2]])]
    (is (= (sort-by #(:direction %) options)
           (sort-by #(:direction %) '({:board [[0 0 0 0] [0 0 0 0] [2 0 0 0] [16 4 0 2]], :score 0N, :direction :down} 
             {:board [[0 0 0 0] [2 0 0 0] [8 0 0 0] [8 4 2 0]], :score 11/4, :direction :left} 
             {:board [[0 0 0 0] [0 0 0 2] [0 0 0 8] [0 8 4 2]], :score 0N, :direction :right} 
             {:board [[2 4 0 2] [16 0 0 0] [0 0 0 0] [0 0 0 0]], :score 207N, :direction :up}))
)))))

(deftest make-options-test
  (testing "FIXME, I fail."
    (let [options (make-options board/test-board)]
       (is (= (count (:adv-options (:down options))) 15)) 
       (is (= (count (:adv-options (:up options))) 15))
       (is (= (count (:adv-options (:left options))) 14)) 
       (is (= (count (:adv-options (:right options))) 14))
      )))

(deftest build-test
  (testing "FIXME, I fail."
    (let [tree (build 30 [[0 8 32 16][2 4 16 2][2 4 8 16][2 4 8 16]])]                                              
    (is (empty? (:options (:down tree))))
    (is (empty? (:options (:up tree))))
    (is (= (count (:options (:right tree))) 1))
    (is (= (count (:options (:left tree))) 1)))))
    
(deftest minimax-test
  (testing "FIXME, I fail."
    (is (= (minimax (build 1000 [[0 8 0 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]]))
           {:right {:mean 151.56927083333332, :max 630N, :min 0N, :options-count 116}, :left {:mean 207.81979166666667, :max 630N, :min 0, :options-count 116}, :down {:mean 197.90533234126985, :max 2471/4, :min 0N, :options-count 544}, :up {:mean 325.28633432539687, :max 4515/4, :min 0N, :options-count 640}}
))))

(deftest optimal-minimax-move-test
  (testing "FIXME, I fail."
    (is (= (optimal-minimax-move [[64 8 32 16] 
                       [2 4 16 2] 
                       [2 4 8 16] 
                       [2 4 8 16]]) 
           :up
           ))))

(deftest play-smartly-test
  (testing "FIXME, I fail."
    (is (= (board/get-element (nth (iterate play-smartly board/test-board) 1) 0 1)  
           4))))
