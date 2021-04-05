package abakusfx;

import java.util.List;
import java.util.Objects;

import org.javamoney.moneta.Money;

import abakus.Constants;

class PersonÜbersicht {
	final String name;
	final Money summe;

	PersonÜbersicht(final String name, final Money summe) {
		this.name = name;
		this.summe = summe;
	}

	static Money sumÜbersichten(final List<PersonÜbersicht> übersichten) {
		return übersichten.stream().map(k -> k.summe).filter(Objects::nonNull).reduce(Money::add)
				.orElseGet(() -> Constants.euros(0));
	}
}