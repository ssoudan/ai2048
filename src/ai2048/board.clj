(ns ai2048.board)
(use '[clojure.core.match :only (match)])

;; Create a new empty board
(def board-size 4)


;; (x,y)
;;
;;         y
;; (0,0)  --->
;;     [[. . .] 
;; x |  [. . .]
;;   |  [. . .]]
;;   v
;;


(defn new-board [] (vec (repeat board-size (vec (to-array (repeat board-size 0))))))

(new-board)

;; Be able to display a board
(defn print-board [board] (map (fn [x] (print (str x "\n")) ) (seq board)))

(print-board [[1 2] [3 4]])

(print-board (new-board))

;; Be able to pick on element of the board

(defn get-element [board x y] ((board x) y))

(get-element [[ 1 2 3 1 ] 
              [ 3 3 1 1 ]
              [ 0 1 1 1 ]
              [ 3 2 1 1 ]] 2 0)

(get-element [[2 4] [8 16]] 0 0)

;; Label value with coordinates

(defn distribute-x [x-rank elements] (map (fn [c] { :x x-rank :y (:y c) :val (:val c)}) elements))

(distribute-x 2 [{:val 1 :y "y"}  {:val 2 :y "y2"}])

(defn rank-x [arg] (map (fn [a b] {:val a :x b}) arg (range))) 
(defn rank-y [arg] (map (fn [a b] {:val a :y b}) arg (range))) 

(defn rank-xy [board] (flatten (map #(distribute-x (:x %) (:val %)) (rank-x (map rank-y (lazy-seq board))))))

(rank-xy [[0 10 20] [1 11 21] [2 12 22]])

;; Select element that are blank

(defn blank-elements [ranked-elements]  (map #(dissoc % :val) (filter #(=(:val %) 0) ranked-elements)))

(blank-elements (rank-xy [[0 10 20] [1 11 21] [2 12 22]]))

(blank-elements (rank-xy [[0 10 20] [1 0 21] [2 0 22]]))

;; Pick a random blank element

(defn board-blank-elements [board] (blank-elements (rank-xy board)))

(board-blank-elements     [[ 1 2 3 1 ] 
                           [ 3 3 1 1 ]
                           [ 1 1 1 1 ]
                           [ 3 2 1 1 ]]) 

(board-blank-elements     [[ 1 2 3 1 ] 
                           [ 3 3 1 1 ]
                           [ 0 1 1 1 ]
                           [ 3 2 1 1 ]]) 

(board-blank-elements     [[ 1 2 3 1 ] 
                           [ 3 3 1 1 ]
                           [ 0 0 0 1 ]
                           [ 3 2 1 1 ]]) 


(defn rand-board-blank-element [board] 
  (let [b-elements (board-blank-elements board)]
    (if (empty? b-elements)
    ;; then
    (throw (Exception. (str "game over! max=" (apply max (flatten board)) ",board=" board)))
    ;; else
    (rand-nth b-elements))))

(rand-board-blank-element [[ 1 2 3 1 ] 
                           [ 3 3 1 1 ]
                           [ 0 1 1 1 ]
                           [ 3 2 1 1 ]]) 


; (rand-board-blank-element [[ 1 2 3 1 ] 
;                            [ 3 3 1 1 ]
;                            [ 1 1 1 1 ]
;                            [ 3 2 1 1 ]]) 

;; Be able to apply a function on a particular row 

(defn apply-at [board idx f] 
  (vec (concat (subvec board 0 idx) (list (f (board idx))) (subvec board (+ idx 1)))))

(apply-at [1 2] 1 (fn [x] 0))

(apply-at [[1 2] [3 4]] 1 (fn [x] x))

;; Be able to apply a function on a particular element of the board

(defn apply-at-xy [board x y f] 
  (apply-at board x (fn [row] (apply-at row y f))))


(apply-at-xy [[1 2 3 4]
              [2 3 4 5]
              [3 4 5 6]
              [4 5 6 7]] 2 2 (fn [x] 0))

;; Be able to replace an element of the board

(defn set-element [board x y val] (apply-at-xy board x y (fn [e] val)))

(set-element [[1 2 3 4]
              [2 3 4 5]
              [3 4 5 6]
              [4 5 6 7]] 2 2 0)

(set-element [[1 2 3 4]
              [2 3 4 5]
              [3 4 5 6]
              [4 5 6 7]] 0 0 0)

;; Be able to merge elements in a row according to the rules
;
; assuming we always fo from left to right
; assuming x != 0
;
;-- special case of #6 --0 #1  [0 0 0 0] -> [0 0 0 0] 
;1 #2  [x 0 0 0] -> [0 0 0 x] 
;1 #3  [0 x 0 0] -> [0 0 0 x] 
;1 #4  [0 0 x 0] -> [0 0 0 x] 
;1 #5  [0 0 0 x] -> [0 0 0 x] 
;2 #6  [x y 0 0] -> [0 0 x y]    if y != 0 and y != x 
;2 #7  [x y 0 0] -> [0 0 0 2.y]  if y == x 
;2 #8  [x 0 y 0] -> [0 0 x y]    if y != 0 and y != x 
;2 #9  [x 0 y 0] -> [0 0 0 2.y]  if y == x 
;2 #10 [x 0 0 y] -> [0 0 x y]    if y != 0 and y != x 
;2 #11 [x 0 0 y] -> [0 0 0 2.y]  if y == x 
;2 #12 [0 x y 0] -> [0 0 x y]    if y != 0 and y != x
;2 #13 [0 x y 0] -> [0 0 0 2.y]  if y == x
;2 #14 [0 x 0 y] -> [0 0 x y]    if y != 0 and y != x
;2 #15 [0 x 0 y] -> [0 0 0 2.y]  if y == x
;2 #16 [0 0 x y] -> [0 0 x y]    if y != 0 and y != x
;2 #17 [0 0 x y] -> [0 0 0 2.y]  if y == x
;3 #18 [x y z 0] -> [0 x y z]    if y != 0 and y != x and y != z and z != 0 
;3 #19 [x y z 0] -> [0 0 x 2.z]  if y != 0 and y != x and y == z
;3 #20 [x y z 0] -> [0 0 2.y z]  if y != 0 and y == x and y != z and z != 0
;3 #21 [x y 0 z] -> [0 x y z]    if y != 0 and y != x and y != z
;3 #22 [x y 0 z] -> [0 0 x 2.z]  if y != 0 and y != x and y == z
;3 #23 [x y 0 z] -> [0 0 2.y z]  if y != 0 and y == x 
;3 #24 [x 0 y z] -> [0 x y z]    if y != 0 and y != x and y != z
;3 #25 [x 0 y z] -> [0 0 x 2.z]  if y != 0 and y != x and y == z
;3 #26 [x 0 y z] -> [0 0 2.y z]  if y != 0 and y == x 
;3 #27 [0 x y z] -> [0 x y z]    if y != 0 and y != x and y != z
;3 #28 [0 x y z] -> [0 0 x 2.z]  if y != 0 and y != x and y == z
;3 #29 [0 x y z] -> [0 0 2.y z]  if y != 0 and y == x 

;4 #30 [w x y z] -> [w x y z]    if y != 0 and w != x and y != x and y != z
;4 #31 [w x y z] -> [0 w x 2.z]  if y != 0 and w != x and y != x and y == z
;4 #32 [w x y z] -> [0 0 2.x 2.z] if y != 0 and w == x and y != x and y == z
;4 #33 [w x y z] -> [0 w 2.y z]  if y != 0 and w != x and y == x and y != z

 (defn evolve-row [row]
  (match row
    [ x 0 0 0 ] [ 0 0 0 x ]
    [ 0 x 0 0 ] [ 0 0 0 x ]
    [ 0 0 x 0 ] [ 0 0 0 x ]
    [ 0 0 0 x ] [ 0 0 0 x ]
    [ x y 0 0 ] (if (= x y) (vec [0 0 0 (+ x y)]) (vec [0 0 x y]))
    [ x 0 y 0 ] (if (= x y) (vec [0 0 0 (+ x y)]) (vec [0 0 x y]))
    [ x 0 0 y ] (if (= x y) (vec [0 0 0 (+ x y)]) (vec [0 0 x y]))
    [ 0 x 0 y ] (if (= x y) (vec [0 0 0 (+ x y)]) (vec [0 0 x y]))
    [ 0 0 x y ] (if (= x y) (vec [0 0 0 (+ x y)]) (vec [0 0 x y]))
    [ 0 x y 0 ] (if (= x y) (vec [0 0 0 (+ x y)]) (vec [0 0 x y]))
    [ x y z 0 ] (if (= y z) (vec [0 0 x (+ y z)]) (if (= x y) (vec [0 0 (+ x y) z]) (vec [0 x y z]))) 
    [ x y 0 z ] (if (= y z) (vec [0 0 x (+ y z)]) (if (= x y) (vec [0 0 (+ x y) z]) (vec [0 x y z])))    
    [ x 0 y z ] (if (= y z) (vec [0 0 x (+ y z)]) (if (= x y) (vec [0 0 (+ x y) z]) (vec [0 x y z])))    
    [ 0 x y z ] (if (= y z) (vec [0 0 x (+ y z)]) (if (= x y) (vec [0 0 (+ x y) z]) (vec [0 x y z])))    
    [ w x y z ] (if (= y z) (if (= w x) (vec [0 0 (+ w x) (+ y z)]) (vec [0 w x (+ y z)])) (if (= x y) (vec [0 w (+ x y) z]) (if (= w x) (vec [0 (+ w x) y z ]) (vec [w x y z]))))
    :else (throw (Exception. "what's that?"))))

(evolve-row [ 1 1 0 1 ])
(evolve-row [ 1024 1024 1 1 ])
(evolve-row [ 1 1 2 0 ])

;; Be able to rotate the board so all move appears to be left to right
(reverse [[1 2 3] [4 5 6]])

(defn rotate [board] (vec (apply map vector board)))

(rotate (rotate [[1 1 1]
        [2 2 2]
        [3 3 3]]))

(defn reverse-board [board] (vec (map #(-> % reverse vec) board)))

(reverse-board [[1 2 3]
                [4 5 6]
                [7 8 9]])

(defn to-right [board] board)
(defn to-left [board] (reverse-board board))
(defn to-down [board] (rotate board))
(defn to-up [board] (-> board rotate vec reverse-board))

(to-up [[1 2 3]
        [4 5 6]
        [7 8 9]])

(defn from-right [board] board)
(defn from-left [board] (reverse-board board))
(defn from-up [board] (rotate (reverse-board board)))
(defn from-down [board] (rotate board))

(from-left (to-left [[1 2 3]
                     [4 5 6]
                     [7 8 9]]))

(from-right (to-right [[1 2 3]
                       [4 5 6]
                       [7 8 9]]))

(from-up (to-up [[1 2 3]
                 [4 5 6]
                 [7 8 9]]))

(from-down (to-down [[1 2 3]
                     [4 5 6]
                     [7 8 9]]))

;; Be able to apply the evolution rules to each row
(defn evolve-board [board] (map evolve-row board))

(evolve-board [[1 1 2 0]
               [1 1 2 0]
               [1 1 2 0]
               [1 1 2 0]])


;; Be able to play a turn in every directions
(defn play-right [board] (-> board to-right evolve-board from-right vec))
(defn play-left [board] (-> board to-left evolve-board from-left vec))
(defn play-up [board] (-> board to-up evolve-board from-up vec))
(defn play-down [board] (-> board to-down evolve-board from-down vec))

(play-right [[1 1 2 0]
             [1 1 2 0]
             [1 1 2 0]
             [1 1 2 0]])

(play-up [[1 1 2 0]
          [1 1 2 0]
          [1 1 2 0]
          [1 1 2 0]])


(play-left [[1 1 2 0]
            [1 1 2 0]
            [1 1 2 0]
            [1 1 2 0]])

(to-up [[1 1 2 0]
          [1 1 2 0]
          [1 1 2 0]
          [1 1 2 0]])

;; Be able to play

(defn move-pieces [board direction] 
  (case direction
    :left (play-left board)
    :right (play-right board)
    :up (play-up board)
    :down (play-down board)))


(move-pieces [[1 1 2 0]
              [1 1 2 0]
              [1 1 2 0]
              [1 1 2 0]] :left)

(move-pieces [[1 1 2 0]
              [1 1 2 0]
              [1 1 2 0]
              [1 1 2 0]] :right) 

(move-pieces [[1 1 2 0]
              [1 1 2 0]
              [1 1 2 0]
              [1 1 2 0]] :up) 

(move-pieces [[1 1 2 0]
              [1 1 2 0]
              [1 1 2 0]
              [1 1 2 0]] :down) 

(let [board [[1 1 2 0]
             [1 1 2 0]
             [1 1 2 0]
             [1 1 2 0]]]
(reduce move-pieces board [:left :left :up :up]))

;; Play a sequence of moves on a board

(defn play-sequence [board moves]
  (reduce move-pieces board moves))

(let [board [[1 1 2 0]
             [1 1 2 0]
             [1 1 2 0]
             [1 1 2 0]]]
(-> (play-sequence board [:left :left :up :up]) print-board))

;; Be able to generate a new random element in a blank cell

(if (= (rand-int 10) 0) 4 2)

(defn generate-new-rand-element [board] 
  (let [blank (rand-board-blank-element board)] 
    (set-element board (:x blank) (:y blank) (if (= (rand-int 10) 0) 4 2))))

(-> (new-board) generate-new-rand-element print-board)

(let [board (new-board)]
  (nth (iterate generate-new-rand-element board) 5 ))

;; Be able to play a real turn:
;; - make a move 
;; - get a random new element
(defn play-a-turn [board direction] 
  (generate-new-rand-element (move-pieces board direction)))

(print-board (play-a-turn (nth (iterate generate-new-rand-element (new-board)) 2) :up))

;; Be able to evaluate the score of a board

(count [1 2 3 4])
(def test-board [[0 2 0 0]
                 [0 2 0 0]
                 [0 0 0 0]
                 [0 0 0 0]])

(move-pieces test-board :up)

(count (board-blank-elements (move-pieces test-board :up)))
(count (board-blank-elements (move-pieces test-board :down)))
(count (board-blank-elements (move-pieces test-board :left)))
(count (board-blank-elements (move-pieces test-board :right)))


(defn eval-option [board cost direction] (let [new-board (move-pieces board direction)] {:board new-board :score (cost new-board) :direction direction}))

(eval-option test-board #(count (board-blank-elements %)) :up)
(eval-option test-board #(count (board-blank-elements %)) :down)
(eval-option test-board #(count (board-blank-elements %)) :left)
(eval-option test-board #(count (board-blank-elements %)) :right)

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


(defn power [exp value] (nth (iterate #(* value %) value) (dec exp)))

(defn cost-fn [board] 
  (let [cost-coef [1 1 1 1
                   2 2 2 2
                   3 3 3 3 
                  64 32 16 8]]
    (reduce + 
            (map 
              (fn [x y] (* (power 2 x) y)) 
              (flatten (reverse board)) 
              cost-coef))
  ))

(pos? 0)

(defn neighbors [board x y]
  (filter identity (concat 
    (let [row (board x)]             
      (list
         (when (pos? y) (row (dec y)))
         (when (< y (dec board-size)) (row (inc y)))))
    (let [rotated-board (rotate board)]
      (let [col (rotated-board y)]
           (list 
             (when (pos? x) (col (dec x))) 
             (when (< x (dec board-size)) (col (inc x))))))
    )))
  
(neighbors [[1 2 3 4]
            [5 6 7 8]
            [9 10 11 12]
            [13 14 15 16]] 3 3)


(neighbors [[1 2 3 4]
            [5 6 7 8]
            [9 10 11 12]
            [13 14 15 16]] 0 0)

(neighbors [[1 2 3 4]
            [5 6 7 8]
            [9 10 11 12]
            [13 14 15 16]] 0 2)


(neighbors [[1 2 3 4]
            [5 6 7 8]
            [9 10 11 12]
            [13 14 15 16]] 2 2)



(defn cost-fn [board] 
  (count (board-blank-elements board)))

(cost-fn test-board)

; (defn cost-fn [board] 
;   (let [coef [[1 1 1 1]
;               [2 2 2 2]
;               [3 3 3 3]
;               [64 32 16 8]]]
;   (reduce + (flatten 
;               (list (for [x (range board-size) y (range board-size)]
;                 (let [val (get-element board x y)]
;                  (* 
;                    (* ((coef x) y) (power 2 val))
;                    ; kill the 2
;                    (- val 2)
;                    ))))))))


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


; (defn more-options [board score direction]  
;   (let [options (options-move board)]
;     (hash-map :direction direction
;               :score score
;               :board board
;               :options (into {} (map #(hash-map (:direction %) %) (options-move board))))))

(defn make-options [board] 
  (into {} (map (fn [x] (hash-map (:direction x) x)) (options-move board))))

(make-options test-board)

(defn build [n board]
    (when (pos? n)
      (when-let [options (make-options board)]
        (assoc-in 
          (assoc-in 
            (assoc-in 
              (assoc-in 
                options 
                [:left :options] 
                (build (dec n) (get-in options [:left :board]))) 
                [:right :options] 
                (build (dec n) (get-in options [:right :board]))) 
                [:up :options] 
                (build (dec n) (get-in options [:up :board]))) 
                [:down :options] 
                (build (dec n) (get-in options [:down :board]))) 
        )))

(build 3 [[64 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]])

(defn minimax-aux [tree]
  (if-let [options (:options tree)]
    (let [vals [(minimax-aux (:left options)) (minimax-aux (:right options)) (minimax-aux (:up options)) (minimax-aux (:down options))]]      
      
      (hash-map :min (apply min (map #(:min %) vals))
                :max (apply max (map #(:max %) vals))))
    (hash-map :min (:score tree) :max (:score tree))))

(defn minimax [tree] 
  (hash-map 
    :left (minimax-aux (:left tree))
    :right (minimax-aux (:right tree))
    :up (minimax-aux (:up tree))
    :down (minimax-aux (:down tree))))

(minimax (build 4 [[64 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]]))


(into [] (hash-map :test "balh" :test2 "balh"))

(defn fmap 
  [f m]
  (into () (for [[k v] m] (f k v))))

(fmap (fn [k v] {v k}) {1 2 3 4}  )

;
; TODO optimize to put in bucket and the select among the moves in the bucket
(defn optimal-minimax-move [board]
    (let [scoring (minimax (build 5 board))] 
      (let [t (fmap (fn [k v] (hash-map :min (:min v) :max (:max v) :direction k)) scoring)]
        (:direction (last (sort-by #(+ (* 16 (:min %)) (:max %)) t)))
)))

(optimal-minimax-move [[64 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]])

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
        (play-a-turn board move)))

(nth (iterate play-optimally test-board) 1000)


(print-board [[64 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]])
(optimize-move [[64 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]])
(options-move [[64 8 32 16] [2 4 16 2] [2 4 8 16] [2 4 8 16]])

