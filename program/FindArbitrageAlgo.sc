import $file.Types, Types._

import scala.collection.mutable

private def resolveTableToArbitrage(bestPathsTable: Map[String, CurrencyPathElem], startingCurr: String): Option[Arbitrage] = {
  @scala.annotation.tailrec
  def resolveArbitragePath(pathTail: List[String]): List[String] = {
    if (bestPathsTable(pathTail.head).previousCurrInPath == startingCurr)
      pathTail
    else
      resolveArbitragePath(bestPathsTable(pathTail.head).previousCurrInPath :: pathTail)
  }

  if (bestPathsTable(startingCurr).previousCurrInPath == startingCurr)
    None
  else
    Some(Arbitrage(resolveArbitragePath(List(startingCurr)), bestPathsTable(startingCurr).totalPathRate))
}


@scala.annotation.tailrec
private def findBestArbitrageForCurrencies(currencies: Set[String], currenciesRates: CurrencyExchangeRates, bestArbSoFar: Option[Arbitrage]): Option[Arbitrage] = {
  val startingCurr = currencies.head
  //'bestPathTable' stores information about currently best paths (aka sequences) of currencies exchanges starting from the 'startingCurr' till the particular currency with their shortcut rate between 'startingCurr' and given currency
  val bestPathsTable = mutable.Map() ++ currencies.map(curr => curr -> CurrencyPathElem(startingCurr, currencies - curr, currenciesRates(startingCurr)(curr))).toMap

  var currenciesWithUpdatedPath = mutable.Set() ++ currencies

  while (currenciesWithUpdatedPath.nonEmpty) {
    val newCurrenciesWithUpdatedPath = mutable.Set[String]()
    currenciesWithUpdatedPath.foreach { fromCurrency =>
      bestPathsTable(fromCurrency).unIncludedCurrenciesInPath.foreach { toCurrency =>
        val fromCurrencyTotalRate = bestPathsTable(fromCurrency).totalPathRate
        val toCurrencyTotalRateSoFar = bestPathsTable(toCurrency).totalPathRate
        val FromCurrencyToCurrencyRate = fromCurrencyTotalRate * currenciesRates(fromCurrency)(toCurrency)
        if (FromCurrencyToCurrencyRate > toCurrencyTotalRateSoFar) {
          bestPathsTable(toCurrency) = CurrencyPathElem(fromCurrency, bestPathsTable(fromCurrency).unIncludedCurrenciesInPath - toCurrency, FromCurrencyToCurrencyRate)
          newCurrenciesWithUpdatedPath.add(toCurrency)
        }
      }
    }
    currenciesWithUpdatedPath = newCurrenciesWithUpdatedPath
  }

  val newBestArbSoFar = List(resolveTableToArbitrage(bestPathsTable.toMap, startingCurr), bestArbSoFar).flatten.maxByOption(_.arbitrageRate)

  if (currencies.size == 2) {
    newBestArbSoFar
  } else {
    findBestArbitrageForCurrencies(currencies.tail, currenciesRates, newBestArbSoFar)
  }
}

def findBestArbitrageForCurrenciesRates(currenciesRates: CurrencyExchangeRates): Option[Arbitrage] = {
  val currencies = currenciesRates.keySet
  findBestArbitrageForCurrencies(currencies, currenciesRates, bestArbSoFar = None)
}


