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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * keeps preferences in a simple map
 */
public class InMemoryPrefs<K extends Enum<K>> implements EnumPrefs<K> {

	private final Map<K, String> prefs;

	public InMemoryPrefs() {
		this.prefs = new ConcurrentHashMap<>();
	}

	public void put(final K key, final String value) {
		prefs.put(key, value);
	}

	public String get(final K key) {
		return prefs.getOrDefault(key, null);
	}

	public boolean contains(final K key) throws PrefsException {
		return get(key) != null;
	}

	public void remove(final K key) {
		prefs.remove(key);
	}
}
