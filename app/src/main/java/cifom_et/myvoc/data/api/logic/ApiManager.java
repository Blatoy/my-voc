package cifom_et.myvoc.data.api.logic;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cifom_et.myvoc.R;
import cifom_et.myvoc.data.api.objects.Category;
import cifom_et.myvoc.data.api.objects.Language;
import cifom_et.myvoc.data.api.objects.Word;
import cifom_et.myvoc.data.database.DatabaseHelper;
import cifom_et.myvoc.data.database.DatabaseManager;
import cifom_et.myvoc.data.database.DatabaseStructure;
import cifom_et.myvoc.data.settings.SettingsHelper;
import cifom_et.myvoc.data.settings.SettingsStructure;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */

public abstract class ApiManager {
    public static ArrayList<Category> categories = new ArrayList<>();
    public static ArrayList<Language> languages = new ArrayList<>();

    /**
     * Load the languages and the categories, place them into 2 ArrayList
     *
     * @param ctx      -
     * @param callback The function to call after the languages and the words are loaded
     */
    public static void loadLanguageAndCategories(final Context ctx, final Runnable callback) {
        final DatabaseManager databaseManager = new DatabaseManager(ctx);
        final DatabaseHelper databaseHelper = new DatabaseHelper(ctx);

        // Reset lists
        ApiManager.languages = new ArrayList<>();
        ApiManager.categories = new ArrayList<>();

        // Load languages from database
        if (databaseManager.loadLanguages() && databaseManager.loadCategories())
            callback.run();

        new ApiHelper(ctx) {
            @Override
            public void onResponseReceived(String result) {
                try {
                    if (result == null || result.equals("")) {
                        // If there's no connection, data loaded from DB will persist
                        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                        if (!mWifi.isConnected() && SettingsHelper.getBoolean(ctx, SettingsStructure.WIFI_SYNC)) {
                            Toast.makeText(ctx, ctx.getResources().getText(R.string.error_wifi_sync_disabled), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ctx, ctx.getResources().getText(R.string.error_cannot_reach_api), Toast.LENGTH_LONG).show();
                        }

                        // TODO: Tests if there's no problem with broken wifi
                        callback.run();
                        return;
                    }

                    // Reset languages
                    ApiManager.languages = new ArrayList<>();

                    // Parse JSON
                    JSONObject mainObject = ApiHelper.parseJson(result);
                    JSONArray languages = mainObject.getJSONArray("data");

                    // Prepare database update
                    databaseHelper.truncate(DatabaseStructure.Language.TABLE_NAME);

                    for (int i = 0; i < languages.length(); i++) {
                        JSONObject language = languages.getJSONObject(i);

                        // Add data into database
                        databaseManager.insertLanguages(
                                language.getInt(ApiStructure.Language.RESULT_ID),
                                language.getString(ApiStructure.Language.RESULT_NAME));

                        ApiManager.languages.add(
                                new Language(
                                        language.getInt(ApiStructure.Language.RESULT_ID),
                                        language.getString(ApiStructure.Language.RESULT_NAME)));
                    }

                    // Load categories
                    new ApiHelper(ctx) {
                        @Override
                        public void onResponseReceived(String result) {
                            try {
                                ApiManager.categories = new ArrayList<>();

                                // If there's an error with the result
                                if (result == null || result == "") {
                                    return;
                                }

                                JSONObject mainObject = ApiHelper.parseJson(result);
                                // Parse JSON
                                JSONArray categories = mainObject.getJSONArray("data");

                                // Prepare database update
                                databaseHelper.truncate(DatabaseStructure.Category.TABLE_NAME);

                                for (int i = 0; i < categories.length(); i++) {
                                    // Parse JSON
                                    JSONObject category = categories.getJSONObject(i);

                                    // Insert categories into the database
                                    databaseManager.insertCategories(
                                            category.getInt(ApiStructure.Category.RESULT_ID),
                                            category.getString(ApiStructure.Category.RESULT_NAME),
                                            category.getInt(ApiStructure.Category.RESULT_LANGUAGE_BASE_ID),
                                            category.getInt(ApiStructure.Category.RESULT_LANGUAGE_TRANSLATION_ID));

                                    // Add categories to the lists
                                    ApiManager.categories.add(
                                            new Category(
                                                    category.getInt(ApiStructure.Category.RESULT_ID),
                                                    category.getString(ApiStructure.Category.RESULT_NAME),
                                                    category.getInt(ApiStructure.Category.RESULT_LANGUAGE_BASE_ID),
                                                    category.getInt(ApiStructure.Category.RESULT_LANGUAGE_TRANSLATION_ID)
                                            )
                                    );
                                }

                                // TODO: Check if there's new categories before displaying this toast
                                Toast.makeText(ctx, ctx.getResources().getText(R.string.categories_updated), Toast.LENGTH_SHORT).show();
                                callback.run();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute(ApiStructure.Category.API_URL, ApiStructure.Category.ACTION_GET_ALL, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(ApiStructure.Language.API_URL, ApiStructure.Language.ACTION_GET_ALL, null);
    }

    /**
     * @param idCategory The id of the category
     * @return The category with the specified ID if it exists
     */
    // Return the category for the selected ID
    public static Category getCategoryFromId(int idCategory) {
        for (int i = 0; i < ApiManager.categories.size(); i++) {
            if (ApiManager.categories.get(i).getId() == idCategory) {
                return ApiManager.categories.get(i);
            }
        }
        return null;
    }

    /**
     * Load the words from the specified category, put them into the List of the category
     *
     * @param ctx        -
     * @param idCategory The id of the category to load
     * @param callback   The function to call after the words are loaded
     */
    public static void loadWordsForCategory(final Context ctx, final int idCategory, final Runnable callback) {
        final DatabaseManager databaseManager = new DatabaseManager(ctx);
        final Category currentCategory = getCategoryFromId(idCategory);
        boolean loadedFromDatabase = false;

        // Load words from database
        if (databaseManager.loadWordsForCategory(idCategory, currentCategory)) {
            callback.run();
            loadedFromDatabase = true;
        }

        // Load words
        ContentValues cv = new ContentValues();
        cv.put(ApiStructure.Word.QUERY_CATEGORY, idCategory);

        final boolean finalLoadedFromDatabase = loadedFromDatabase;
        new ApiHelper(ctx) {
            @Override
            public void onResponseReceived(String result) {
                try {
                    // Check if it's a valid response
                    if (currentCategory == null || result == null || result == "") {
                        // Prevent list reset if there's no result, but list was loaded by DB
                        if (!finalLoadedFromDatabase)
                            callback.run();
                        return;
                    }

                    JSONObject mainObject = ApiHelper.parseJson(result);
                    final JSONArray words = mainObject.getJSONArray("data");

                    // Check if there's an update
                    if (currentCategory.getWords().size() > 0) {
                        ArrayList<Word> tmpList = new ArrayList<>();

                        // Create temp list to check if the voc is the same
                        for (int i = 0; i < words.length(); i++) {
                            JSONObject word = words.getJSONObject(i);

                            // Fill the temp list
                            tmpList.add(new Word(
                                    word.getInt(ApiStructure.Word.RESULT_ID),
                                    word.getString(ApiStructure.Word.RESULT_BASE),
                                    word.getString(ApiStructure.Word.RESULT_TRANSLATION),
                                    idCategory
                            ));
                        }

                        // Check size
                        if (currentCategory.getWords().size() != tmpList.size()) {
                            handleListUpdate(ctx, databaseManager, idCategory, words, currentCategory, callback);
                            return;
                        }

                        // Check every word
                        for (int i = 0; i < tmpList.size(); i++) {
                            Word tmpWord = tmpList.get(i);
                            Word defWord = currentCategory.getWords().get(i);

                            if (!tmpWord.getBase().equals(defWord.getBase()) || !tmpWord.getTranslation().equals(defWord.getTranslation())) {
                                handleListUpdate(ctx, databaseManager, idCategory, words, currentCategory, callback);
                                return;
                            }
                        }
                    } else {
                        // Import words from the API
                        importWords(words, databaseManager, idCategory, currentCategory);
                        callback.run();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(ApiStructure.Word.API_URL, ApiStructure.Word.ACTION_GET_FROM_CATEGORY, ApiHelper.getJSONStringFromValues(cv));
    }

    /**
     * Display the "proceed" update dialog, handle the result
     *
     * @param ctx             -
     * @param databaseManager The current databaseManager
     * @param idCategory      The id of the category to update
     * @param words           The list of words to update
     * @param currentCategory The current category
     * @param callback        The function to call after the update is finished
     */
    private static void handleListUpdate(Context ctx, final DatabaseManager databaseManager, final int idCategory, final JSONArray words, final Category currentCategory, final Runnable callback) {
        // Confirm reset list dialog
        new AlertDialog.Builder(ctx)
                .setTitle(R.string.word_update_available)
                .setMessage(R.string.word_update_available_desc)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear database, and update from API
                        databaseManager.clearWordsFromCategory(idCategory);
                        importWords(words, databaseManager, idCategory, currentCategory);
                        callback.run();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * Import the word from the API, put them into the database
     *
     * @param words           The list of updated words
     * @param databaseManager The current databaseManager
     * @param idCategory      The id of the category to update
     * @param currentCategory The category to put the word in
     */
    private static void importWords(JSONArray words, DatabaseManager databaseManager, int idCategory, Category currentCategory) {
        currentCategory.setWords(new ArrayList<Word>());

        for (int i = 0; i < words.length(); i++) {
            try {
                // Parse JSON
                JSONObject word = words.getJSONObject(i);

                // Add words into DB
                databaseManager.insertWord(
                        word.getInt(ApiStructure.Word.RESULT_ID),
                        idCategory,
                        word.getString(ApiStructure.Word.RESULT_BASE),
                        word.getString(ApiStructure.Word.RESULT_TRANSLATION));

                // Add words into the current list
                currentCategory.getWords().add(new Word(
                        word.getInt(ApiStructure.Word.RESULT_ID),
                        word.getString(ApiStructure.Word.RESULT_BASE),
                        word.getString(ApiStructure.Word.RESULT_TRANSLATION),
                        idCategory
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
