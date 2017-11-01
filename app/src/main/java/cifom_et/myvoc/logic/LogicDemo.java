package cifom_et.myvoc.logic;

import android.content.Context;

import cifom_et.myvoc.R;
import cifom_et.myvoc.adapters.AdapterDemo;
import cifom_et.myvoc.adapters.AdapterQuiz;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.RectangleShape;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public abstract class LogicDemo {
    public static final String DEMO_QUIZ = "demoQuiz";
    public static final String DEMO_SELECT = "demoSelect";
    public static final String DEMO_RESULT = "demoResult";
    public static int demoProgress;

    /**
     * Start the quiz demo
     *
     * @param adapterDemo The demo adapter of the activity
     * @param adapterQuiz The quiz adapter of the activity
     */
    public static void startQuizDemo(final AdapterDemo adapterDemo, final AdapterQuiz adapterQuiz) {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(10);

        MaterialShowcaseSequence sequence = adapterDemo.getSequence();

        sequence.singleUse(DEMO_QUIZ);
        sequence.setConfig(config);

        config.setShape(new RectangleShape(0, 0));
        // sequence.addSequenceItem(((Activity) ctx).findViewById(R.id.quizCategoryName), "This shows the name and the progression of the current set.", "");
        adapterDemo.addSequenceItem(sequence, R.id.quiz_question, R.string.demo_quiz_translation);
        adapterDemo.addSequenceItem(sequence, R.id.demo_card, R.string.demo_quiz_show_answer);
        adapterDemo.addSequenceItem(sequence, R.id.demo_card, R.string.demo_quiz_show_next);
        adapterDemo.addSequenceItem(sequence, R.id.quiz_demo_fullscreen, R.string.demo_quiz_end);

        // Tracks user progression
        sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
            @Override
            public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {
                demoProgress++;
            }
        });

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(final MaterialShowcaseView materialShowcaseView, int i) {
                switch (demoProgress) {
                    case 1:
                        adapterQuiz.setDemoImage(true);
                        adapterQuiz.setDemoImageVisibility(true);
                        adapterQuiz.addDemoDisplayAnswerListener(materialShowcaseView);
                        break;
                    case 2:
                        adapterQuiz.setDemoImage(false);
                        adapterQuiz.setDemoImageVisibility(true);
                        adapterQuiz.addDemoNextWordListener(materialShowcaseView, adapterQuiz);
                        break;
                    default:
                        adapterQuiz.setDemoImageVisibility(false);
                        adapterDemo.addDemoTouchListener(materialShowcaseView);
                        break;
                }
            }
        });
        sequence.start();
    }

    /**
     * Start the "select" demo
     *
     * @param adapterDemo The adapterDemo of the activity
     */
    public static void startSelectDemo(final AdapterDemo adapterDemo) {
        ShowcaseConfig config = new ShowcaseConfig();
        MaterialShowcaseSequence sequence = adapterDemo.getSequence();

        config.setDelay(10);
        config.setShape(new RectangleShape(0, 0));

        sequence.setConfig(config);
        sequence.singleUse(DEMO_SELECT);
        adapterDemo.addSequenceItem(sequence, R.id.select_search_category, R.string.demo_select_filter_search);
        adapterDemo.addSequenceItem(sequence, R.id.select_search_language_1, R.string.demo_select_filter_spinner);
        adapterDemo.addSequenceItem(sequence, R.id.select_category_list, R.string.demo_select_category);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(final MaterialShowcaseView materialShowcaseView, int i) {
                adapterDemo.addDemoTouchListener(materialShowcaseView);
            }
        });

        sequence.start();
    }

    /**
     * Start the result demo
     *
     * @param adapterDemo The adapterDemo of the activity
     */
    public static void startResultDemo(final AdapterDemo adapterDemo) {
        ShowcaseConfig config = new ShowcaseConfig();
        MaterialShowcaseSequence sequence = adapterDemo.getSequence();

        config.setDelay(10);
        config.setShape(new RectangleShape(0, 0));

        sequence.setConfig(config);
        sequence.singleUse(DEMO_RESULT);
        // adapterDemo.addSequenceItem(sequence, R.id.resultCard, R.string.demo_result_progression);
        adapterDemo.addSequenceItem(sequence, R.id.result_information, R.string.demo_result_about_list);
        adapterDemo.addSequenceItem(sequence, R.id.result_demo_fullscreen, R.string.demo_result_restart);
        // adapterDemo.addSequenceItem(sequence, R.id.resultDemoFullscreen, R.string.demo_finished);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(final MaterialShowcaseView materialShowcaseView, int i) {
                adapterDemo.addDemoTouchListener(materialShowcaseView);
            }
        });
        sequence.start();
    }


    /**
     * Restart the demo
     *
     * @param ctx The current context
     */
    public static void resetAllSequence(Context ctx) {
        MaterialShowcaseView.resetAll(ctx);
        demoProgress = 0;
    }
}
