package abakus

import org.javamoney.moneta.Money

import javax.money.CurrencyUnit
import javax.money.Monetary
import java.text.DecimalFormat
import java.text.NumberFormat

class Constants {

    static final Locale locale = Locale.GERMANY
    private static final CurrencyUnit eur = Monetary.getCurrency(locale)

    private static final DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale)
    static {
        df.setParseBigDecimal(true)
    }

    static BigDecimal toBigDec(String floatStr) {
        df.parse(floatStr)
    }

    static Money toEuro(String floatStr) {
        Money.of(toBigDec(floatStr), eur)
    }

    static Money euros(Number amount) {
        Money.of(amount, eur)
    }

    static BigDecimal percent(BigDecimal percent) {
        percent / BigDecimal.valueOf(100)
    }

    static BigDecimal percent(int percent) {
        BigDecimal.valueOf(percent) / BigDecimal.valueOf(100)
    }
}