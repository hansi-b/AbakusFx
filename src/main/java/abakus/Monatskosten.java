package abakus;

import java.time.YearMonth;
import java.util.Objects;

public class Monatskosten {

	public final YearMonth stichtag;
	public final Stelle stelle;
	public final ExplainedMoney kosten;

	Monatskosten(final YearMonth stichtag, final Stelle stelle, final ExplainedMoney kosten) {
		this.stichtag = stichtag;
		this.stelle = stelle;
		this.kosten = kosten;
	}

	@Override
	public int hashCode() {
		return Objects.hash(kosten, stelle, stichtag);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final Monatskosten other = (Monatskosten) obj;

		return Constants.eq(kosten, other.kosten) && //
				Constants.eq(stelle, other.stelle) && //
				Constants.eq(stichtag, other.stichtag);
	}
}