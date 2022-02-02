/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2022 Hans Bering
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
package utils;

/**
 * a thin wrapper around java Preferences with typed keys
 *
 * @param <K> the key enum
 */
public interface EnumPrefs<K extends Enum<K>> {

	static class PrefsException extends RuntimeException {

		private static final long serialVersionUID = -554364308103429180L;

		PrefsException(final Exception cause) {
			super(cause);
		}
	}

	public void put(final K key, final String value) throws PrefsException;

	public String get(final K key) throws PrefsException;

	public boolean contains(final K key) throws PrefsException;

	public void remove(final K key) throws PrefsException;
}
