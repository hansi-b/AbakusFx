package abakusFx;

class SeriesPrefs {

    enum PrefKeys {
        _version,
        seriesSettings
    }

    enum PrefVersion {
        v1_singleSeries // single tab model
    }

    private final PrefVersion currentVersion = PrefVersion.v1_singleSeries;

    private final PrefsAdapter<SerieSettingsController, PrefKeys> prefs = new PrefsAdapter<>(SerieSettingsController.class);

    static SeriesPrefs create() {
        SeriesPrefs prefs = new SeriesPrefs();
        prefs.initialize();
        return prefs;
    }

    /**
     * check our prefs for version information and update if necessary
     */
    private void initialize() {

        if (!prefs.contains(PrefKeys._version)) {
            /* either never used here before, or we need to remove pre-version pref content */
            if (prefs.contains(PrefKeys.seriesSettings))
                prefs.remove(PrefKeys.seriesSettings);
            prefs.put(PrefKeys._version, currentVersion.name());
        }

        String versionName = prefs.get(PrefKeys._version);
        try {
            PrefVersion incomingVersion = PrefVersion.valueOf(versionName);
            if (incomingVersion != currentVersion)
                throw new IllegalStateException(String.format("Cannot handle outdated preferences version %s (need %s)", incomingVersion, currentVersion));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(String.format("Cannot handle unknown preferences version %s (need %s)", versionName, currentVersion));
        }
    }

    String getModelString() {
        return prefs.get(PrefKeys.seriesSettings);
    }

    void setModelString(String modelString) {
        prefs.put(PrefKeys.seriesSettings, modelString);
    }
}
