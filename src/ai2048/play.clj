(ns ai2048.play
  (:require [ai2048.board :as board]
            [ai2048.utils :as utils]
            [clojure.pprint]))

;; Be able to play a real turn:
;; - make a move 
;; - get a random new element
(defn play-a-turn [board direction] 
  (board/generate-new-rand-element (board/move-pieces board direction)))

(board/print-board (play-a-turn (nth (iterate board/generate-new-rand-element (board/new-board)) 2) :up))

;; Be able to evaluate the score of a board

(count (board/board-blank-elements (board/move-pieces board/test-board :up)))
(count (board/board-blank-elements (board/move-pieces board/test-board :down)))
(count (board/board-blank-elements (board/move-pieces board/test-board :left)))
(count (board/board-blank-elements (board/move-pieces board/test-board :right)))


(defn eval-option [board cost direction] (let [new-board (board/move-pieces board direction)] {:board new-board :score (cost new-board) :direction direction}))

(eval-option test-board #(count (board/board-blank-elements %)) :up)
(eval-option test-board #(count (board/board-blank-elements %)) :down)
(eval-option test-board #(count (board/board-blank-elements %)) :left)
(eval-option test-board #(count (board/board-blank-elements %)) :right)

(reduce + (map * (flatten (reverse test-board)) (iterate inc 1)))


(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate inc 1))) :up)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate inc 1))) :down)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate inc 1))) :left)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate inc 1))) :right)

(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :up)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :down)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :left)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :right)

(print-board test-board)

(print-board (:board (last (sort-by #(:score %) [
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :up)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :down)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :left)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :right)]))))

