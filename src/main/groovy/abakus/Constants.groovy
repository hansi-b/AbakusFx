package abakus

import org.javamoney.moneta.Money

import javax.money.CurrencyUnit
import javax.money.Monetary
import java.text.DecimalFormat

class Constants {
    private static final CurrencyUnit euros = Monetary.getCurrency(Locale.GERMANY)

    private static final DecimalFormat df = DecimalFormat.getNumberInstance(Locale.GERMANY)
    static {
        df.setParseBigDecimal(true)
    }

    static BigDecimal toBigDec(String floatStr) {
        df.parse(floatStr)
    }

    static Money toEuro(String floatStr) {
        Money.of(toBigDec(floatStr), euros)
    }
}