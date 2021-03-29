package abakus;

import java.math.BigDecimal;
import java.util.Objects;

public class Stelle {
	private static final BigDecimal BIG_100 = BigDecimal.valueOf(100);

	public final Gruppe gruppe;
	public final Stufe stufe;
	public final BigDecimal umfangPercent;

	private Stelle(final Gruppe g, final Stufe s, final BigDecimal umfangPercent) {
		this.gruppe = g;
		this.stufe = s;
		this.umfangPercent = umfangPercent;
	}

	public static Stelle of(final Stelle stelle, final Stufe stufe) {
		return new Stelle(stelle.gruppe, stufe, stelle.umfangPercent);
	}

	public static Stelle of(final Gruppe g, final Stufe s, final int umfang) {
		return new Stelle(g, s, BigDecimal.valueOf(umfang));
	}

	static Stelle of(final Gruppe g, final Stufe s) {
		return Stelle.of(g, s, 100);
	}

	public boolean istVollzeit() {
		return umfangPercent == BIG_100;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		final Stelle other = (Stelle) obj;
		return gruppe == other.gruppe && stufe == other.stufe && umfangPercent.equals(other.umfangPercent);
	}

	@Override
	public int hashCode() {
		return Objects.hash(gruppe, stufe, umfangPercent);
	}

	@Override
	public String toString() {
		return String.format("Stelle(%s/%s, %s%%)", gruppe, stufe, umfangPercent);
	}
}
