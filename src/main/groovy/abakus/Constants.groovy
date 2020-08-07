package abakus

import org.javamoney.moneta.Money

import javax.money.CurrencyUnit
import javax.money.Monetary
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth

class Constants {

    static final Locale locale = Locale.GERMANY
    private static final CurrencyUnit euros = Monetary.getCurrency(locale)

    static Money euros(Number amount) {
        Money.of(amount, euros)
    }

    private static final DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale)
    static {
        df.setParseBigDecimal(true)
    }

    static BigDecimal toBigDec(String floatStr) {
        df.parse(floatStr)
    }

    static Money toEuro(String floatStr) {
        Money.of(toBigDec(floatStr), euros)
    }

    static BigDecimal percent(BigDecimal percent) {
        percent / BigDecimal.valueOf(100)
    }

    static BigDecimal percent(int percent) {
        BigDecimal.valueOf(percent) / BigDecimal.valueOf(100)
    }

    /**
     * @return a LocalDate at the start of the given month in the given year
     */
    static LocalDate startOfMonth(int year, int month) {
        LocalDate.of(year, month, 1)
    }

    /**
     * @return a LocalDate at the end of the given month in the given year
     */
    static LocalDate endOfMonth(int year, int month) {
        YearMonth.of(year, month).atEndOfMonth()
    }
}