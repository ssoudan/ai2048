(ns ai2048.play
  (:require [ai2048.board :as board]
            [ai2048.utils :as utils]
            [clojure.pprint]))

;; Be able to play a real turn:
;; - make a move
;; - get a random new element
(defn play-a-turn [board direction]
  (board/generate-new-rand-element (board/move-pieces board direction)))

; (play-a-turn [[0 2 0 0] [0 0 2 0] [0 0 0 0] [0 0 0 0]] :up)

;; Be able to evaluate the score of a board

(defn eval-option [board payoff-fn direction]
  (let [new-board (board/move-pieces board direction)]
    {:board new-board :score (payoff-fn new-board) :direction direction}))

; (eval-option board/test-board #(count (board/board-blank-elements %)) :up)
; (eval-option board/test-board #(count (board/board-blank-elements %)) :down)
; (eval-option board/test-board #(count (board/board-blank-elements %)) :left)
; (eval-option board/test-board #(count (board/board-blank-elements %)) :right)


;; Be able to pick the largest elements of the board with their coordinate (use the coordinate for the sorting so that all elements
;; with the same value are kind of sorted according to their positions)

(defn n-biggest-elements [n board]
  (take-last n
             (sort-by
               #(+ (* 131072 (:val %)) (:x %) (:y %))
               (filter
                 #(not (= 0 (:val %)))
                 (board/rank-xy board)))))


; (n-biggest-elements 16 [[1 2 2 4]
;                         [5 0 0 8]
;                         [2 2 2 12]
;                         [13 14 15 16]])

;; Be able to evaluate the payoff of a board
;; Multiple options to do that.

;; This one is an attempt to get the bigger elements sorted and close (manhanttan distance) as well as
;; value the blank elements and try to get the biggest element in a corner.
;; Doesn't work that well.

; (defn payoff-fn [board]
;   (let [big-guys (n-biggest-elements 6 board)]
;     (* (count (board/board-blank-elements board))
;        (+ 1
;           (reduce + (map (fn [x y] (if (= (utils/manhattan-distance x y) 1) '1 '0)) big-guys (drop 1 big-guys))))
;        (let [biggest (last big-guys)]
;        (if (or
;              (= (:x biggest) 0)
;              (= (:x biggest) 3)
;              (= (:y biggest) 0)
;              (= (:y biggest) 3)) '2 '1)

;        ))))

;;
;; This is the one
;; it can reach 2048!!
;;
;; We want:
;; - as many blank element as possible
;; - as big value as possible upper line

(defn payoff-fn [board]
  (let [payoff-coef [0 0 0 0
                     0 0 0 0
                     1 1 1 1
                     10 10 10 10]]
    (* (/ (count (board/board-blank-elements board)) 16)
       (reduce +
               (map
                 (fn [x y] (* (utils/power 2 x) y))
                 (flatten (reverse board))
                 payoff-coef)))
    ))

; (payoff-fn [[0 0 0 0] [0 8 0 16] [4 4 32 2] [4 8 32 32]])

; (payoff-fn [[2 2 2 2] [2 8 2 16] [4 4 32 2] [4 8 32 32]])

;; Be able to generate the board resulting of the moves
;;
;; TODO: blacklist taboo-ed directions

(defn options-move [board]
  (sort-by #(:score %) [
                         (eval-option board payoff-fn :up)
                         (eval-option board payoff-fn :down)
                         (eval-option board payoff-fn :left)
                         (eval-option board payoff-fn :right)]))

; (options-move [[8 4 2 2] [4 8 8 2] [64 16 16 4] [16 2 32 16]])
; (options-move [[0 0 0 0] [2 0 0 0] [8 0 0 0] [8 4 0 2]])

;; This is the general structure for the evaluation of the options:
;; {
;;  :score XXX
;;  :direction XXX
;;  :board XXX
;;  :options [ {...}]
;; }

;; Be able to generate the game turn (with new random elements)
;; For that we first add to the previous structure the places where those elements can fit.
;; Then we will make assumption on what the actual random value is and again generate the new options
;; starting from there.

(defn make-options [board]
  (into {} (map (fn [x] (hash-map (:direction x) (assoc x :adv-options (board/board-blank-elements (:board x))))) (options-move board))))

; (make-options board/test-board)

;; Be able to construct a tree of possible moves.
;; This tree is sort-of limited in size by the initial budget.
;; That's not very precise as we don't know beforehand how many options we will get in a subtree before evaluating it
;; and we always want to evaluate the different branches with some fairness.

(defn build [budget board]
  (when (pos? budget)
    (let [options (make-options board)]
      (assoc-in
        (assoc-in
          (assoc-in
            (assoc-in
              options
              [:left :options]
              (let [opt (get-in options [:left :adv-options])]
                (into [] (filter identity (map #(build (- (/ budget 4) (* 4 (count opt))) (board/set-element (get-in options [:left :board]) (:x %) (:y %) 2)) opt)))))
            [:right :options]
            (let [opt (get-in options [:right :adv-options])]
              (into [] (filter identity (map #(build (- (/ budget 4) (* 4 (count opt))) (board/set-element (get-in options [:right :board]) (:x %) (:y %) 2)) opt)))))
          [:up :options]
          (let [opt (get-in options [:up :adv-options])]
            (into [] (filter identity (map #(build (- (/ budget 4) (* 4 (count opt))) (board/set-element (get-in options [:up :board]) (:x %) (:y %) 2)) opt)))))
        [:down :options]
        (let [opt (get-in options [:down :adv-options])]
          (into [] (filter identity (map #(build (- (/ budget 4) (* 4 (count opt))) (board/set-element (get-in options [:down :board]) (:x %) (:y %) 2)) opt)))))
      )))

; (build 30 [[0 8 32 16]
;            [2 4 16 2]
;            [2 4 8 16]
;            [2 4 8 16]])

; {:right {:options [{:left {:options [], :adv-options (), :board [[2 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]], :score 0, :direction :left},
;                     :right {:options [], :adv-options (), :board [[2 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]], :score 0, :direction :right},
;                     :down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0}), :board [[0 0 0 0] [0 8 32 16] [4 4 16 2] [4 8 16 32]], :score 340N, :direction :down},
;                     :up {:options [], :adv-options ({:x 2, :y 0} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [4 8 16 2] [0 4 16 32] [0 0 0 0]], :score 450N, :direction :up}}],
;          :adv-options ({:x 0, :y 0}),
;          :board [[0 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]],
;          :score 341/4,
;          :direction :right},
;  :left {:options [
;                   {:left {:options [], :adv-options (), :board [[8 32 16 2] [2 4 16 2] [2 4 8 16] [2 4 8 16]], :score 0, :direction :left},
;                    :right {:options [], :adv-options (), :board [[8 32 16 2] [2 4 16 2] [2 4 8 16] [2 4 8 16]], :score 0, :direction :right},
;                    :down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 2} {:x 1, :y 3}), :board [[0 0 0 0] [8 32 0 0] [2 4 32 4] [4 8 16 32]], :score 408N, :direction :down},
;                    :up {:options [], :adv-options ({:x 2, :y 2} {:x 2, :y 3} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[8 32 32 4] [4 8 16 32] [2 4 0 0] [0 0 0 0]], :score 918N, :direction :up}}],
;         :adv-options ({:x 0, :y 3}),
;         :board [[8 32 16 0] [2 4 16 2] [2 4 8 16] [2 4 8 16]],
;         :score 405/4,
;         :direction :left},
;  :down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0}), :board [[0 0 0 0] [0 8 32 16] [2 4 16 2] [4 8 16 32]], :score 340N, :direction :down},
;  :up {:options [], :adv-options ({:x 2, :y 0} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [2 8 16 2] [0 4 16 32] [0 0 0 0]], :score 1785/4, :direction :up}}


; (build 300 [[0 8 32 16]
;            [2 4 16 2]
;            [2 4 8 16]
;            [2 4 8 16]])

; {:right {:options [{:left {:options [], :adv-options (), :board [[2 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]], :score 0, :direction :left}, :right {:options [], :adv-options (), :board [[2 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]], :score 0, :direction :right}, :down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0}), :board [[0 0 0 0] [0 8 32 16] [4 4 16 2] [4 8 16 32]], :score 340N, :direction :down}, :up {:options [], :adv-options ({:x 2, :y 0} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [4 8 16 2] [0 4 16 32] [0 0 0 0]], :score 450N, :direction :up}}], :adv-options ({:x 0, :y 0}), :board [[0 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]], :score 341/4, :direction :right},
;  :left {:options [{:left {:options [], :adv-options (), :board [[8 32 16 2] [2 4 16 2] [2 4 8 16] [2 4 8 16]], :score 0, :direction :left}, :right {:options [], :adv-options (), :board [[8 32 16 2] [2 4 16 2] [2 4 8 16] [2 4 8 16]], :score 0, :direction :right}, :down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 2} {:x 1, :y 3}), :board [[0 0 0 0] [8 32 0 0] [2 4 32 4] [4 8 16 32]], :score 408N, :direction :down}, :up {:options [], :adv-options ({:x 2, :y 2} {:x 2, :y 3} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[8 32 32 4] [4 8 16 32] [2 4 0 0] [0 0 0 0]], :score 918N, :direction :up}}], :adv-options ({:x 0, :y 3}), :board [[8 32 16 0] [2 4 16 2] [2 4 8 16] [2 4 8 16]], :score 405/4, :direction :left},
;  :down {
;         :options
;         [{
;           :down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0} {:x 1, :y 2}), :board [[0 0 0 0] [0 8 0 16] [4 4 32 2] [4 8 32 32]], :score 24N, :direction :down},
;           :right {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 1, :y 0}), :board [[0 0 0 2] [0 8 32 16] [2 4 16 2] [4 8 16 32]], :score 272N, :direction :right},
;           :left {:options [], :adv-options ({:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 3}), :board [[2 0 0 0] [8 32 16 0] [2 4 16 2] [4 8 16 32]], :score 337N, :direction :left},
;           :up {:options [], :adv-options ({:x 2, :y 0} {:x 2, :y 2} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [4 4 32 2] [0 8 0 32] [0 0 0 0]], :score 810N, :direction :up}}
;          {:down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0} {:x 1, :y 2}), :board [[0 2 0 0] [0 8 0 16] [2 4 32 2] [4 8 32 32]], :score 85/4, :direction :down},
;           :right {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 1, :y 0}), :board [[0 0 0 2] [0 8 32 16] [2 4 16 2] [4 8 16 32]], :score 272N, :direction :right},
;           :left {:options [], :adv-options ({:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 3}), :board [[2 0 0 0] [8 32 16 0] [2 4 16 2] [4 8 16 32]], :score 337N, :direction :left},
;           :up {:options [], :adv-options ({:x 2, :y 0} {:x 2, :y 2} {:x 3, :y 0} {:x 3, :y 2} {:x 3, :y 3}), :board [[2 2 32 16] [4 8 32 2] [0 4 0 32] [0 8 0 0]], :score 1335/2, :direction :up}}
;          {:down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0}), :board [[0 0 0 0] [0 8 2 16] [2 4 32 2] [4 8 32 32]], :score 85/4, :direction :down},
;           :right {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 1, :y 0}), :board [[0 0 0 2] [0 8 32 16] [2 4 16 2] [4 8 16 32]], :score 272N, :direction :right},
;           :left {:options [], :adv-options ({:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 3}), :board [[2 0 0 0] [8 32 16 0] [2 4 16 2] [4 8 16 32]], :score 337N, :direction :left},
;           :up {:options [], :adv-options ({:x 2, :y 0} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[2 8 2 16] [4 4 32 2] [0 8 32 32] [0 0 0 0]], :score 705/2, :direction :up}}
;          {:down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 1, :y 0} {:x 1, :y 2}), :board [[0 0 0 2] [0 8 0 16] [2 4 32 2] [4 8 32 32]], :score 20N, :direction :down},
;           :right {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 1, :y 0}), :board [[0 0 0 2] [0 8 32 16] [2 4 16 2] [4 8 16 32]], :score 272N, :direction :right},
;           :left {:options [], :adv-options ({:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 3}), :board [[2 0 0 0] [8 32 16 0] [2 4 16 2] [4 8 16 32]], :score 337N, :direction :left},
;           :up {:options [], :adv-options ({:x 2, :y 0} {:x 2, :y 2} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2}), :board [[2 8 32 2] [4 4 32 16] [0 8 0 2] [0 0 0 32]], :score 2685/4, :direction :up}}
;          {:down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0} {:x 1, :y 2}), :board [[0 0 0 0] [0 8 0 16] [4 4 32 2] [4 8 32 32]], :score 24N, :direction :down},
;           :left {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3}), :board [[0 0 0 0] [2 8 32 16] [2 4 16 2] [4 8 16 32]], :score 273N, :direction :left},
;           :right {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3}), :board [[0 0 0 0] [2 8 32 16] [2 4 16 2] [4 8 16 32]], :score 273N, :direction :right},
;           :up {:options [], :adv-options ({:x 2, :y 0} {:x 2, :y 2} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [4 4 32 2] [0 8 0 32] [0 0 0 0]], :score 810N, :direction :up}}],
;         :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0}), :board [[0 0 0 0] [0 8 32 16] [2 4 16 2] [4 8 16 32]], :score 340N, :direction :down},
;  :up {:options
;       [{
;         :down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0} {:x 1, :y 1} {:x 1, :y 2}), :board [[0 0 0 0] [0 0 0 16] [4 16 32 2] [4 4 32 32]], :score 0N, :direction :down},
;         :left {:options [], :adv-options ({:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [2 8 16 2] [2 4 16 32] [0 0 0 0]], :score 357N, :direction :left},
;         :right {:options [], :adv-options ({:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [2 8 16 2] [2 4 16 32] [0 0 0 0]], :score 357N, :direction :right},
;         :up {:options [], :adv-options ({:x 2, :y 0} {:x 2, :y 1} {:x 2, :y 2} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 16 32 16] [4 4 32 2] [0 0 0 32] [0 0 0 0]], :score 1029N, :direction :up}}
;        {:down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0} {:x 1, :y 1} {:x 1, :y 2}), :board [[0 0 0 0] [0 0 0 16] [4 16 32 2] [4 4 32 32]], :score 0N, :direction :down},
;         :left {:options [], :adv-options ({:x 2, :y 3} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [2 8 16 2] [4 16 32 0] [2 0 0 0]], :score 357N, :direction :left},
;         :right {:options [], :adv-options ({:x 2, :y 0} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2}), :board [[4 8 32 16] [2 8 16 2] [0 4 16 32] [0 0 0 2]], :score 357N, :direction :right},
;         :up {:options [], :adv-options ({:x 2, :y 0} {:x 2, :y 1} {:x 2, :y 2} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 16 32 16] [4 4 32 2] [0 0 0 32] [0 0 0 0]], :score 1029N, :direction :up}}
;        {:down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0} {:x 1, :y 2}), :board [[0 0 0 0] [0 16 0 16] [4 4 32 2] [2 2 32 32]], :score 96N, :direction :down},
;         :left {:options [], :adv-options ({:x 2, :y 3} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [2 8 16 2] [4 16 32 0] [2 0 0 0]], :score 357N, :direction :left},
;         :right {:options [], :adv-options ({:x 2, :y 0} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2}), :board [[4 8 32 16] [2 8 16 2] [0 4 16 32] [0 0 0 2]], :score 357N, :direction :right},
;         :up {:options [], :adv-options ({:x 2, :y 0} {:x 2, :y 2} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 16 32 16] [2 4 32 2] [0 2 0 32] [0 0 0 0]], :score 1755/2, :direction :up}}
;        {:left {:options [], :adv-options ({:x 2, :y 3} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [2 8 16 2] [4 16 32 0] [2 0 0 0]], :score 357N, :direction :left},
;         :right {:options [], :adv-options ({:x 2, :y 0} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2}), :board [[4 8 32 16] [2 8 16 2] [0 4 16 32] [0 0 0 2]], :score 357N, :direction :right},
;         :down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 0, :y 3} {:x 1, :y 0} {:x 1, :y 1}), :board [[0 0 0 0] [0 0 32 16] [4 16 32 2] [2 4 2 32]], :score 384N, :direction :down},
;         :up {:options [], :adv-options ({:x 2, :y 0} {:x 2, :y 1} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 16 32 16] [2 4 32 2] [0 0 2 32] [0 0 0 0]], :score 1755/2, :direction :up}}
;        {:down {:options [], :adv-options ({:x 0, :y 0} {:x 0, :y 1} {:x 0, :y 2} {:x 1, :y 0} {:x 1, :y 1} {:x 1, :y 2}), :board [[0 0 0 16] [0 0 0 2] [4 16 32 32] [2 4 32 2]], :score 0N, :direction :down},
;         :left {:options [], :adv-options ({:x 2, :y 3} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [2 8 16 2] [4 16 32 0] [2 0 0 0]], :score 357N, :direction :left},
;         :right {:options [], :adv-options ({:x 2, :y 0} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2}), :board [[4 8 32 16] [2 8 16 2] [0 4 16 32] [0 0 0 2]], :score 357N, :direction :right},
;         :up {:options [], :adv-options ({:x 2, :y 0} {:x 2, :y 1} {:x 2, :y 2} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2}), :board [[4 16 32 16] [2 4 32 2] [0 0 0 32] [0 0 0 2]], :score 1755/2, :direction :up}}],
;       :adv-options ({:x 2, :y 0} {:x 3, :y 0} {:x 3, :y 1} {:x 3, :y 2} {:x 3, :y 3}), :board [[4 8 32 16] [2 8 16 2] [0 4 16 32] [0 0 0 0]], :score 1785/4, :direction :up}}


