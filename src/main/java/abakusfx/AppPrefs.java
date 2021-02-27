package abakusfx;

import java.io.File;
import java.util.Optional;

class AppPrefs {

	enum PrefKeys {
		_version, lastProject
	}

	enum PrefVersion {
		v1
	}

	private static final PrefVersion currentVersion = PrefVersion.v1;

	private final PrefsAdapter<App, PrefKeys> prefs = new PrefsAdapter<>(App.class);

	static AppPrefs create() {
		AppPrefs prefs = new AppPrefs();
		prefs.initialize();
		return prefs;
	}

	/**
	 * check our prefs for version information and update if necessary
	 */
	private void initialize() {

		if (!prefs.contains(PrefKeys._version)) {
			prefs.put(PrefKeys._version, currentVersion.name());
		}

		String versionName = prefs.get(PrefKeys._version);
		try {
			PrefVersion incomingVersion = PrefVersion.valueOf(versionName);
			if (incomingVersion != currentVersion)
				throw new IllegalStateException(String.format("Cannot handle outdated preferences version %s (need %s)",
						incomingVersion, currentVersion));
		} catch (IllegalArgumentException ex) {
			throw new IllegalStateException(String.format("Cannot handle unknown preferences version %s (need %s)",
					versionName, currentVersion), ex);
		}
	}

	Optional<File> getLastProject() {
		String s = prefs.get(PrefKeys.lastProject);
		return Optional.ofNullable(s).map(File::new);
	}

	void setLastProject(File projectFile) {
		prefs.put(PrefKeys.lastProject, projectFile.getAbsolutePath());
	}

	void removeLastProject() {
		prefs.remove(PrefKeys.lastProject);
	}
}
