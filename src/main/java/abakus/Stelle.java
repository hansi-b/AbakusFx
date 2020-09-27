package abakus;

import java.math.BigDecimal;
import java.util.Objects;

public class Stelle {
	public final Gruppe gruppe;
	public final Stufe stufe;
	public final BigDecimal umfang;

	Stelle(final Gruppe g, final Stufe s, final BigDecimal umfang) {
		this.gruppe = g;
		this.stufe = s;
		this.umfang = umfang;
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Stelle other = (Stelle) obj;
		if (gruppe != other.gruppe)
			return false;
		if (stufe != other.stufe)
			return false;
		if (umfang == null)
			return other.umfang == null;

		return umfang.equals(other.umfang);
	}

	@Override
	public int hashCode() {
		return Objects.hash(gruppe, stufe, umfang);
	}
}