;;
;; Be able to pick the best option
;;
;; Now before evaluating the payoff of each options (at the root), we first need to propagate the payoff
;; of each tree back to the root of the tree.
;; We don't know which move the opponent will play but when it is our turn, we can decide what to do.
;;
;; And that's where the particularity of the game strikes compared to a regular zero-sum game.
;; In a zero-sum game against a rational contender, we would pick the option that maximize
;; our minimal payoff (hence the name: minimax) since the other guy will try maximize his payoff too
;; and the sum of the payoffs is zero.
;; In our case, the contender randomly picks is move.
;; We can be less risk-averse and move from the maximization of the minimum payoff to the maximization
;; of the average payoff of the considered subtree (say at least the part that has been evaluated)
;;
;; That's what we do here, we sort our options - remember, just need to select the first move.
;; The ranking is done with the following criteria:
;; We value most the minimum payoff (0 doesn't necessarily mean you loose but... that's very likely
;; indicates the wrong direction). We also value the mean of the score in the considered tree.
;; Then we pick the one with the highest payoff.


(defn choose-move [choices]
  (last (sort-by #(+ (* 2 (:min (:value %))) (:mean (:value %))) choices))
  )

; (defn choose-move [choices]
;   (last (sort-by #(+ (* 2 (:min (:value %))) (:mean (:value %)) (:max (:value %))) choices))
; )

;; Be able to compute the payoff of the options.
;; This function return various values (mean/min/max/options-count) that
;; can later be used to select which move is better. Remember we only care of
;; choosing the move at the root of the tree.
;;
;; The strategy of the AI is based on minimax algorithm. With some adaptation as in our case
;; the contender is far from being rational since it picks a random blank element and set it
;; to a random value.
;;
;; Will come back on this later.
;;
;; First we need to functions: minimax-aggregate and choose-move. First to compute the payoff of deciding
;; to go in one direction. Second to select one direction.

;;
;; Be able to aggregate the payoff of the move in a given direction (depending on what the opponent do)
;;

(defn minimax-aggregate [vals]

  (hash-map :min (try (apply min (map #(:min %) vals)) (catch Exception e 0))
            :mean (try (utils/mean (map #(:mean %) vals)) (catch Exception e 0))
            :options-count (try (reduce + (map #(:options-count %) vals)) (catch Exception e 0))
            :max (try (apply max (map #(:max %) vals)) (catch Exception e 0))))

;;
;; Be able to pick the best option and aggregate the payoff recursively in the tree.
;;

(defn minimax-aux [tree]
  (let [options (:options tree)]
    (if (not (empty? options))
      ; general case:
      (let [choices [
                      {:direction :left :value (minimax-aggregate (filter identity (into [] (map #(minimax-aux (:left %)) options))))}
                      {:direction :right :value (minimax-aggregate (filter identity (into [] (map #(minimax-aux (:right %)) options))))}
                      {:direction :up :value (minimax-aggregate (filter identity (into [] (map #(minimax-aux (:up %)) options))))}
                      {:direction :down :value (minimax-aggregate (filter identity (into [] (map #(minimax-aux (:down %)) options))))}]]
        (assoc-in (:value (choose-move choices)) [:options-count] (reduce + (map #(get-in % [:value :options-count]) choices))))
      ; if we have reached a leaf:
      (hash-map :min (:score tree) :mean (float (:score tree)) :max (:score tree) :options-count 1)
      )))

; (defn minimax-aux [tree]
;   (let [options (:options tree)]
;     (if (not (empty? options))
;     (let [vals (concat
;                  (filter identity (into [] (map #(minimax-aux (:left %)) options)))
;                  (filter identity (into [] (map #(minimax-aux (:right %)) options)))
;                  (filter identity (into [] (map #(minimax-aux (:up %)) options)))
;                  (filter identity (into [] (map #(minimax-aux (:down %)) options))))]
;       (hash-map :min (try (apply min (map #(:min %) vals)) (catch Exception e 0 ))
;                 :mean (try (utils/mean (map #(:mean %) vals)) (catch Exception e 0 ))
;                 :options-count (try (reduce + (map #(:options-count %) vals)) (catch Exception e 0 ))
;                 :max (try (apply max (map #(:max %) vals)) (catch Exception e 0 ))))

;       (hash-map :min (:score tree) :mean (float (:score tree)) :max (:score tree) :options-count 1)
;     )))

;;
;; Be able to evaluate the payoff of a tree.
;;

(defn minimax [tree]
  (hash-map
    :left (minimax-aux (:left tree))
    :right (minimax-aux (:right tree))
    :up (minimax-aux (:up tree))
    :down (minimax-aux (:down tree))))

; (minimax (build 1000 [[0 8 0 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]]))

; (minimax (build 1000 [[0 8 0 16] [2 4 16 2] [2 4 8 0] [2 4 0 16]]))

; (minimax (build 1000 [[0 8 0 16] [2 0 16 0] [2 4 8 0] [0 4 0 16]]))

; (minimax (build 400 [[0 2 4 8]
;                      [2 4 0 0]
;                      [0 8 0 2]
;                      [4 16 64 128]]))


;;
;; Be able to pick the best option for next move (at the root).
;;

;
; TODO optimize to put in bucket and the select among the moves in the bucket
(defn optimal-minimax-move [board]
  (let [scoring (minimax (build 1000 board))]
    (let [t (utils/fmap (fn [k v] (hash-map :min (:min v) :mean (:mean v) :max (:max v) :direction k :options-count (:options-count v))) scoring)]
      (let [options (sort-by #(+ (* 2 (:min %)) (:mean %)) t)]
        (doall (map println options))
        (:direction (last options)))
      )))


; (optimal-minimax-move [[64 8 32 16]
;                        [2 4 16 2]
;                        [2 4 8 16]
;                        [2 4 8 16]])

;;
;; Be able to compute the score (approximativaly seen the first value can be 4)
;; (assuming the score accumulated for a single value v is (log(2,v)-1)*v)

(defn score [board]
  (reduce + (map #(if (< 2 %) (* % (- (/ (Math/log %) (Math/log 2)) 1)) 0) (flatten board))))

; (score [[128 4 0 0]
;         [16 0 0 0]
;         [4 0 0 0]
;         [0 0 0 0]])


;;
;;
;; Be able to play a turn with our AI
;; This function take a board and return a board so it's easy to iterate it.
;;

(defn play-smartly [board]

  (let [move (optimal-minimax-move board)]
    (board/print-board board)

    (println (str "move=" move))

    (println (str "score=" (score board)))
    
    (play-a-turn board move)))

;;  (nth
;;         (iterate play-smartly board/test-board)
;;          4000)

 ; (time
 ;   (try
 ;     (nth
 ;       (iterate play-smartly board/test-board)
 ;        6000)
 ;     (catch Exception e
 ;     	(println (.getMessage e)))))
