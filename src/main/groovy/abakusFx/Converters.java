package abakusFx;

import abakus.Constants;
import abakus.Stufe;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;

import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.time.YearMonth;

public class Converters {

    public static StufeConverter createStufeConverter() {
        return new StufeConverter();
    }

    public static class StufeConverter extends StringConverter<Stufe> {

        @Override
        public String toString(Stufe stufe) {
            return stufe.asString();
        }

        @Override
        public Stufe fromString(String string) {
            return Stufe.fromString(string);
        }
    }

    public static class YearMonthConverter extends StringConverter<YearMonth> {

        @Override
        public String toString(YearMonth yearMonth) {
            return yearMonth.toString();
        }

        @Override
        public YearMonth fromString(String string) {
            return YearMonth.parse(string);
        }
    }

    public static class MoneyConverter extends StringConverter<Money> {
        private static final MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder.of(Constants.getLocale())
                        .set(CurrencyStyle.SYMBOL)
                        .build());

        @Override
        public String toString(Money money) {
            return format.format(money);
        }

        @Override
        public Money fromString(String string) {
            return Money.parse(string);
        }
    }

    public static class PercentSpinnerFactory extends SpinnerValueFactory.IntegerSpinnerValueFactory {

        private static class PercentConverter extends IntegerStringConverter {

            @Override
            public String toString(Integer percent) {
                return String.format("%d%%", percent);
            }

            @Override
            public Integer fromString(String string) {
                String numStr = string.replaceAll("%", "");
                return numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
            }
        }

        public PercentSpinnerFactory() {
            super(0, 100, 100, 5);
            setConverter(new PercentConverter());
        }
    }
}
