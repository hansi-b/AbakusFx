package abakusFx;

import java.util.prefs.Preferences;

/**
 * a thin wrapper around java Preferences with typed keys
 *
 * @param <T> the class for these preferences
 * @param <K> the key enum
 */
class PrefsAdapter<T, K extends Enum<K>> {

    static class PrefsException extends RuntimeException {
        PrefsException(Exception cause) {
            super(cause);
        }
    }

    private final Class<T> clazz;

    PrefsAdapter(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void put(K key, String value) {
        backingPrefs().put(key.name(), value);
    }

    public String get(K key) {
        return backingPrefs().get(key.name(), null);
    }

    public boolean contains(K key) throws PrefsException {
        return get(key) != null;
    }

    public void remove(K key) {
        backingPrefs().remove(key.name());
    }


    private Preferences backingPrefs() {
        return Preferences.userNodeForPackage(clazz).node(clazz.getSimpleName());
    }
}
