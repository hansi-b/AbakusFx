/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2021  Hans Bering
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
