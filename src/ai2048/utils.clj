(ns ai2048.utils)

(defn power [exp value] (if (= exp 0) 1 (nth (iterate #(* value %) value) (dec exp))))

; (power 2 34)

; (power 0 4)

(defn abs "(abs n) is the absolute value of n" [n]
  (cond
   (not (number? n)) (throw (IllegalArgumentException.
                             "abs requires a number"))
   (neg? n) (- n)
   :else n))

; (abs -4)

(defn manhattan-distance [x y]
  (+ (abs (- (:x x) (:x y))) (abs (- (:y x) (:y y)))))

; (manhattan-distance {:x 1 :y 0} {:x 0 :y 1} )
  
(defn mean [sq]
  (if (empty? sq)
      0
      (/ (reduce + sq) (count sq))))

; (mean [1 2 3])

(defn fmap 
  [f m]
  (into () (for [[k v] m] (f k v))))

; (fmap (fn [k v] {v k}) {1 2 3 4})
   
