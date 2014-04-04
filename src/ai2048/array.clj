(ns ai2048.array)

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

;; Be able to pick on element of the board

(defn get-element [board x y] ((board x) y))

(get-element [[ 1 2 3 1 ] 
              [ 3 3 1 1 ]
              [ 0 1 1 1 ]
              [ 3 2 1 1 ]] 2 0)

(get-element [[2 4] [8 16]] 0 0)

;; Label value with coordinates

(defn distribute-x [x-rank elements] (map (fn [c] { :x x-rank :y (:y c) :val (:val c)}) elements))

(distribute 2 [{:val 1 :y "y"}  {:val 2 :y "y2"}])

(defn rank-x [arg] (map (fn [a b] {:val a :x b}) arg (range))) 
(defn rank-y [arg] (map (fn [a b] {:val a :y b}) arg (range))) 

(defn rank-xy [board] (flatten (map #(distribute-x (:x %) (:val %)) (rank-x (map rank-y (lazy-seq board))))))

(rank-xy [[0 10 20] [1 11 21] [2 12 22]])

;; Select element that are blank

(defn blank-elements [ranked-elements]  (map #(dissoc % :val) (filter #(=(:val %) 0) ranked-elements)))

(blank-elements (rank-xy [[0 10 20] [1 11 21] [2 12 22]]))

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

(defn rand-board-blank-element [board] 
  (let [b-elements (board-blank-elements board)]
    (if (empty? b-elements)
    ;; then
    (throw (Exception. "game over!"))
    ;; else
    (rand-nth b-elements))))

(rand-board-blank-element [[ 1 2 3 1 ] 
                           [ 3 3 1 1 ]
                           [ 0 1 1 1 ]
                           [ 3 2 1 1 ]]) 


(rand-board-blank-element [[ 1 2 3 1 ] 
                           [ 3 3 1 1 ]
                           [ 1 1 1 1 ]
                           [ 3 2 1 1 ]]) 

;; Be able to replace an element of the board

(defn set-element [board x y val] ...)

;; TODO


;; TODO


(defn move-pieces [direction board] ...)

 