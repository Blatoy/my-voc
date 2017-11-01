package cifom_et.myvoc.data.api.objects;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public class Word {
    private int id;
    private String base;
    private String translation;
    private int categoryId;
    private int roundMissedCounter;
    private int totalMissedCounter;
    private boolean hasBeenAnsweredCorrectly;
    private boolean hasBeenAnsweredCorrectlyFirstTime;

    /**
     * Create a word that was never asked before
     *
     * @param id          The id of the word
     * @param base        The "definition" of the word
     * @param translation The "meaning" of the word
     * @param categoryId  The category of this
     */
    public Word(int id, String base, String translation, int categoryId) {
        this.id = id;
        this.base = base;
        this.translation = translation;
        this.categoryId = categoryId;
        this.roundMissedCounter = 0;
        this.hasBeenAnsweredCorrectly = false;
    }

    /**
     * @param id                         The id of the word
     * @param base                       The base translation of this word
     * @param translation                The target translation of this word
     * @param categoryId                 The id of the category
     * @param answeredCorrectly          If the word has already been answered correctly
     * @param answeredCorrectlyFirstTime If the word has been answered correctly the first time
     * @param roundMissedCounter         The number of time the word has been missed this round
     * @param totalMissedCounter         The number of time the word has been missed
     */
    public Word(int id, String base, String translation, int categoryId, boolean answeredCorrectly, boolean answeredCorrectlyFirstTime, int roundMissedCounter, int totalMissedCounter) {
        this(id, base, translation, categoryId);
        this.hasBeenAnsweredCorrectlyFirstTime = answeredCorrectlyFirstTime;
        this.hasBeenAnsweredCorrectly = answeredCorrectly;
        this.roundMissedCounter = roundMissedCounter;
        this.totalMissedCounter = totalMissedCounter;
    }

    /**
     * @return The number of time the word has been failed
     */
    public int getTotalMissedCounter() {
        return totalMissedCounter;
    }

    /**
     * @param totalMissedCounter The number of time the word has been failed
     */
    public void setTotalMissedCounter(int totalMissedCounter) {
        this.totalMissedCounter = totalMissedCounter;
    }

    /**
     * @return If the word has been answer correctly in the round
     */
    public boolean hasBeenAnsweredCorrectly() {
        return hasBeenAnsweredCorrectly;
    }

    /**
     * @param hasBeenAnsweredCorrectly Set if the word has been answered correctly in the round
     */
    public void setHasBeenAnsweredCorrectly(boolean hasBeenAnsweredCorrectly) {
        this.hasBeenAnsweredCorrectly = hasBeenAnsweredCorrectly;
    }

    /**
     * @return If the word has been answered correctly on the first try
     */
    public boolean hasBeenAnsweredCorrectlyFirstTime() {
        return hasBeenAnsweredCorrectlyFirstTime;
    }

    /**
     * @param hasBeenAnsweredCorrectlyFirstTime Set if the word has been answered correctly on the first try
     */
    public void setHasBeenAnsweredCorrectlyFirstTime(boolean hasBeenAnsweredCorrectlyFirstTime) {
        this.hasBeenAnsweredCorrectlyFirstTime = hasBeenAnsweredCorrectlyFirstTime;
    }

    /**
     * @return The number of time the word has been missed in the round
     */
    public int getRoundMissedCounter() {
        return roundMissedCounter;
    }

    /**
     * @param roundMissedCounter Set the number of time the word has been missed in the round
     */
    public void setRoundMissedCounter(int roundMissedCounter) {
        this.roundMissedCounter = roundMissedCounter;
    }

    /**
     * Increment the round and the total missed counter
     */
    public void incrementFailedCounter() {
        this.roundMissedCounter++;
        this.totalMissedCounter++;
    }

    /**
     * @return The id of the word
     */
    public int getId() {
        return id;
    }

    /**
     * @param id Set the id of the word
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Return the "definition" of the word
     */
    public String getBase() {
        return base;
    }

    /**
     * @param base Set the "definition" of the word
     */
    public void setBase(String base) {
        this.base = base;
    }

    /**
     * @return Return the "meaning" of the word
     */
    public String getTranslation() {
        return translation;
    }

    /**
     * @param translation Set the meaning of the word
     */
    public void setTranslation(String translation) {
        this.translation = translation;
    }

    /**
     * @return The category id of the word
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId Set the category id of the word
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
