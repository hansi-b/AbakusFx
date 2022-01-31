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

class AppPrefs {

	/**
	 * poor person's dependency injection for preferences to use mocked prefs in
	 * tests
	 */
	static class Factory {
		private static AppPrefs fixedPrefs = null;

		static void fixed(final AppPrefs appPrefs) {
			fixedPrefs = appPrefs;
		}

		static AppPrefs create() {
			if (fixedPrefs != null)
				return fixedPrefs;

			final AppPrefs appPrefs = new AppPrefs();
			initialize(appPrefs.prefs);
			return appPrefs;
		}

		/**
		 * check our prefs for version information and update if necessary
		 */
		private static void initialize(final PrefsAdapter<App, PrefKeys> prefs) {

			if (!prefs.contains(PrefKeys._version)) {
				prefs.put(PrefKeys._version, PrefVersion.v1.name());
			}

			final String versionName = prefs.get(PrefKeys._version);
			try {
				final PrefVersion incomingVersion = PrefVersion.valueOf(versionName);
				if (incomingVersion != currentVersion)
					throw new IllegalStateException(
							String.format("Cannot handle outdated preferences version %s (need %s)", incomingVersion,
									currentVersion));
			} catch (final IllegalArgumentException ex) {
				throw new IllegalStateException(String.format("Cannot handle unknown preferences version %s (need %s)",
						versionName, currentVersion), ex);
			}
		}
	}

	enum PrefVersion {
		v1
	}

	enum PrefKeys {
		_version, lastProject, wasDisclaimerAccepted
	}

	private static final PrefVersion currentVersion = PrefVersion.v1;

	private final PrefsAdapter<App, PrefKeys> prefs = new PrefsAdapter<>(App.class);

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
