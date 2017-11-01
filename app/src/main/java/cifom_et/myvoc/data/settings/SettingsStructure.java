package cifom_et.myvoc.data.settings;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public abstract class SettingsStructure {
    public static final String SEARCH_BASE_LANGUAGE_ID = "baseLanguageId";
    public static final String SEARCH_TRANSLATION_LANGUAGE_ID = "translationLanguageId";
    public static final String API_BASE_PATH = "apiBasePath";
    public static final String WIFI_SYNC = "wifiSync";
    public static final String DEFAULT_QUIZ_ORDER = "defaultQuizOrder";
    public static final String DEFAULT_PREF_NAME = "appSettings";
    public static final String SET_UP_DONE = "firstLaunch";
    public static final int QUIZ_ORDER_ASK = 0;
    public static final int QUIZ_ORDER_BASE_TRANSLATION = 1;
    public static final int QUIZ_ORDER_TRANSLATION_BASE = 2;
}
