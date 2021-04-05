package abakusfx;

import abakus.Gruppe;
import abakus.Stelle;
import abakus.Stufe;

class GruppeStufe implements Comparable<GruppeStufe> {
	final Gruppe gruppe;
	final Stufe stufe;

	private GruppeStufe(final Gruppe gruppe, final Stufe stufe) {
		this.gruppe = gruppe;
		this.stufe = stufe;
	}

	static GruppeStufe of(final Stelle stelle) {
		return stelle != null ? new GruppeStufe(stelle.gruppe, stelle.stufe) : null;
	}

	@Override
	public int compareTo(final GruppeStufe o) {
		final int gCmp = gruppe.compareTo(o.gruppe);
		return gCmp != 0 ? gCmp : stufe.compareTo(o.stufe);
	}

	@Override
	public String toString() {
		return String.format("%s/%s", gruppe, stufe.asString());
	}
}