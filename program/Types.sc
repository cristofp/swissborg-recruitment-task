
type CurrencyExchangeRates = Map[String, Map[String, BigDecimal]]

case class Arbitrage(arbitragePath: List[String], arbitrageRate: BigDecimal) {
  override def toString(): String =
    s"${arbitragePath.mkString("_")}, rate: ${arbitrageRate}"
}

case class CurrencyPathElem(previousCurrInPath: String, unIncludedCurrenciesInPath: Set[String], totalPathRate: BigDecimal)
