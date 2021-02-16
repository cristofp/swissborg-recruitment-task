import $file.Types, Types._
import upickle.default.read


def fetchCurrenciesRatesMap(staticData: Boolean = false): CurrencyExchangeRates = {
  val data = if (staticData) {
    staticExchangeRatesData
  } else {
    val r = requests.get("https://fx.priceonomics.com/v1/rates/")

    if (r.statusCode != 200) {
      println("Failure when obtaining currency exhange rates data")
      exit(1)
    }
    r.text

  }

  read[Map[String, String]](data) //sample element: ("USD_JPY", "109.0681005")
    .toList
    .groupBy(_._1.split("_")(0))
    .map {
      case (fromCurr, elements) => // like (USD, List(("USD_JPY", "109.0681005"), ...)
        val toCurrRateMap = elements.map {
          t => t._1.split("_")(1) -> BigDecimal(t._2)
        }.toMap

        (fromCurr, toCurrRateMap)
    }
}

val staticExchangeRatesData =
  """{
    |  "USD_JPY": "102.4590",
    |  "USD_USD": "1.0000000",
    |  "JPY_EUR": "0.0075",
    |  "BTC_USD": "115.65",
    |  "JPY_BTC": "0.0000811",
    |  "USD_EUR": "0.7779",
    |  "EUR_USD": "1.2851",
    |  "EUR_JPY": "131.7110",
    |  "JPY_USD": "0.0098",
    |  "BTC_BTC": "1.0000000",
    |  "EUR_BTC": "0.01125",
    |  "BTC_JPY": "12325.44",
    |  "JPY_JPY": "1.0000000",
    |  "BTC_EUR": "88.8499",
    |  "EUR_EUR": "1.0000000",
    |  "USD_BTC": "0.0083"
    |}""".stripMargin

def nSpaces(n: Int): String = {
  new String(Array.fill(n)(' '))
}

def printCurrencyExchangeRates(rates: CurrencyExchangeRates): Unit = {
  val currencies = rates.keys
  print("|     |")
  currencies.foreach(curr => print(s"        ${curr}        |"))
  println()
  currencies.foreach { rowCurr =>
    print(s"| ${rowCurr} |")
    currencies.foreach { colCurr =>
      val rate = rates(rowCurr)(colCurr).toString()
      print(s" $rate${nSpaces(17 - rate.length)} |")
    }
    println()
  }
}