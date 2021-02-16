# Bitcoin Arbitrage

## Summary
This repository contains solution to Bitcoin Arbitrage task described [here](https://priceonomics.com/jobs/puzzle/).

Developed program finds the best currency arbitrage for provided currencies rates. 
## How to run
Program is coded as [Ammonite Scala Scripts](http://ammonite.io/#ScalaScripts).
To run the program you need to have [Ammonite REPL](http://ammonite.io/#Ammonite-REPL) installed and so having `amm` command available in terminal.

To run program obtaining currency rates dynamically from https://fx.priceonomics.com/v1/rates/ : 
```
amm program/Main.sc
```
To run program on static data taken from [task description](https://priceonomics.com/jobs/puzzle/):
```$xslt
amm program/Main.sc true
```
# Explanation of algorithm

![Sample exchange Rates](https://raw.githubusercontent.com/cristofp/swissborg-recruitment-task/main/assets/rates.png)

Findings about Arbitrage Seeking problem:
  - Each currency exchange chain can be described as a chain of multiplications of the particular currency exchange rates and so 
  the result of multiplication is the total rate between first and last currency in the chain.
  E.g. for exchange chain: `BTC -> USD -> JPY -> EUR` we have `115.65 * 102.4590 * 0.0075` that gives for this chain 
  total exchange rate `BTC -> ... -> JPY  =  88.870375` which turns out to be slightly more profitable then exchanging BTC to EUR directly (according to the table above)
  - Arbitrage is a currency exchange chain that starts and ends with the same currency and the total rate is greater then 1.
  E.g. `USD -> EUR -> BTC -> USD` gives total rate: `0.7779 * 0.01125 * 115.65 = 1.0120965`. By exchanging my money via this chain
  I'm getting back `1.0120965` times more after one full run.
  - Arbitrage chain is *de facto* a loop and it doesn't matter from which currency I start exchanging - the total rate after going one whole loop is the same.
  So e.g. `USD -> EUR -> BTC -> USD` is equivalent to `EUR -> BTC -> USD -> EUR`
  - One arbitrage loop is better then the other if its the total rate is higher the of the other.
  
Invented algorithm
 - Algorithm applied in the program is a creative modification of Dijkstra Algorithm. The main foundation is a rule:
 if optimal currency exchange chain from A to Z goes via X (`A -> ... -> X -> ... -> Z`) then it's part `A -> ... -> X` is also optimal.
 - Having this as basis, the algorithm searches for the best currency chains (paths) from given starting currency to all other currencies.
 The search space is being narrowed down in each iteration, because when many paths lead to the same currency, only the best one (having the highest total rate) is left for further exploration.
 What's more only currencies whose their optimal paths were updated in previous iteration are taken into account for next iteration for searching for 
 further best paths to other nodes having them as predecessor.
 - Paths are not allowed to have cycles (having such cycle in optimal path would mean we have inner arbitrage), the only cycle allowed from the starting currency back to itself.
 That mean, that having *N* currencies the longest possible path may have maximum *N* connections 
 - Because there needs to be a starting currency assigned, potentially found arbitrage would be limited to include this currency in its loop. 
 It may happened that there's other better arbitrage in given graph that doesn't include this starting currency. To look for such arbitrages, 
 the algo is repeated on set of currencies excluding the start currency from previous iterations. 

 # Complexity of algorithm
 The pessimistic complexity of algorithm is *O(n^4)*. For given currency set and starting currency there are three nested loops, and on top of that we repeat the algo on sets excluding previous starting currencies till the set consisting of two currencies.
 
 However the practical complexity is much less since in two of the algorithm loops, the number of iterations decreases by one in subsequent goes.
 
 # Coding style
 Functional Programming principles were generally obeyed with pragmatic exceptions. In algorithm core code section mutable structures were used
 since in this way code is more readable and algorithm easier to understand.
    

[here]: https://priceonomics.com/jobs/puzzle/