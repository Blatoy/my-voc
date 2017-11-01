package cifom_et.myvoc.logic;

import android.content.Context;

import cifom_et.myvoc.data.database.DatabaseManager;
import cifom_et.myvoc.data.settings.SettingsHelper;
import cifom_et.myvoc.data.settings.SettingsStructure;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public abstract class LogicSettings {
    public static void restoreDefaultSettings(Context ctx) {
        SettingsHelper.setString(ctx, SettingsStructure.API_BASE_PATH, "http://orikaru.net/standalone/my-voc");
        SettingsHelper.setInt(ctx, SettingsStructure.SEARCH_BASE_LANGUAGE_ID, -1);
        SettingsHelper.setInt(ctx, SettingsStructure.SEARCH_TRANSLATION_LANGUAGE_ID, -1);
        SettingsHelper.setBoolean(ctx, SettingsStructure.WIFI_SYNC, true);
        SettingsHelper.setInt(ctx, SettingsStructure.DEFAULT_QUIZ_ORDER, SettingsStructure.QUIZ_ORDER_ASK);
        LogicDemo.resetAllSequence(ctx);
        DatabaseManager databaseManager = new DatabaseManager(ctx);
        databaseManager.createDatabase();
    }

    public static void resetTutorial(Context ctx) {
        LogicDemo.resetAllSequence(ctx);
    }

}
