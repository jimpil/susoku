(ns sudoku.solver
  (:refer-clojure :exclude [==])
  (:require [clojure.core.logic :refer :all] 
            [clojure.core.logic.fd :as fd])
  (:gen-class :main true))
  
;;Credits go entirely to David Nolen for writing such a succinct solver and actually for many other things as well :)  

(defn init-board [vars puzzle]
  (matche [vars puzzle]
          ([[] []]
             succeed)
          ([[_ . more-vars] [0 . more-puzzle]]
             (init-board more-vars more-puzzle))
          ([[num . more-vars] [num . more-puzzle]]
             (init-board more-vars more-puzzle))))


(defn solve [puzzle]
  (let [sdnum (fd/domain 1 2 3 4 5 6 7 8 9)
        board (repeatedly 81 lvar)
        rows (into [] (map vec (partition 9 board)))
        cols (apply map vector rows)

        get-square (fn [x y]
                     (for [x (range x (+ x 3))
                           y (range y (+ y 3))]
                       (get-in rows [x y])))

        squares (for [x (range 0 9 3)
                      y (range 0 9 3)]
                  (get-square x y))]

    (run* [q] ;;return all solutions
         (== q board)
         (everyg #(fd/in % sdnum) board)
         (init-board board puzzle)
         (everyg fd/distinct rows)
         (everyg fd/distinct cols)
         (everyg fd/distinct squares))))
  
