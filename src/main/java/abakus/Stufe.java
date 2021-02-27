package abakus;

import java.time.YearMonth;

public enum Stufe {
	eins, zwei, drei, vier, fünf, sechs;

	public String asString() {
		return String.format("%d", ordinal() + 1);
	}

	public static Stufe fromString(final String ordStr) {
		return values()[Integer.valueOf(ordStr) - 1];
	}

	Stufe nächste() {
		return ordinal() < sechs.ordinal() ? values()[ordinal() + 1] : sechs;
	}

	YearMonth nächsterAufstieg(final YearMonth seit) {
		return seit.plusYears(ordinal() + 1L);
	}

	Stufe stufeAm(final YearMonth seit, final YearMonth am) {

		final Stufe n = nächste();
		if (n == this)
			return this;

		final YearMonth aufstieg = nächsterAufstieg(seit);
		return aufstieg.isAfter(am) ? this : n.stufeAm(aufstieg, am);
	}
}