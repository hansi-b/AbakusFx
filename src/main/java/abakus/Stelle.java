package abakus;

import java.math.BigDecimal;
import java.util.Objects;

public class Stelle {

	public final Gruppe gruppe;
	public final Stufe stufe;
	public final BigDecimal umfang;

	private Stelle(final Gruppe g, final Stufe s, final BigDecimal umfang) {
		this.gruppe = g;
		this.stufe = s;
		this.umfang = umfang;
	}

	public static Stelle of(Stelle stelle, Stufe stufe) {
		return new Stelle(stelle.gruppe, stufe, stelle.umfang);
	}

	public static Stelle of(final Gruppe g, final Stufe s, final int umfang) {
		return new Stelle(g, s, BigDecimal.valueOf(umfang));
	}

	static Stelle of(final Gruppe g, final Stufe s) {
		return Stelle.of(g, s, 100);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		final Stelle other = (Stelle) obj;
		return gruppe == other.gruppe && stufe == other.stufe && umfang.equals(other.umfang);
	}

	@Override
	public int hashCode() {
		return Objects.hash(gruppe, stufe, umfang);
	}
}
