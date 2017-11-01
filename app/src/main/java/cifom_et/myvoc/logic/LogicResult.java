package cifom_et.myvoc.logic;

import cifom_et.myvoc.adapters.AdapterResult;
import cifom_et.myvoc.data.api.objects.Word;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 * http://i.imgur.com/btRTnmY.jpg #easter egg
 */
public abstract class LogicResult {
    /**
     * Update the quiz status
     *
     * @param adapterResult The result adapter
     */
    public static void updateQuizStatus(AdapterResult adapterResult) {
        int wordLeft = LogicQuiz.getWordLeftCount(LogicQuiz.currentCategory.getWords());
        int categoryWordSize = LogicQuiz.currentCategory.getWords().size();
        boolean displayCategoryHeader;
        adapterResult.setProgression(LogicQuiz.currentCategory.getName(), categoryWordSize - wordLeft, categoryWordSize);

        if (wordLeft == 0) {
            adapterResult.displayStopButton();

            // Display all words
            for (int i = 0; i <= LogicQuiz.currentCategory.getMaxFailedWordCount(); i++) {
                displayCategoryHeader = true;
                for (int j = 0; j < LogicQuiz.currentCategory.getWords().size(); j++) {
                    Word currentWord = LogicQuiz.currentCategory.getWords().get(j);
                    if (currentWord.getTotalMissedCounter() == i) {
                        if (displayCategoryHeader) {
                            double percentage = ((double) LogicQuiz.currentCategory.getFailedTimeCount(i) / (double) LogicQuiz.currentCategory.getWords().size()) * 100;
                            adapterResult.addCategoryHeaderToResultList(i, percentage);
                            displayCategoryHeader = false;
                        }
                        adapterResult.addWordToResultList(
                                currentWord.getBase(),
                                currentWord.getTranslation());
                    }
                }
            }
        } else {
            // Display round words
            for (int i = 0; i <= LogicQuiz.currentCategory.getMaxFailedWordInRoundCount(); i++) {
                displayCategoryHeader = true;
                for (int j = 0; j < LogicQuiz.currentWordSeries.size(); j++) {
                    Word currentWord = LogicQuiz.currentWordSeries.get(j);
                    // Show only asked words
                    if (!currentWord.hasBeenAnsweredCorrectly())
                        continue;
                    if (currentWord.getRoundMissedCounter() == i) {
                        if (displayCategoryHeader) {
                            adapterResult.addCategoryHeaderToResultList(i, -1);
                            displayCategoryHeader = false;
                        }
                        adapterResult.addWordToResultList(
                                currentWord.getBase(),
                                currentWord.getTranslation());
                    }
                }
            }
        }
    }
}
