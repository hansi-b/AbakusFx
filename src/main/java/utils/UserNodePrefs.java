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

import java.util.prefs.Preferences;

/**
 * a thin wrapper around java Preferences with typed keys
 *
 * @param <K> the key enum
 */
public class UserNodePrefs<K extends Enum<K>> implements EnumPrefs<K> {

	private final Preferences node;

	UserNodePrefs(Preferences node) {
		this.node = node;
	}

	public static <L extends Enum<L>> UserNodePrefs<L> forApp(final Class<?> clazz) {
		return new UserNodePrefs<>(Preferences.userNodeForPackage(clazz).node(clazz.getSimpleName()));
	}

	public void put(final K key, final String value) {
		node.put(key.name(), value);
	}

	public String get(final K key) {
		return node.get(key.name(), null);
	}

	public boolean contains(final K key) throws PrefsException {
		return get(key) != null;
	}

	public void remove(final K key) {
		node.remove(key.name());
	}
}
