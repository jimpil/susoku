(ns sudoku.generator
  (:require [sudoku.solver :refer [solve]]))

(defonce digits (set (range 1 10)))
(defonce rows [:a :b :c :d :e :f :g :h :i])
(defonce cols (range 1 10))
(defonce squares (for [r rows c cols] [r c]))
(defonce unitlist (concat (for [c cols] (for [r rows] [r c]))
                          (for [r rows] (for [c cols] [r c]))
                          (for [rs (partition 3 rows) cs (partition 3 cols)]
                          (for [r rs c cs] [r c]))))
(def units (into {} (for [s squares]
                      [s (for [u unitlist :when (some #{s} u)] u)])))
(def peers (into {} (for [s squares]
                      [s (-> (reduce into #{} (units s)) (disj s))])))
 
(declare assign eliminate)

(defn- reduce-true  "Like reduce but short-circuits upon logical false"
  [f val coll]
  (when val
    (loop [val val, coll coll]
      (if (empty? coll)
        val
        (when-let [val* (f val (first coll))]
          (recur val* (rest coll)))))))


(defn- assign
  "Whittle down the square at s to digit d by eliminating every digit
  except d from the square, and doing constraint propogation. Returns
  false if a contradiction results"
  [values s d]
  (reduce-true #(eliminate %1 s %2)
               values
               (disj (values s) d)))
 
(defn- eliminate
  "Eliminate digit d from square s and do any appropriate constraint propogation."
  [values s d]
  (if-not ((values s) d)
    values ;already eliminated
    (when-not (= #{d} (values s)) ;can't remove last value
      (let [values (update-in values [s] disj d)
            values (if (= 1 (count (values s)))
                     ;; Only one digit left, eliminate it from peers
                     (reduce-true #(eliminate %1 %2 (first (%1 s)))
                                  values
                                  (peers s))
                     values)]
        (reduce-true
         (fn [values u]
           (let [dplaces (for [s u :when ((values s) d)] s)]
             (when-not (zero? (count dplaces)) ;must be a place for this value
               (if (= 1 (count dplaces))
                 ;; Only one spot remaining for d in a unit -- assign it
                 (assign values (first dplaces) d)
                 values))))
         values
         (units s))))))


(defn random-puzzle
  "Make a random puzzle with N or more clues."
  ([] (random-puzzle 17))
  ([n]
     (let [done? (fn [values]
                   (let [ds (apply concat (filter #(= 1 (count %)) (vals values)))]
                     (and (<= n (count ds)) (<= 8 (count (distinct ds))))))
           steps (reductions #(assign %1 %2 (-> %2 %1 seq rand-nth))
                             (into {} (for [s squares] [s digits]))
                             (shuffle squares))
           values (first (filter #(or (not %) (done? %)) steps))]
       (if (nil? values)
         (recur n) ;contradiction - retry
          (for [ds (map values squares)]
                 (if (next ds) 0 (first ds)))))))
                                
                                
(defmacro time*
  "Evaluates expr and returns  time-in-seconds"
  [expr]
  `(let [start# (System/nanoTime)
         ret# ~expr]
    (/ (double (- (System/nanoTime) start#)) 1000000000.0)))                                
                                
(defn difficulty [puzzle]
  (let [T (-> puzzle solve first time*)]
    (condp <= T 
         0.8 :very-hard
         0.5 :hard
         0.2 :medium
         0.05 :easy
              :very-easy))) 
             
             
(defn puzzle-of "Attempts to find a puzzle of the specified difficulty." 
 ([n diffi]
   (let [f (fn []
             (some #(when (= diffi (difficulty %)) %) 
              (repeatedly random-puzzle)))]
   (if (or (nil? n) 
           (= 1 n)) (f) 
    (repeatedly n f))))
 ([diffi] 
   (puzzle-of 1 diffi)))    

             
                                            
