package abakusfx;

import java.util.prefs.Preferences;

/**
 * a thin wrapper around java Preferences with typed keys
 *
 * @param <T> the class for these preferences
 * @param <K> the key enum
 */
class PrefsAdapter<T, K extends Enum<K>> {

	static class PrefsException extends RuntimeException {

		private static final long serialVersionUID = -554364308103429180L;

		PrefsException(final Exception cause) {
			super(cause);
		}
	}

	private final Class<T> clazz;

	PrefsAdapter(final Class<T> clazz) {
		this.clazz = clazz;
	}

	public void put(final K key, final String value) {
		backingPrefs().put(key.name(), value);
	}

	public String get(final K key) {
		return backingPrefs().get(key.name(), null);
	}

	public boolean contains(final K key) throws PrefsException {
		return get(key) != null;
	}

	public void remove(final K key) {
		backingPrefs().remove(key.name());
	}

	private Preferences backingPrefs() {
		return Preferences.userNodeForPackage(clazz).node(clazz.getSimpleName());
	}
}
