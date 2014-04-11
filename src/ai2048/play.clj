(ns ai2048.play
  (:require [ai2048.board :as board]
            [ai2048.utils :as utils]
            [clojure.pprint]))

;; Be able to play a real turn:
;; - make a move 
;; - get a random new element
(defn play-a-turn [board direction] 
  (board/generate-new-rand-element (board/move-pieces board direction)))

; (board/print-board (play-a-turn (nth (iterate board/generate-new-rand-element (board/new-board)) 2) :up))

;; Be able to evaluate the score of a board

(defn eval-option [board payoff-fn direction] (let [new-board (board/move-pieces board direction)] {:board new-board :score (payoff-fn new-board) :direction direction}))

; (eval-option board/test-board #(count (board/board-blank-elements %)) :up)
; (eval-option board/test-board #(count (board/board-blank-elements %)) :down)
; (eval-option board/test-board #(count (board/board-blank-elements %)) :left)
; (eval-option board/test-board #(count (board/board-blank-elements %)) :right)


;; Be able to pick the largest elements of the board with their coordinate (use the coordinate for the sorting so that all elements
;; with the same value are kind of sorted according to their positions)

(defn n-biggest-elements [n board]  (take-last n (sort-by #(+ (* 131072 (:val %)) (:x %) (:y %)) (filter #(not (= 0 (:val %))) (board/rank-xy board)))))


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

; (payoff-fn [[0 0 0 0] [0 8 0 16] [4 4 32 2] [4 8 32 32]])


;;
;; This is the one
;; it can reach 2048!!
;;

(defn payoff-fn [board] 
  (let [payoff-coef [ 0 0 0 0
                    0 0 0 0
                    0 0 0 0
                    1 1 0 0]]
    (* (/ (count (board/board-blank-elements board)) 16)
       (reduce + 
            (map 
              (fn [x y] (* (utils/power 2 x) y)) 
              (flatten (reverse board)) 
              payoff-coef)))
  ))


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

(defn minimax-aux [tree]
  (let [options (:options tree)]    
    (if (not (empty? options))
    (let [vals (concat 
                 (filter identity (into [] (map #(minimax-aux (:left %)) options))) 
                 (filter identity (into [] (map #(minimax-aux (:right %)) options))) 
                 (filter identity (into [] (map #(minimax-aux (:up %)) options)))  
                 (filter identity (into [] (map #(minimax-aux (:down %)) options))))]
      (hash-map :min (try (apply min (map #(:min %) vals)) (catch Exception e 0 ))
                :mean (try (utils/mean (map #(:mean %) vals)) (catch Exception e 0 ))
                :options-count (try (reduce + (map #(:options-count %) vals)) (catch Exception e 0 ))
                :max (try (apply max (map #(:max %) vals)) (catch Exception e 0 ))))
    
      (hash-map :min (:score tree) :mean (float (:score tree)) :max (:score tree) :options-count 1)
    )))

(defn minimax [tree] 
  (hash-map 
    :left (minimax-aux (:left tree))
    :right (minimax-aux (:right tree))
    :up (minimax-aux (:up tree))
    :down (minimax-aux (:down tree))))

; (minimax (build 1000 [[0 8 0 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]]))

; (minimax (build 1000 [[0 8 0 16] [2 4 16 2] [2 4 8 0] [2 4 0 16]]))

; (minimax (build 1000 [[0 8 0 16] [2 0 16 0] [2 4 8 0] [0 4 0 16]]))

;;
;; This is the one
;; it can reach 2048!!
;;
;; We want:
;; - as many blank element as possible
;; - as big value as possible in the two positions in one corner

(defn payoff-fn [board] 
  (let [payoff-coef [ 0 0 0 0
                      0 0 0 0
                      0 0 0 0
                      1 1 0 0]]
    (* (/ (count (board/board-blank-elements board)) 16)
       (reduce + 
            (map 
              (fn [x y] (* (utils/power 2 x) y)) 
              (flatten (reverse board)) 
              payoff-coef)))
  ))


; (minimax (build 4 [[0 2 4 8]
;                    [2 4 0 0]
;                    [0 8 0 2]
;                    [4 16 64 128]]))

;;
;; Be able to pick the best option
;; 
;; That's where the particularity of the game strikes.
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
;; indicates the wrong direction). We also value the mean and the max of the score in the considered tree.
;; Then we pick the one with the highest payoff.

;
; TODO optimize to put in bucket and the select among the moves in the bucket
(defn optimal-minimax-move [board]
    (let [scoring (minimax (build 1000 board))] 
      (let [t (utils/fmap (fn [k v] (hash-map :min (:min v) :mean (:mean v) :max (:max v) :direction k :options-count (:options-count v))) scoring)]
        (let [options (sort-by #(+ (* 2 (:min %)) (:max %) (:mean %)) t)]
          (doall (map println options))
        (:direction (last options)))
)))


; (optimal-minimax-move [[64 8 32 16] 
;                        [2 4 16 2] 
;                        [2 4 8 16] 
;                        [2 4 8 16]])

;;
;;
;; Be able to play a turn with our AI
;; This function take a board and return a board so it's easy to iterate it.
;;

(defn play-smartly [board]
  
    (let [move (optimal-minimax-move board)]
        (board/print-board  board)
        
        (println (str "move=" move))

        (play-a-turn board move)))

; (time 
;   (try 
;     (nth 
;       (iterate play-smartly test-board)
;        4000) 
;     (catch Exception e 
;     	(println (.getMessage e)))))
