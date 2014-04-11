(ns ai2048.board
  (:require [ai2048.utils :as utils]
   :use [clojure.core.match :only (match)]))
; (use '[clojure.core.match :only (match)])

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

; (new-board)

;; Be able to display a board
(defn print-board [board] 
  (doall (map (fn [x] (println x) ) (seq board))))

; (print-board [[1 2] [3 4]])

; (print-board (new-board))

;; Be able to pick on element of the board

(defn get-element [board x y] ((board x) y))

; (get-element [[ 1 2 3 1 ] 
;               [ 3 3 1 1 ]
;               [ 0 1 1 1 ]
;               [ 3 2 1 1 ]] 2 0)

; (get-element [[2 4] [8 16]] 0 0)

;; Label value with coordinates

(defn distribute-x [x-rank elements] (map (fn [c] { :x x-rank :y (:y c) :val (:val c)}) elements))

; (distribute-x 2 [{:val 1 :y "y"}  {:val 2 :y "y2"}])

(defn rank-x [arg] (map (fn [a b] {:val a :x b}) arg (range))) 
(defn rank-y [arg] (map (fn [a b] {:val a :y b}) arg (range))) 

(defn rank-xy [board] (flatten (map #(distribute-x (:x %) (:val %)) (rank-x (map rank-y (lazy-seq board))))))

; (rank-xy [[0 10 20] [1 11 21] [2 12 22]])

;; Select element that are blank

(defn blank-elements [ranked-elements]  (map #(dissoc % :val) (filter #(=(:val %) 0) ranked-elements)))

; (blank-elements (rank-xy [[0 10 20] [1 11 21] [2 12 22]]))

; (blank-elements (rank-xy [[0 10 20] [1 0 21] [2 0 22]]))

;; Pick a random blank element

(defn board-blank-elements [board] (blank-elements (rank-xy board)))

; (board-blank-elements     [[ 1 2 3 1 ] 
;                            [ 3 3 1 1 ]
;                            [ 1 1 1 1 ]
;                            [ 3 2 1 1 ]]) 

; (board-blank-elements     [[ 1 2 3 1 ] 
;                            [ 3 3 1 1 ]
;                            [ 0 1 1 1 ]
;                            [ 3 2 1 1 ]]) 

; (board-blank-elements     [[ 1 2 3 1 ] 
;                            [ 3 3 1 1 ]
;                            [ 0 0 0 1 ]
;                            [ 3 2 1 1 ]]) 


(defn rand-board-blank-element [board] 
  (let [b-elements (board-blank-elements board)]
    (if (empty? b-elements)
    ;; then
    (throw (Exception. (str "game over! max=" (apply max (flatten board)) ",board=" board)))
    ;; else
    (rand-nth b-elements))))

; (rand-board-blank-element [[ 1 2 3 1 ] 
;                            [ 3 3 1 1 ]
;                            [ 0 1 1 1 ]
;                            [ 3 2 1 1 ]]) 


;; Be able to apply a function on a particular row 

(defn apply-at [board idx f] 
  (vec (concat (subvec board 0 idx) (list (f (board idx))) (subvec board (+ idx 1)))))

; (apply-at [1 2] 1 (fn [x] 0))

; (apply-at [[1 2] [3 4]] 1 (fn [x] x))

;; Be able to apply a function on a particular element of the board

(defn apply-at-xy [board x y f] 
  (apply-at board x (fn [row] (apply-at row y f))))


; (apply-at-xy [[1 2 3 4]
;               [2 3 4 5]
;               [3 4 5 6]
;               [4 5 6 7]] 2 2 (fn [x] 0))

;; Be able to replace an element of the board

(defn set-element [board x y val] (apply-at-xy board x y (fn [e] val)))

; (set-element [[1 2 3 4]
;               [2 3 4 5]
;               [3 4 5 6]
;               [4 5 6 7]] 2 2 0)

; (set-element [[1 2 3 4]
;               [2 3 4 5]
;               [3 4 5 6]
;               [4 5 6 7]] 0 0 0)

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
    [ w x y z ] (if (= y z) 
                  (if (= w x)                     
                    (vec [0 0 (+ w x) (+ y z)]) ; y = z && w = x
                    (vec [0 w x (+ y z)])) ; y = z && w != x
                  (if (= x y) 
                    (vec [0 w (+ x y) z]) ; y != z && x = y
                    (if (= w x) 
                      (vec [0 (+ w x) y z ]) ; y != z && w = x
                      (vec [w x y z])))) ; y = z && w != x
    :else (throw (Exception. "what's that?"))))

; (evolve-row [ 1 1 0 1 ])
; (evolve-row [ 1024 1024 1 1 ])
; (evolve-row [ 1 1 2 0 ])

;; Be able to rotate the board so all move appears to be left to right
(defn rotate [board] (vec (apply map vector board)))

; (rotate (rotate [[1 1 1]
;         [2 2 2]
;         [3 3 3]]))

(defn reverse-board [board] (vec (map #(-> % reverse vec) board)))

; (reverse-board [[1 2 3]
;                 [4 5 6]
;                 [7 8 9]])

(defn to-right [board] board)
(defn to-left [board] (reverse-board board))
(defn to-down [board] (rotate board))
(defn to-up [board] (-> board rotate vec reverse-board))

; (to-up [[1 2 3]
;         [4 5 6]
;         [7 8 9]])

; (to-down [[1 2 3]
;           [4 5 6]
;           [7 8 9]])

; (to-left [[1 2 3]
;           [4 5 6]
;           [7 8 9]])

; (to-right [[1 2 3]
;            [4 5 6]
;            [7 8 9]])


(defn from-right [board] board)
(defn from-left [board] (reverse-board board))
(defn from-up [board] (rotate (reverse-board board)))
(defn from-down [board] (rotate board))

; (from-left (to-left [[1 2 3]
;                      [4 5 6]
;                      [7 8 9]]))

; (from-right (to-right [[1 2 3]
;                        [4 5 6]
;                        [7 8 9]]))

; (from-up (to-up [[1 2 3]
;                  [4 5 6]
;                  [7 8 9]]))

; (from-down (to-down [[1 2 3]
;                      [4 5 6]
;                      [7 8 9]]))

;; Be able to apply the evolution rules to each row
(defn evolve-board [board] (map evolve-row board))

; (evolve-board [[1 1 2 0]
;                [1 1 2 0]
;                [1 1 2 0]
;                [1 1 2 0]])


;; Be able to play a turn in every directions
(defn play-right [board] (-> board to-right evolve-board from-right vec))
(defn play-left [board] (-> board to-left evolve-board from-left vec))
(defn play-up [board] (-> board to-up evolve-board from-up vec))
(defn play-down [board] (-> board to-down evolve-board from-down vec))

; (play-right [[1 1 2 0]
;              [1 1 2 0]
;              [1 1 2 0]
;              [1 1 2 0]])

; (play-up [[1 1 2 0]
;           [1 1 2 0]
;           [1 1 2 0]
;           [1 1 2 0]])


; (play-left [[1 1 2 0]
;             [1 1 2 0]
;             [1 1 2 0]
;             [1 1 2 0]])

; (play-down [[1 1 2 0]
;             [1 1 2 0]
;             [1 1 2 0]
;             [1 1 2 0]])

;; Be able to play

(defn move-pieces [board direction] 
  (case direction
    :left (play-left board)
    :right (play-right board)
    :up (play-up board)
    :down (play-down board)))


; (move-pieces [[1 1 2 0]
;               [1 1 2 0]
;               [1 1 2 0]
;               [1 1 2 0]] :left)

; (move-pieces [[1 1 2 0]
;               [1 1 2 0]
;               [1 1 2 0]
;               [1 1 2 0]] :right) 

; (move-pieces [[1 1 2 0]
;               [1 1 2 0]
;               [1 1 2 0]
;               [1 1 2 0]] :up) 

; (move-pieces [[1 1 2 0]
;               [1 1 2 0]
;               [1 1 2 0]
;               [1 1 2 0]] :down) 

;; Play a sequence of moves on a board
(defn play-sequence [board moves]
  (reduce move-pieces board moves))

; (-> (play-sequence [[1 1 2 0]
;                     [1 1 2 0]
;                     [1 1 2 0]
;                     [1 1 2 0]] [:left :left :up :up]) print-board)

;; Be able to generate a new random element in a blank cell

(defn generate-new-rand-element [board] 
  (let [blank (rand-board-blank-element board)] 
    (set-element board (:x blank) (:y blank) (if (= (rand-int 10) 0) 4 2))))

; (generate-new-rand-element (new-board))

; (-> (new-board) generate-new-rand-element print-board)

; (print-board (generate-new-rand-element (new-board)))

; (let [board (new-board)]
;   (nth (iterate generate-new-rand-element board) 5 ))

;; Be able to collect neighbors

(defn neighbors [board x y]  
  (when (and (< x board-size) (<= 0 x) (< y board-size) (<= 0 y))
  (filter identity (concat 
    (let [row (board x)]             
      (list
         (when (pos? y) (row (dec y)))
         (when (< y (dec board-size)) (row (inc y)))))
    (let [rotated-board (rotate board)]
      (let [col (rotated-board y)]
           (list 
             (when (pos? x) (col (dec x))) 
             (when (< x (dec board-size)) (col (inc x))))))))))
  
; (neighbors [[1 2 3 4]
;             [5 6 7 8]
;             [9 10 11 12]
;             [13 14 15 16]] 3 3)


; (neighbors [[1 2 3 4]
;             [5 6 7 8]
;             [9 10 11 12]
;             [13 14 15 16]] 0 0)

; (neighbors [[1 2 3 4]
;             [5 6 7 8]
;             [9 10 11 12]
;             [13 14 15 16]] 0 2)


; (neighbors [[1 2 3 4]
;             [5 6 7 8]
;             [9 10 11 12]
;             [13 14 15 16]] 2 2)

;; Define a test-board

(def test-board [[0 2 0 0]
                 [0 2 0 0]
                 [0 0 0 0]
                 [0 0 0 0]])

; (move-pieces test-board :up)