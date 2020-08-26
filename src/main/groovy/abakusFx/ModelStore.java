package abakusFx;

import java.util.prefs.Preferences;

class ModelStore {
    public <T> String get(Class<T> clazz, String key) {
        Preferences prefs = Preferences.userNodeForPackage(clazz);
        return prefs.get(key, "");
    }

    public <T> void put(Class<T> clazz, String key, String modelString) {
        Preferences prefs = Preferences.userNodeForPackage(clazz);
        prefs.put(key, modelString);
    }
}
