package abakus

import org.javamoney.moneta.Money

import javax.money.CurrencyUnit
import javax.money.Monetary
import java.text.DecimalFormat

class Constants {

    static final Locale locale = Locale.GERMANY
    static final CurrencyUnit euros = Monetary.getCurrency(locale)

    private static final DecimalFormat df = DecimalFormat.getNumberInstance(locale)
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