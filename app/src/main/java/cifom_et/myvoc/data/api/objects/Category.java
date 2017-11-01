package cifom_et.myvoc.data.api.objects;

import java.util.ArrayList;

import cifom_et.myvoc.data.api.logic.ApiManager;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public class Category {
    private int id;
    private String name;
    private int languageIdBase;
    private int languageIdTranslation;
    private ArrayList<Word> words = new ArrayList<>();

    /**
     * @param id                    The id of the category
     * @param name                  The name of the category
     * @param languageIdBase        The base language ID of the category
     * @param languageIdTranslation The translation language ID of the category
     */
    public Category(int id, String name, int languageIdBase, int languageIdTranslation) {
        this.id = id;
        this.name = name;
        this.languageIdBase = languageIdBase;
        this.languageIdTranslation = languageIdTranslation;
    }

    /**
     * @return The list of all the words in this category
     */
    // Getter and setters
    public ArrayList<Word> getWords() {
        return words;
    }

    /**
     * @param words Set the list of word
     */
    public void setWords(ArrayList<Word> words) {
        this.words = words;
    }

    /**
     * @return The id of this category
     */
    public int getId() {
        return id;
    }

    /**
     * @param id Set the id of this category
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The name of this category
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Set the name of this category
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Get the id of the bage language
     */
    public int getLanguageIdBase() {
        return languageIdBase;
    }

    /**
     * @param languageIdBase Set the id of the base language
     */
    public void setLanguageIdBase(int languageIdBase) {
        this.languageIdBase = languageIdBase;
    }

    /**
     * @return Get the id of the translation language
     */
    public int getLanguageIdTranslation() {
        return languageIdTranslation;
    }

    /**
     * @param languageIdTranslation Set the id for the translation language
     */
    public void setLanguageIdTranslation(int languageIdTranslation) {
        this.languageIdTranslation = languageIdTranslation;
    }

    /**
     * @return The name of the base language if it exists
     */
    public String getLanguageBaseName() {
        for (int i = 0; i < ApiManager.languages.size(); i++) {
            if (this.languageIdBase == ApiManager.languages.get(i).getId())
                return ApiManager.languages.get(i).getName();
        }
        return "";
    }

    /**
     * @return The name of the translation language if it exists
     */
    public String getLanguageTranslationName() {
        for (int i = 0; i < ApiManager.languages.size(); i++) {
            if (this.languageIdTranslation == ApiManager.languages.get(i).getId())
                return ApiManager.languages.get(i).getName();
        }
        return "";
    }

    /**
     * Reset the current round
     */
    public void resetAnswerCorrectly() {
        for (int i = 0; i < this.words.size(); i++) {
            this.words.get(i).setHasBeenAnsweredCorrectly(false);
            this.words.get(i).setRoundMissedCounter(0);
        }
    }

    /**
     * Reset all the words
     */
    public void resetAll() {
        for (int i = 0; i < this.words.size(); i++) {
            this.words.get(i).setHasBeenAnsweredCorrectlyFirstTime(false);
            this.words.get(i).setTotalMissedCounter(0);
            this.words.get(i).setRoundMissedCounter(0);
            this.words.get(i).setHasBeenAnsweredCorrectly(false);
            this.words.get(i).setRoundMissedCounter(0);
        }
    }

    /**
     * @return The number of fail for the most failed word
     */
    public int getMaxFailedWordCount() {
        int maxFailedCount = 0;
        for (int i = 0; i < this.words.size(); i++) {
            if (this.words.get(i).getTotalMissedCounter() > maxFailedCount)
                maxFailedCount = this.words.get(i).getTotalMissedCounter();
        }

        return maxFailedCount;
    }

    /**
     * @return The number of fail for the most failed word in the current round
     */
    public int getMaxFailedWordInRoundCount() {
        int maxFailedCount = 0;
        for (int i = 0; i < this.words.size(); i++) {
            if (this.words.get(i).getRoundMissedCounter() > maxFailedCount)
                maxFailedCount = this.words.get(i).getRoundMissedCounter();
        }
        return maxFailedCount;
    }

    /**
     * @param time The number of fail
     * @return The number of world failed time
     */
    public int getFailedTimeCount(int time) {
        int count = 0;
        for (int i = 0; i < this.getWords().size(); i++) {
            if (this.getWords().get(i).getTotalMissedCounter() == time)
                count++;
        }
        return count;
    }
}
