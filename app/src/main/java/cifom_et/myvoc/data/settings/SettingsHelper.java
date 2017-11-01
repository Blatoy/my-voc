package cifom_et.myvoc.data.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public abstract class SettingsHelper {
    /**
     * @param ctx The current context
     * @param key The setting to get
     * @return the value or false
     */
    public static boolean getBoolean(Context ctx, String key) {
        SharedPreferences preference = getSharedPreference(ctx);
        return preference.getBoolean(key, false);
    }

    /**
     * @param ctx The current context
     * @param key The setting to get
     * @return the value or ""
     */
    public static String getString(Context ctx, String key) {
        SharedPreferences preference = getSharedPreference(ctx);
        return preference.getString(key, "");
    }

    /**
     * @param ctx The current context
     * @param key The setting to get
     * @return the value or -1
     */
    public static int getInt(Context ctx, String key) {
        SharedPreferences preference = getSharedPreference(ctx);
        return preference.getInt(key, -1);
    }

    /**
     * @param ctx   The current context
     * @param key   They key of the setting to set
     * @param value The value of the setting
     */
    public static void setBoolean(Context ctx, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(ctx);
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * @param ctx   The current context
     * @param key   They key of the setting to set
     * @param value The value of the setting
     */
    public static void setString(Context ctx, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(ctx);
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * @param ctx   The current context
     * @param key   They key of the setting to set
     * @param value The value of the setting
     */
    public static void setInt(Context ctx, String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(ctx);
        editor.putInt(key, value);
        // Maybe put commit?
        editor.apply();
    }

    /**
     * @param ctx The current context
     * @return a SharedPreferencesEditor
     */
    private static SharedPreferences.Editor getSharedPreferencesEditor(Context ctx) {
        return getSharedPreference(ctx).edit();
    }

    /**
     * @param ctx The current context
     * @return a SharedPreferences
     */
    private static SharedPreferences getSharedPreference(Context ctx) {
        return ctx.getSharedPreferences(SettingsStructure.DEFAULT_PREF_NAME, 0);
    }
}
