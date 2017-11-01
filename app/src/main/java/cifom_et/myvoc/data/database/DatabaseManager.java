package cifom_et.myvoc.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import cifom_et.myvoc.data.api.logic.ApiManager;
import cifom_et.myvoc.data.api.objects.Category;
import cifom_et.myvoc.data.api.objects.Language;
import cifom_et.myvoc.data.api.objects.Word;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * @param ctx -
     */
    public DatabaseManager(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Create all the database tables
     */
    public void createDatabase() {
        // Category table
        db.execSQL("CREATE TABLE IF NOT EXISTS '" + DatabaseStructure.Category.TABLE_NAME + "' " +
                "(" +
                "'" + DatabaseStructure.Category.COL_CATEGORY_ID + "' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'" + DatabaseStructure.Category.COL_CATEGORY_NAME + "' VARCHAR(50), " +
                "'" + DatabaseStructure.Category.COL_LANGUAGE_BASE_ID + "' SMALLINT(5), " +
                "'" + DatabaseStructure.Category.COL_LANGUAGE_TRANSLATION_ID + "' SMALLINT(5) " +
                ");");

        // Language table
        this.db.execSQL("CREATE TABLE IF NOT EXISTS '" + DatabaseStructure.Language.TABLE_NAME + "' " +
                "(" +
                "'" + DatabaseStructure.Language.COL_LANGUAGE_ID + "' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'" + DatabaseStructure.Language.COL_LANGUAGE_NAME + "' VARCHAR(25)" +
                ");");

        // Word table
        this.db.execSQL("CREATE TABLE IF NOT EXISTS '" + DatabaseStructure.Word.TABLE_NAME + "' " +
                "(" +
                "'" + DatabaseStructure.Word.COL_WORD_ID + "' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'" + DatabaseStructure.Word.COL_WORD_BASE + "' INTEGER, " +
                "'" + DatabaseStructure.Word.COL_WORD_TRANSLATION + "' INTEGER, " +
                "'" + DatabaseStructure.Word.COL_CATEGORY_ID + "' INTEGER, " +
                "'" + DatabaseStructure.Word.COL_ANSWER_CORRECTLY_ROUND + "' INTEGER, " +
                "'" + DatabaseStructure.Word.COL_MISSED_IN_ROUND + "' INTEGER, " +
                "'" + DatabaseStructure.Word.COL_MISSED_TOTAL + "' INTEGER, " +
                "'" + DatabaseStructure.Word.COL_ANSWER_CORRECTLY_SERIES + "' VARCHAR(25)" +
                ");");
    }

    /**
     * @param idCategory      The id of the category
     * @param currentCategory The current category, word will be put in it
     * @return True if there's at least one result
     */
    public boolean loadWordsForCategory(int idCategory, Category currentCategory) {
        currentCategory.setWords(new ArrayList<Word>());
        // Retrieve data from DB
        String[] a = new String[1];
        a[0] = idCategory + "";
        Cursor c = dbHelper.getResults(DatabaseStructure.Word.TABLE_NAME, null, DatabaseStructure.Word.COL_CATEGORY_ID + " = ?", a, DatabaseStructure.Word.COL_WORD_ID);
        if (c.moveToFirst()) {
            do {
                // Add words to the category
                currentCategory.getWords().add(
                        new Word(
                                c.getInt(c.getColumnIndex(DatabaseStructure.Word.COL_WORD_ID)),
                                c.getString(c.getColumnIndex(DatabaseStructure.Word.COL_WORD_BASE)),
                                c.getString(c.getColumnIndex(DatabaseStructure.Word.COL_WORD_TRANSLATION)),
                                idCategory,
                                false,
                                (c.getInt(c.getColumnIndex(DatabaseStructure.Word.COL_ANSWER_CORRECTLY_SERIES)) == 1),
                                0,
                                c.getInt(c.getColumnIndex(DatabaseStructure.Word.COL_MISSED_TOTAL))
                        ));
            } while (c.moveToNext());
            c.close();
        }
        return c.getCount() > 0;
    }

    /**
     * Delete each table and recreate them
     */
    public void resetDatabase() {
        // Clear each database
        this.db.execSQL("DROP TABLE '" + DatabaseStructure.Word.TABLE_NAME + "'");
        this.db.execSQL("DROP TABLE '" + DatabaseStructure.Language.TABLE_NAME + "'");
        this.db.execSQL("DROP TABLE '" + DatabaseStructure.Category.TABLE_NAME + "'");
        // Recreate database
        createDatabase();
    }

    /**
     * Insert a word (without progression) into the database
     *
     * @param wordId      The id of the word to insert
     * @param idCategory  The id of the category to insert
     * @param base        The "definition" of the word
     * @param translation The "meaning" of the word
     */
    public void insertWord(int wordId, int idCategory, String base, String translation) {
        // Insert a word into the database
        ContentValues cv = new ContentValues();
        cv.put(DatabaseStructure.Word.COL_WORD_ID, wordId);
        cv.put(DatabaseStructure.Word.COL_CATEGORY_ID, idCategory);
        cv.put(DatabaseStructure.Word.COL_WORD_BASE, base);
        cv.put(DatabaseStructure.Word.COL_WORD_TRANSLATION, translation);

        dbHelper.insert(DatabaseStructure.Word.TABLE_NAME, cv);
    }

    /**
     * Insert a category into the database
     *
     * @param id                    The id of the category
     * @param name                  The name of the category
     * @param languageBaseId        The "definition" language of the category
     * @param languageTranslationId The "meaning" language of the category
     */
    public void insertCategories(int id, String name, int languageBaseId, int languageTranslationId) {
        // Insert a category into the database
        ContentValues cv = new ContentValues();
        cv.put(DatabaseStructure.Category.COL_CATEGORY_ID, id);
        cv.put(DatabaseStructure.Category.COL_CATEGORY_NAME, name);
        cv.put(DatabaseStructure.Category.COL_LANGUAGE_BASE_ID, languageBaseId);
        cv.put(DatabaseStructure.Category.COL_LANGUAGE_TRANSLATION_ID, languageTranslationId);

        dbHelper.insert(DatabaseStructure.Category.TABLE_NAME, cv);
    }

    /**
     * Load all the languages from the database
     *
     * @return true if the languages were load
     */
    public boolean loadLanguages() {
        // Load languages from the database
        Cursor c = dbHelper.getResults(DatabaseStructure.Language.TABLE_NAME, null, null, null, DatabaseStructure.Language.COL_LANGUAGE_NAME);
        if (c.moveToFirst()) {
            do {
                ApiManager.languages.add(
                        new Language(
                                c.getInt(c.getColumnIndex(DatabaseStructure.Language.COL_LANGUAGE_ID)),
                                c.getString(c.getColumnIndex(DatabaseStructure.Language.COL_LANGUAGE_NAME))));
            } while (c.moveToNext());
            c.close();
        }
        return c.getCount() > 0;
    }

    /**
     * Load all the categories from the database
     *
     * @return true if the categories were load
     */
    public boolean loadCategories() {
        // load categories from the DB
        Cursor c = dbHelper.getResults(DatabaseStructure.Category.TABLE_NAME, null, null, null, DatabaseStructure.Category.COL_CATEGORY_NAME);
        if (c.moveToFirst()) {
            do {
                ApiManager.categories.add(
                        new Category(
                                c.getInt(c.getColumnIndex(DatabaseStructure.Category.COL_CATEGORY_ID)),
                                c.getString(c.getColumnIndex(DatabaseStructure.Category.COL_CATEGORY_NAME)),
                                c.getInt(c.getColumnIndex(DatabaseStructure.Category.COL_LANGUAGE_BASE_ID)),
                                c.getInt(c.getColumnIndex(DatabaseStructure.Category.COL_LANGUAGE_TRANSLATION_ID))));
            } while (c.moveToNext());
            c.close();
        }
        return c.getCount() > 0;
    }

    /**
     * Insert a language into the database
     *
     * @param id   The id of the language
     * @param name The name of the language
     */
    public void insertLanguages(int id, String name) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseStructure.Language.COL_LANGUAGE_ID, id);
        cv.put(DatabaseStructure.Language.COL_LANGUAGE_NAME, name);
        dbHelper.insert(DatabaseStructure.Language.TABLE_NAME, cv);
    }

    /**
     * Remove all the words from a category
     *
     * @param idCategory The id of the category to empty
     */
    public void clearWordsFromCategory(int idCategory) {
        String[] a = new String[1];
        a[0] = idCategory + "";
        dbHelper.delete(DatabaseStructure.Word.TABLE_NAME, DatabaseStructure.Word.COL_CATEGORY_ID + " = ?", a);
    }

    /**
     * Reset all the word in a category
     *
     * @param idCategory The id of the category to reset
     */
    public void resetWordFromCategory(int idCategory) {
        String[] a = new String[1];
        a[0] = idCategory + "";

        ContentValues cv = new ContentValues();
        cv.put(DatabaseStructure.Word.COL_ANSWER_CORRECTLY_SERIES, 0);
        cv.put(DatabaseStructure.Word.COL_ANSWER_CORRECTLY_ROUND, 0);
        cv.put(DatabaseStructure.Word.COL_MISSED_TOTAL, 0);
        cv.put(DatabaseStructure.Word.COL_MISSED_IN_ROUND, 0);


        dbHelper.update(DatabaseStructure.Word.TABLE_NAME, cv, DatabaseStructure.Word.COL_CATEGORY_ID + " = ?", a);
    }
}