(print-board (let [new-board (:board (last (sort-by #(:score %) [
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :up)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :down)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :left)
(eval-option test-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :right)])))]
  
  (:board (last (sort-by #(:score %) [
(eval-option new-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :up)
(eval-option new-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :down)
(eval-option new-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :left)
(eval-option new-board #(reduce + (map * (flatten (reverse %)) (iterate (fn [x] (* 2 x)) 1))) :right)])))))

(take 5 (iterate #(play-a-turn % :up) (new-board)))


; (defn cost-fn [board] 
;   (let [cost-coef [1 1 1 1
;                    2 2 2 2
;                    3 3 3 3 
;                   64 32 16 8]]
;     (reduce + 
;             (map 
;               (fn [x y] (* (power 2 x) y)) 
;               (flatten (reverse board)) 
;               cost-coef))
;   ))

(defn n-biggest-elements [n board]  (take-last n (sort-by #(+ (* 131072 (:val %)) (:x %) (:y %)) (filter #(not (= 0 (:val %))) (rank-xy board)))))
; (defn n-biggest-elements [n board]  (map #(dissoc % :val) (take-last n (sort-by #(:val %) (rank-xy board)))))
(n-biggest-elements 16 [[1 2 2 4]
                      [5 0 0 8]
                      [2 2 2 12]
                      [13 14 15 16]])



  
(defn cost-fn [board] 
  (let [big-guys (n-biggest-elements 6 board)]
    (* (count (board-blank-elements board)) 
       (+ 1
          (reduce + (map (fn [x y] (if (= (manhattan-distance x y) 1) '1 '0)) big-guys (drop 1 big-guys))))
       (let [biggest (last big-guys)]
       (if (or 
             (= (:x biggest) 0)
             (= (:x biggest) 3)
             (= (:y biggest) 0)
             (= (:y biggest) 3)) '2 '1)
             
       ))))

(cost-fn [[0 0 0 0] [0 8 0 16] [4 4 32 2] [4 8 32 32]])

; (defn cost-fn [board] 
;   (count (board-blank-elements board)))

; (cost-fn test-board)

; (defn cost-fn [board] 
;   (let [cost-coef [0  0  0  0
;                    0  0  0  0
;                    0  0  0  0
;                    1  1  1  1]]
;     (/ (* (count (board-blank-elements board)) 
;        (reduce + 
;             (map 
;               (fn [x y] (* (power 2 x) y)) 
;               (flatten (reverse board)) 
;               cost-coef)))
;        16)
;   ))

; (defn cost-fn [board] 
;   (let [cost-coef [1  1  1  1
;                    2  2  2  2
;                    3  3  3  3
;                    20  20  4  4]]
;     (/ (* (count (board-blank-elements board)) 
;        (reduce + 
;             (map 
;               (fn [x y] (* x y)) 
;               (flatten (reverse board)) 
;               cost-coef)))
;        16)
;   ))

(cost-fn [[1 2 3 4]
            [5 6 7 8]
            [9 10 11 12]
            [13 0 15 16]])

; (defn cost-fn [board] 
;   (let [coef [[1 1 1 1]
;               [2 2 2 2]
;               [3 3 3 3]
;               [64 32 16 8]]]
;   (reduce + (flatten 
;               (list (for [x (range board-size) y (range board-size)]
;                 (let [val (get-element board x y)]
                 
;                    (* ((coef x) y) (power 2 val))
                 
;                    )))))))


(defn optimize-move [board]   
  (:direction (last (sort-by #(:score %) [
    (eval-option board cost-fn :up)
    (eval-option board cost-fn :down)
    (eval-option board cost-fn :left)
    (eval-option board cost-fn :right)]))))


(defn options-move [board]   
  (sort-by #(:score %) [
    (eval-option board cost-fn :up)
    (eval-option board cost-fn :down)
    (eval-option board cost-fn :left)
    (eval-option board cost-fn :right)]))

(optimize-move test-board)
(options-move [[8 4 2 2] [4 8 8 2] [64 16 16 4] [16 2 32 16]])
(options-move [[0 0 0 0] [2 0 0 0] [8 0 0 0] [8 4 0 2]])

; {
;  :score XXX
;  :direction XXX
;  :board XXX
;  :options {XXX}
; }


; (defn make-options [board] 
;   (into {} (map (fn [x] (hash-map (:direction x) x)) (options-move board))))

(defn make-options [board] 
  (into {} (map (fn [x] (hash-map (:direction x) (assoc x :adv-options (board-blank-elements (:board x))))) (options-move board))))

(make-options test-board)

; (defn build [n board]
;     (when (pos? n)
;       (let [options (make-options board)]
;         (assoc-in 
;           (assoc-in 
;             (assoc-in 
;               (assoc-in 
;                 options 
;                 [:left :options] 
;                 (build (dec n) (get-in options [:left :board]))) 
;                 [:right :options] 
;                 (build (dec n) (get-in options [:right :board]))) 
;                 [:up :options] 
;                 (build (dec n) (get-in options [:up :board]))) 
;                 [:down :options] 
;                 (build (dec n) (get-in options [:down :board]))) 
;         )))

(defn build [depth credit board]
  (println (str "credit=" credit " depth=" depth))
    (when (pos? credit)
      (let [options (make-options board)]
        (assoc-in 
          (assoc-in 
            (assoc-in 
              (assoc-in 
                options 
                [:left :options] 
                (let [opt (get-in options [:left :adv-options])]
                (into [] (filter identity (map #(build (inc depth) (- (/ credit 4) (* 4 (count opt))) (set-element (get-in options [:left :board]) (:x %) (:y %) 2)) opt)))))
                [:right :options] 
                (let [opt (get-in options [:right :adv-options])]
                (into [] (filter identity (map #(build (inc depth) (- (/ credit 4) (* 4 (count opt))) (set-element (get-in options [:right :board]) (:x %) (:y %) 2)) opt))))) 
                [:up :options] 
                (let [opt (get-in options [:up :adv-options])]
                (into [] (filter identity (map #(build (inc depth) (- (/ credit 4) (* 4 (count opt))) (set-element (get-in options [:up :board]) (:x %) (:y %) 2)) opt))))) 
                [:down :options] 
                (let [opt (get-in options [:down :adv-options])]
                (into [] (filter identity (map #(build (inc depth) (- (/ credit 4) (* 4 (count opt))) (set-element (get-in options [:down :board]) (:x %) (:y %) 2)) opt))))) 
        )))

(build 0 300 [[0 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]])


(defn minimax-aux [tree]
  (let [options (:options tree)]    
    (if (not (empty? options))
    (let [vals (concat 
                 (filter identity (into [] (map #(minimax-aux (:left %)) options))) 
                 (filter identity (into [] (map #(minimax-aux (:right %)) options))) 
                 (filter identity (into [] (map #(minimax-aux (:up %)) options)))  
                 (filter identity (into [] (map #(minimax-aux (:down %)) options))))]
      (hash-map :min (try (apply min (map #(:min %) vals)) (catch Exception e 0 ))
                :mean (try (mean (map #(:mean %) vals)) (catch Exception e 0 ))
                :options-count (try (reduce + (map #(:options-count %) vals)) (catch Exception e 0 ))
                :max (try (apply max (map #(:max %) vals)) (catch Exception e 0 ))))
    
      (hash-map :min (:score tree) :mean (float (:score tree)) :max (:score tree) :options-count 1)
    )))



; (defn minimax-aux [tree]
;   (if-let [options (:options tree)]
;     (let [vals [(minimax-aux (:left options)) (minimax-aux (:right options)) (minimax-aux (:up options)) (minimax-aux (:down options))]]
      
;       (hash-map :min (apply min (map #(:min %) vals))
;                 :max (apply max (map #(:max %) vals))))
;     (hash-map :min (:score tree) :max (:score tree))))


; (defn minimax [tree] 
;   (hash-map 
;     :left (minimax-aux (:left tree))
;     :right (minimax-aux (:right tree))
;     :up (minimax-aux (:up tree))
;     :down (minimax-aux (:down tree))))


(defn minimax [tree] 
  (hash-map 
    :left (minimax-aux (:left tree))
    :right (minimax-aux (:right tree))
    :up (minimax-aux (:up tree))
    :down (minimax-aux (:down tree))))

(minimax (build 1000 [[0 8 0 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]]))

(minimax (build 1000 [[0 8 0 16] [2 4 16 2] [2 4 8 0] [2 4 0 16]]))

(minimax (build 1000 [[0 8 0 16] [2 0 16 0] [2 4 8 0] [0 4 0 16]]))

;
;
; XXXX this is the one
;
;

(defn cost-fn [board] 
  (let [cost-coef [ 0 0 0 0
                    0 0 0 0
                    0 0 0 0
                    1 1 0 0]]
    (* (/ (count (board-blank-elements board)) 16)
       (reduce + 
            (map 
              (fn [x y] (* (power 2 x) y)) 
              (flatten (reverse board)) 
              cost-coef)))
  ))


(minimax (build 4 [[0 2 4 8]
                   [2 4 0 0]
                   [0 8 0 2]
                   [4 16 64 128]]))

(build 4 [[0 2 4 8]
                   [2 4 8 32]
                   [0 8 16 2]
                   [4 16 64 128]])




;
; TODO optimize to put in bucket and the select among the moves in the bucket
(defn optimal-minimax-move [board]
    (let [scoring (minimax (build 1000 board))] 
      (let [t (fmap (fn [k v] (hash-map :min (:min v) :mean (:mean v) :max (:max v) :direction k :options-count (:options-count v))) scoring)]
        (let [options (sort-by #(+ (* 2 (:min %)) (:max %) (:mean %)) t)]
          (doall (map println options))
        (:direction (last options)))
)))


(optimal-minimax-move [[64 8 32 16] 
                       [2 4 16 2] 
                       [2 4 8 16] 
                       [2 4 8 16]])

; {:left {:options 
;         {:left {:options nil, :board [[2 0 0 0] [2 0 0 0] [0 0 0 0] [0 0 0 0]], :score 0, :direction :left}, 
;          :right {:options nil, :board [[0 0 0 2] [0 0 0 2] [0 0 0 0] [0 0 0 0]], :score 0, :direction :right}, 
;          :up {:options nil, :board [[4 0 0 0] [0 0 0 0] [0 0 0 0] [0 0 0 0]], :score 32, :direction :up}, 
;          :down {:options nil, :board [[0 0 0 0] [0 0 0 0] [0 0 0 0] [4 0 0 0]], :score 2048, :direction :down}}, 
;         :board [[2 0 0 0] [2 0 0 0] [0 0 0 0] [0 0 0 0]], 
;         :score 0, 
;         :direction :left}, 
;  :right {:options 
;          {:left {:options nil, :board [[2 0 0 0] [2 0 0 0] [0 0 0 0] [0 0 0 0]], :score 0, :direction :left}, 
;           :right {:options nil, :board [[0 0 0 2] [0 0 0 2] [0 0 0 0] [0 0 0 0]], :score 0, :direction :right}, 
;           :up {:options nil, :board [[0 0 0 4] [0 0 0 0] [0 0 0 0] [0 0 0 0]], :score 32, :direction :up}, 
;           :down {:options nil, :board [[0 0 0 0] [0 0 0 0] [0 0 0 0] [0 0 0 4]], :score 256, :direction :down}}, 
;          :board [[0 0 0 2] [0 0 0 2] [0 0 0 0] [0 0 0 0]], :score 0, 
;          :direction :right}, 
;  :up {:options 
;       {:up {:options nil, :board [[0 4 0 0] [0 0 0 0] [0 0 0 0] [0 0 0 0]], :score 32, :direction :up}, 
;        :left {:options nil, :board [[4 0 0 0] [0 0 0 0] [0 0 0 0] [0 0 0 0]], :score 32, :direction :left}, 
;        :right {:options nil, :board [[0 0 0 4] [0 0 0 0] [0 0 0 0] [0 0 0 0]], :score 32, :direction :right}, 
;        :down {:options nil, :board [[0 0 0 0] [0 0 0 0] [0 0 0 0] [0 4 0 0]], :score 1024, :direction :down}}, 
;       :board [[0 4 0 0] [0 0 0 0] [0 0 0 0] [0 0 0 0]], 
;       :score 32, :direction 
;       :up}, 
;  :down {:options 
;         {:up {:options nil, :board [[0 4 0 0] [0 0 0 0] [0 0 0 0] [0 0 0 0]], :score 32, :direction :up}, 
;          :right {:options nil, :board [[0 0 0 0] [0 0 0 0] [0 0 0 0] [0 0 0 4]], :score 256, :direction :right}, 
;          :down {:options nil, :board [[0 0 0 0] [0 0 0 0] [0 0 0 0] [0 4 0 0]], :score 1024, :direction :down}, 
;          :left {:options nil, :board [[0 0 0 0] [0 0 0 0] [0 0 0 0] [4 0 0 0]], :score 2048, :direction :left}}, 
;         :board [[0 0 0 0] [0 0 0 0] [0 0 0 0] [0 4 0 0]], 
;         :score 1024, 
;         :direction :down}}


  
(defn play-optimally [board]
  
    (let [move (optimal-minimax-move board)]
        (print-board  board)
        
        (println (str "move=" move))

        (play-a-turn board move)))

(time (try (nth (iterate play-optimally test-board) 4000) (catch Exception e (println (.getMessage e)))))


; (time (try (nth (iterate play-optimally [[2 2048 4 2]
;                                          [4 4 64 32]
;                                          [0 2 32 4]
;                                          [0 0 0 0]]) 1000) (catch Exception e (println (.getMessage e)))))



(print-board [[64 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]])
(optimize-move [[64 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]])
(options-move [[64 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]])
