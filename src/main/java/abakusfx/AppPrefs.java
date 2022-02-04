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
package abakusfx;

import java.io.File;
import java.util.Optional;

import utils.EnumPrefs;
import utils.UserNodePrefs;

class AppPrefs {

	private static EnumPrefs<PrefKeys> fixedPrefs;

	/**
	 * poor person's dependency injection for preferences to use mocked prefs in
	 * tests
	 */
	static void fix(EnumPrefs<PrefKeys> prefs) {
		fixedPrefs = prefs;
	}

	/**
	 * this is called by the AppController
	 */
	static AppPrefs create() {
		EnumPrefs<PrefKeys> effectivePrefs = fixedPrefs != null ? fixedPrefs : new UserNodePrefs<>(App.class);
		return new AppPrefs(effectivePrefs);
	}

	enum PrefVersion {
		v1
	}

	enum PrefKeys {
		_version, lastProject, wasDisclaimerAccepted
	}

	private static final PrefVersion currentVersion = PrefVersion.v1;

	private final EnumPrefs<PrefKeys> prefs;

	private AppPrefs(EnumPrefs<PrefKeys> prefs) {
		this.prefs = prefs;
		ensureVersion();
	}

	/**
	 * Check our prefs for version information and update if necessary.
	 */
	private void ensureVersion() {

		if (!prefs.contains(PrefKeys._version)) {
			prefs.put(PrefKeys._version, PrefVersion.v1.name());
		}

		final String versionName = prefs.get(PrefKeys._version);
		try {
			final PrefVersion incomingVersion = PrefVersion.valueOf(versionName);
			if (incomingVersion != currentVersion)
				throw new IllegalStateException(String.format("Cannot handle outdated preferences version %s (need %s)",
						incomingVersion, currentVersion));
		} catch (final IllegalArgumentException ex) {
			throw new IllegalStateException(String.format("Cannot handle unknown preferences version %s (need %s)",
					versionName, currentVersion), ex);
		}
	}

	Optional<File> getLastProject() {
		final String s = prefs.get(PrefKeys.lastProject);
		return Optional.ofNullable(s).map(File::new);
	}

	void setLastProject(final File projectFile) {
		prefs.put(PrefKeys.lastProject, projectFile.getAbsolutePath());
	}

	void removeLastProject() {
		prefs.remove(PrefKeys.lastProject);
	}

	boolean wasDisclaimerAccepted() {
		return Boolean.valueOf(prefs.get(PrefKeys.wasDisclaimerAccepted));
	}

	void setDisclaimerAccepted(boolean wasAccepted) {
		prefs.put(PrefKeys.wasDisclaimerAccepted, Boolean.toString(wasAccepted));
	}
}
