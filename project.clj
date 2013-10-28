(defproject sudoku "0.2.0"
  :description "Rudimentary Sudoku solver & generator"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.4"] 
                 [org.clojure/core.logic "0.8.4" :exclusions [[org.clojure/clojure]]]]
  :uberjar-name "SudokuSolver.jar"
  :aot :all
  :main sudoku.app
  )
