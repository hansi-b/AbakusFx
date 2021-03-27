package abakus;

import java.time.YearMonth;
import java.util.Objects;

public class Monatskosten {

	public final YearMonth stichtag;
	public final Stelle stelle;
	public final ExplainedMoney brutto;
	public final ExplainedMoney sonderzahlung;

	Monatskosten(final YearMonth stichtag, final Stelle stelle, final ExplainedMoney brutto,
			final ExplainedMoney sonderzahlung) {
		this.stichtag = stichtag;
		this.stelle = stelle;
		this.brutto = brutto;
		this.sonderzahlung = sonderzahlung;
	}

	@Override
	public int hashCode() {
		return Objects.hash(brutto, sonderzahlung, stelle, stichtag);
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

		return Constants.eq(brutto, other.brutto) && //
				Constants.eq(sonderzahlung, other.sonderzahlung) && //
				Constants.eq(stelle, other.stelle) && //
				Constants.eq(stichtag, other.stichtag);
	}
}