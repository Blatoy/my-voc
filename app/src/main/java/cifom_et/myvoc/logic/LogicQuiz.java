package cifom_et.myvoc.logic;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Random;

import cifom_et.myvoc.activities.ActivityResult;
import cifom_et.myvoc.adapters.AdapterQuiz;
import cifom_et.myvoc.data.api.logic.ApiManager;
import cifom_et.myvoc.data.api.objects.Category;
import cifom_et.myvoc.data.api.objects.Word;
import cifom_et.myvoc.data.database.DatabaseStructure;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */

public abstract class LogicQuiz {
    public static Category currentCategory;
    public static ArrayList<Word> currentWordSeries;

    public static int currentProgress;
    public static Word currentWord;

    public static boolean reverseOrder;

    /**
     * Initiate the quiz
     *
     * @param ctx         The current context
     * @param adapterQuiz The quiz adapter
     * @param categoryId  The category to start
     */
    public static void startQuestioner(final Context ctx, final AdapterQuiz adapterQuiz, int categoryId) {
        // Reset
        currentProgress = 0;
        currentWord = null;
        currentWordSeries = null;

        currentCategory = findCategoryById(categoryId);

        // Display the name of the category
        adapterQuiz.setProgression(currentCategory.getName(), 0, 0);
        // Check if word are already loaded
        if (currentCategory != null) {
            adapterQuiz.setLoadingVisibility(true);
            ApiManager.loadWordsForCategory(ctx, currentCategory.getId(), new Runnable() {
                @Override
                public void run() {
                    if (currentCategory == null || currentCategory.getWords() == null || currentCategory.getWords().size() == 0) {
                        adapterQuiz.displayListEmptyDialog();
                    } else {
                        createSeries();
                        askQuestion(ctx, adapterQuiz);
                        adapterQuiz.setLoadingVisibility(false);
                    }
                }
            });
        } else {
            createSeries();
            askQuestion(ctx, adapterQuiz);
            adapterQuiz.setLoadingVisibility(false);
        }
    }

    /**
     * Get the category with the specified ID
     *
     * @param categoryId The id of the category
     * @return The category
     */
    public static Category findCategoryById(int categoryId) {
        for (int i = 0; i < ApiManager.categories.size(); i++) {
            if (ApiManager.categories.get(i).getId() == categoryId)
                return ApiManager.categories.get(i);
        }
        return null;
    }

    /**
     * Start a new round of the quiz
     */
    public static void startNewRound() {
        currentCategory.resetAnswerCorrectly();
        currentProgress = 0;
        createSeries();
    }

    /**
     * Choose random words to create a serie
     */
    public static void createSeries() {
        // IMPROVE: Retrieve number of word by series in settings
        currentWordSeries = new ArrayList<>();
        int numberWordsBySeries = 10;
        int numberWordsLeft = getWordLeftCount(currentCategory.getWords());
        numberWordsBySeries = currentCategory.getWords().size() > numberWordsBySeries ? numberWordsBySeries : currentCategory.getWords().size();
        numberWordsBySeries = numberWordsLeft > numberWordsBySeries ? numberWordsBySeries : numberWordsLeft;

        for (int i = 0; i < numberWordsBySeries; i++) {
            Word w;
            do {
                w = currentCategory.getWords().get((new Random().nextInt(currentCategory.getWords().size())));
            }
            while (wordListContainsWord(currentWordSeries, w) || w.hasBeenAnsweredCorrectlyFirstTime());
            currentWordSeries.add(w);
        }
    }

    /**
     * @param wordList A list of word
     * @return The number of word left
     */
    public static int getWordLeftCount(ArrayList<Word> wordList) {
        int counter = 0;
        for (int i = 0; i < wordList.size(); i++) {
            if (wordList.get(i).hasBeenAnsweredCorrectlyFirstTime())
                counter++;
        }
        return wordList.size() - counter;
    }

    /**
     * Choose a random word to display, or start the result activity if there's no more word
     *
     * @param ctx         The current context
     * @param adapterQuiz The quiz adapter
     */
    public static void askQuestion(Context ctx, AdapterQuiz adapterQuiz) {
        // Update current progress
        adapterQuiz.setProgression(currentCategory.getName(), currentProgress, currentWordSeries.size());

        // Check if the series is finished
        if (currentWordSeries.size() <= currentProgress) {
            Intent intent = new Intent(ctx, ActivityResult.class).putExtra(LogicSelect.INTENT_EXTRA_CATEGORY_ID, currentCategory.getId());
            ctx.startActivity(intent);
            ((Activity) ctx).finish();
            return;
        }

        // Find a random word
        for (int i = 0; i < currentWordSeries.size(); i++) {
            do {
                currentWord = currentWordSeries.get((new Random().nextInt(currentWordSeries.size())));
            } while (currentWord.hasBeenAnsweredCorrectly());
        }

        adapterQuiz.setCardText(currentWord.getBase(), currentWord.getTranslation(), reverseOrder);
    }

    /**
     * @param wordArrayList The arrayList with all words
     * @param word          The word to search
     * @return True if the word is in the wordArrayList
     */
    public static boolean wordListContainsWord(ArrayList<Word> wordArrayList, Word word) {
        for (int i = 0; i < wordArrayList.size(); i++) {
            if (wordArrayList.get(i).equals(word))
                return true;
        }
        return false;
    }

    /**
     * Update the current progress, set "hasBeenAnsweredCorrectly" and play the animation
     *
     * @param isRight     True if the user was right, false otherwise
     * @param adapterQuiz The current quiz adapter
     */
    public static void handleUserResponse(boolean isRight, AdapterQuiz adapterQuiz) {
        if (isRight) {
            // Increment the progress
            currentProgress++;
            // Don't ask again in round
            currentWord.setHasBeenAnsweredCorrectly(true);

            // Don't ask again in the serie if it was right on the first time
            if (currentWord.getRoundMissedCounter() == 0) {
                currentWord.setHasBeenAnsweredCorrectlyFirstTime(true);
            }
        } else {
            currentWord.incrementFailedCounter();
        }

        adapterQuiz.playAnimationDisplayNextCard(isRight, adapterQuiz);

        // Update the database
        ContentValues cv = new ContentValues();
        cv.put(DatabaseStructure.Word.COL_ANSWER_CORRECTLY_SERIES, currentWord.hasBeenAnsweredCorrectlyFirstTime());
        cv.put(DatabaseStructure.Word.COL_ANSWER_CORRECTLY_ROUND, currentWord.hasBeenAnsweredCorrectly());
        cv.put(DatabaseStructure.Word.COL_MISSED_TOTAL, currentWord.getTotalMissedCounter());
        cv.put(DatabaseStructure.Word.COL_MISSED_IN_ROUND, currentWord.getRoundMissedCounter());

        String[] a = new String[1];
        a[0] = currentWord.getId() + "";

        adapterQuiz.getDatabaseHelper().update(DatabaseStructure.Word.TABLE_NAME, cv, DatabaseStructure.Word.COL_WORD_ID + " = ?", a);
    }


}
