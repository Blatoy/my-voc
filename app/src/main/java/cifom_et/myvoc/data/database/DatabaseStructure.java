package cifom_et.myvoc.data.database;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public abstract class DatabaseStructure {
    // Define the database structure
    public static abstract class Category {
        public static final String TABLE_NAME = "category";
        public static final String COL_CATEGORY_ID = "CategoryID";
        public static final String COL_CATEGORY_NAME = "CategoryName";
        public static final String COL_LANGUAGE_BASE_ID = "LanguageIDBase";
        public static final String COL_LANGUAGE_TRANSLATION_ID = "LanguageIDTranslation";
    }

    public static abstract class Language {
        public static final String TABLE_NAME = "language";
        public static final String COL_LANGUAGE_ID = "LanguageID";
        public static final String COL_LANGUAGE_NAME = "LanguageName";
    }

    public static abstract class Word {
        public static final String TABLE_NAME = "word";
        public static final String COL_WORD_ID = "WordID";
        public static final String COL_WORD_BASE = "WordBase";
        public static final String COL_WORD_TRANSLATION = "WordTranslation";
        public static final String COL_CATEGORY_ID = "CategoryID";
        public static final String COL_ANSWER_CORRECTLY_SERIES = "AskedInCurrentSeries";
        public static final String COL_ANSWER_CORRECTLY_ROUND = "AskedInCurrentRound";
        public static final String COL_MISSED_IN_ROUND = "MissedInRound";
        public static final String COL_MISSED_TOTAL = "MissedTotal";
    }
}
