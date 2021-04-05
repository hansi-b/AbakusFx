package abakusfx;

import java.util.List;
import java.util.Objects;

import org.javamoney.moneta.Money;

import abakus.Constants;

class PersonÜbersicht {
	final String name;
	final SeriesÜbersicht serie;

	PersonÜbersicht(final String name, final SeriesÜbersicht serie) {
		this.name = name;
		this.serie = serie;
	}

	static Money sumÜbersichten(final List<PersonÜbersicht> übersichten) {
		return übersichten.stream().map(p -> p.serie.summe).filter(Objects::nonNull).reduce(Money::add)
				.orElseGet(() -> Constants.euros(0));
	}
}