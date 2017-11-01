package cifom_et.myvoc.data.api.logic;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public abstract class ApiStructure {
    public abstract class Language {
        public static final String API_URL = "language.php";
        public static final String ACTION_GET_ALL = "getAll";
        public static final String RESULT_ID = "languageId";
        public static final String RESULT_NAME = "languageName";
        // May be used in the future (no plans right now)
        public static final String ACTION_GET_AVAILABLE = "getAvailable";
        public static final String ACTION_ADD = "add";
        public static final String ACTION_REMOVE = "remove";
        public static final String ACTION_SET = "set";
    }

    public abstract class Category {
        public static final String API_URL = "category.php";
        public static final String ACTION_GET_ALL = "getAll";
        public static final String RESULT_ID = "categoryId";
        public static final String RESULT_NAME = "categoryName";
        public static final String RESULT_LANGUAGE_BASE_ID = "languageIdBase";
        public static final String RESULT_LANGUAGE_TRANSLATION_ID = "languageIdTranslation";
        // May be used in the future (no plans right now)
        public static final String ACTION_GET_MATCHING = "getMatching";
        public static final String ACTION_ADD = "add";
        public static final String ACTION_REMOVE = "remove";
        public static final String ACTION_SET = "set";

    }

    public abstract class Word {
        public static final String API_URL = "word.php";
        public static final String ACTION_GET_FROM_CATEGORY = "getFromCategory";
        public static final String RESULT_ID = "wordId";
        public static final String RESULT_BASE = "wordBase";
        public static final String RESULT_TRANSLATION = "wordTranslation";
        public static final String QUERY_CATEGORY = "categoryId";
        // May be used in the future (no plans right now)
        public static final String ACTION_ADD = "add";
        public static final String ACTION_REMOVE = "remove";
        public static final String ACTION_SET = "set";
    }
}
