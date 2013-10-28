# sudoku-solver

A sudoku solver and generator in Clojure. 

## Usage

Download the jar from [here](https://dl.dropboxusercontent.com/u/45723414/SudokuSolver.jar)

The way the puzzle generation works is peculiar to say the least. I couldn't think of a good way to judge a puzzle so I'm just feeding random puzzles of 17 clues to the generator and measure how long it takes to solve them. Based on my estimates on a 800MHz-1GHz CPU it takes up to 
> 0.05 sec to solve a :very-easy puzzle
 
>0.2 sec for an :easy 

>0.5 for a :medium 

>0.8 for a :hard

> 0.86 for a :very-hard

The hardest sudoku ever devised takes consistently 0.83 - 0.86 sec on a couple of machines that I tried  

## License

Copyright © 2013 Dimitrios Piliouras

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
