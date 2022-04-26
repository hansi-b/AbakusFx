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

import java.util.Optional;

import org.hansib.sundries.prefs.OptEnum;
import org.hansib.sundries.prefs.OptFile;
import org.hansib.sundries.prefs.Prefs;
import org.hansib.sundries.prefs.ReqBoolean;
import org.hansib.sundries.prefs.store.UserNodePrefsStore;

class AppPrefs {

	private static Prefs<PrefKeys> fixedPrefs;

	/**
	 * poor person's dependency injection for a prefs store to use mocked prefs in
	 * tests
	 */
	static void fix(Prefs<PrefKeys> prefs) {
		fixedPrefs = prefs;
	}

	/**
	 * this is called by the AppController
	 */
	static AppPrefs create() {
		final Prefs<PrefKeys> effectivePrefs = fixedPrefs != null ? fixedPrefs
				: new Prefs<>(UserNodePrefsStore.forApp(App.class));
		return new AppPrefs(effectivePrefs);
	}

	enum PrefVersion {
		v1
	}

	private static final PrefVersion currentVersion = PrefVersion.v1;

	enum PrefKeys {
		_version, //
		lastProject, //
		wasDisclaimerAccepted
	}

	private final OptEnum<PrefVersion> version;

	private final OptFile lastProject;
	private final ReqBoolean disclaimerAccepted;

	private AppPrefs(Prefs<PrefKeys> prefs) {

		version = prefs.optionalEnum(PrefKeys._version, PrefVersion.class);

		lastProject = prefs.optionalFile(PrefKeys.lastProject);
		disclaimerAccepted = prefs.requiredBoolean(PrefKeys.wasDisclaimerAccepted, false);

		ensureVersion();
	}

	/**
	 * Check our prefs for version information and update if necessary.
	 */
	private void ensureVersion() {

		Optional<PrefVersion> incomingVersion = version.get();
		if (!incomingVersion.isPresent()) {
			version.set(currentVersion);
		} else {
			if (incomingVersion.get() != currentVersion)
				throw new IllegalStateException(String.format("Cannot handle preferences version %s (need %s)",
						incomingVersion.get(), currentVersion));
		}
	}

	OptFile lastProject() {
		return lastProject;
	}

	ReqBoolean disclaimerAccepted() {
		return disclaimerAccepted;
	}
}
