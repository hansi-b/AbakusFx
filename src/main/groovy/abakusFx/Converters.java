package abakusFx;

import abakus.Stufe;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

public class Converters {

    public static StufeConverter createStufeConverter() {
        return new StufeConverter();
    }

    private static class StufeConverter extends StringConverter<Stufe> {

        @Override
        public String toString(Stufe stufe) {
            return stufe.asString();
        }

        @Override
        public Stufe fromString(String string) {
            return Stufe.fromString(string);
        }
    }

    public static PercentConverter createPercentConverter() {
        return new PercentConverter();
    }

    public static class PercentConverter extends IntegerStringConverter {

        @Override
        public String toString(Integer percent) {
            return String.format("%d%%", percent);
        }

        @Override
        public Integer fromString(String string) {
            String numStr = string.replaceAll("%", "");
            return numStr.isEmpty() ? 0 : Integer.valueOf(numStr);
        }
    }
}
