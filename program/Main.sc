import $file.Types, Types._
import $file.Helpers, Helpers._
import $file.FindArbitrageAlgo, FindArbitrageAlgo._

@main
def main(useStaticDataset: Boolean = false): Unit ={
  val currenciesRates: CurrencyExchangeRates = fetchCurrenciesRatesMap(useStaticDataset)
  printCurrencyExchangeRates(currenciesRates)

  println()

  val arbitrage = findBestArbitrageForCurrenciesRates(currenciesRates)

  arbitrage match {
    case Some(arbitrage) => println(s"Best arbitrage: $arbitrage")
    case None => println("There is no arbitrage for given exchange rates")
  }
}