(ns sudoku.app
  (:require [clojure.pprint    :refer [pprint]]
            [clojure.tools.cli :refer [cli]]
            [clojure.java.io   :refer [writer]]
            [sudoku.solver :refer [solve]] 
            [sudoku.generator :refer [puzzle-of random-puzzle]])
  (:gen-class :main true))
  
  
(def HELP_MESSAGE  "\nA convenient Sudoku solver based on Clojure core.logic (respect to David Nolen). 
Example usage: 
java -jar SudokuSolver.jar -p \"[8 0 6 5 0 0 0 0 0 
                                0 0 4 0 0 0 0 0 8
                                0 0 0 0 0 0 6 0 0
                                0 0 0 0 0 0 0 0 0
                                3 7 0 4 5 0 0 0 0
                                5 0 1 0 9 8 0 0 7
                                0 0 0 0 0 7 0 2 0
                                2 5 7 1 6 0 0 0 9
                                0 8 0 0 3 0 0 4 0]\"           
               
java -jar SudokuSolver -f \"puzzles.txt\"
  
Optional switches include: ")         

(defn -main  "Main entry for the solver/generator."
  [& args]
 (let [[opts argus banner] 
        (cli args
      ["-h" "--help" "Show help/instructions." :flag true :default false]
      ["-g" "--generate" "Generate a puzzle of the specified difficulty. Options include [:very-easy, :easy, :medium, :hard, :very-hard]"]
      ["-n" "--number" "Specify how many solutions you'd like to get back." :default :all]
      ["-p" "--puzzle" "The sudoku puzzle you wish to solve as a sequence of 81 digits representing empty slots with 0."]
      ["-f" "--file"   "The path to a file containing an arbitrary number of sudoku puzzles you wish to solve (e.g. \"/home/user/puzzles.txt\" ). These must be a 2d sequence."])
      time-it? (:time opts)
      file     (:file opts)
      puzzle   (:puzzle opts)
      puzzle  (cond-> puzzle 
                 ((complement nil?) puzzle) read-string)
      n       (:number opts) 
      n      (cond-> n 
                 ((complement keyword?) n) Integer/parseInt)
      G       (:generate opts)]
   (assert (or (and file (not puzzle)) 
               (and puzzle (not file))
               (and (not puzzle) (not file))) "INCOMPATIBLE SWITCHES DETECTED!\nCan either accept a puzzle OR a file of puzzles. NOT both...")
   (assert (or (and G (not puzzle)) 
               (and puzzle (not G))
               (and (not puzzle) (not G))) "INCOMPATIBLE SWITCHES DETECTED!\nCan either solve a puzzle OR generate one. NOT both...")                 
  (when (:help opts)
      (println HELP_MESSAGE "\n\n" banner)
      (System/exit 0))
  (when puzzle    
   (time  
    (condp = n
      :all (->> puzzle solve (map #(partition 9 %)) doall pprint) 
           (->> puzzle solve (take n) (map #(partition 9 %)) doall pprint)))
     (System/exit 0))
  (when file
    (let [puzzles (-> file slurp read-string)
          solutions (mapv (comp #(partition 9 %) first solve) puzzles)]
      (with-open [^java.io.Writer wr (writer "sudoku-solutions.txt")]
        (binding [*out* wr]
          (pprint solutions)))
      (println "\n File 'sudoku-solutions.txt' was written successfully...\n")        
      (System/exit 0)))
   (when G
   (let [N (if (number? n) n 1)
         sols (puzzle-of N (read-string G))
         sols (if (> 2 N) (partition 9 sols) 
                (doall (map #(partition 9 %) sols)))] 
      (pprint sols)   
      (System/exit 0)))  )) 
;=============================================================

;;sample puzzles:

(def hardest 
"According to http://www.telegraph.co.uk/science/science-news/9359579/Worlds-hardest-sudoku-can-you-crack-it.html"
   [8 0 0 0 0 0 0 0 0
    0 0 3 6 0 0 0 0 0
    0 7 0 0 9 0 2 0 0
    0 5 0 0 0 7 0 0 0
    0 0 0 0 4 5 7 0 0
    0 0 0 1 0 0 0 3 0
    0 0 1 0 0 0 0 6 8
    0 0 8 5 0 0 0 1 0
    0 9 0 0 0 0 4 0 0]) ;; roughly 6 sec for 1 solution!!!   
    
;; (-> hardest solve doall time) 


(def puzzle1 ;;MAN-UP facebook challenge for 5 pounds!
   [8 0 6 5 0 0 0 0 0
    0 0 4 0 0 0 0 0 8
    0 0 0 0 0 0 6 0 0
    0 0 0 0 0 0 0 0 0
    3 7 0 4 5 0 0 0 0
    5 0 1 0 9 8 0 0 7
    0 0 0 0 0 7 0 2 0
    2 5 7 1 6 0 0 0 9
    0 8 0 0 3 0 0 4 0]) ;; 69 possible solutions

