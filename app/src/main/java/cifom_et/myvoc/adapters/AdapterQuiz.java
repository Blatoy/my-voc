package cifom_et.myvoc.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import cifom_et.myvoc.R;
import cifom_et.myvoc.activities.ActivityQuiz;
import cifom_et.myvoc.data.database.DatabaseHelper;
import cifom_et.myvoc.data.database.DatabaseManager;
import cifom_et.myvoc.logic.LogicQuiz;
import cifom_et.myvoc.utils.OnSwipeTouchListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public class AdapterQuiz {
    private Context ctx;
    // Views
    private View quizCard;
    private TextView quizCategory;
    private TextView quizProgression;
    private TextView quizCardQuestion;
    private TextView quizCardResponse;
    // Animation speed
    private int animationRotateDuration = 150;
    private int animationSlideDuration = 250;
    private int animationTextAlphaDuration = 10;
    private boolean playAnimation = true;
    private Point screenSize; // Used in animations

    /**
     * @param ctx -
     */
    public AdapterQuiz(Context ctx) {
        // Init variables
        this.ctx = ctx;
        this.screenSize = new Point();

        this.quizCard = ((Activity) ctx).findViewById(R.id.quiz_card);
        this.quizCardQuestion = (TextView) ((Activity) ctx).findViewById(R.id.quiz_question);
        this.quizCardResponse = (TextView) ((Activity) ctx).findViewById(R.id.quiz_answer);
        this.quizCategory = (TextView) ((Activity) ctx).findViewById(R.id.quiz_category_name);
        this.quizProgression = (TextView) ((Activity) ctx).findViewById(R.id.quiz_progression);

        // Add listener when the user slide / touch the quizCard
        this.addEventListeners(this);
    }

    /**
     * Set the top card content
     *
     * @param categoryName The name of the category
     * @param asked        Number of words already asked
     * @param total        NUmber of words total
     */
    public void setProgression(String categoryName, int asked, int total) {
        quizCategory.setText(categoryName);
        quizProgression.setText(String.format(ctx.getString(R.string.quiz_progression), asked, total));
    }

    /**
     * Set the text for the "question card"
     *
     * @param baseWord        The word from the main language
     * @param translationWord The word to learn
     * @param reverse         Reverse the questioning order
     */
    public void setCardText(String baseWord, String translationWord, boolean reverse) {
        quizCardQuestion.setText(reverse ? translationWord : baseWord);
        quizCardResponse.setText(reverse ? baseWord : translationWord);
    }

    /**
     * Play the rotate effect, and display the answer
     */
    public void playAnimationDisplayAnswer() {
        if (!playAnimation) {
            setAnswerVisibility(true);
            return;
        }

        quizCard.animate().setDuration(animationRotateDuration).scaleX(0f).withEndAction(new Runnable() {
            @Override
            public void run() {
                setAnswerVisibility(true);
                playAnimationTextAlpha();
                quizCard.animate().setDuration(animationRotateDuration).scaleX(1f).withLayer();
            }
        });
    }

    /**
     * Display the answer text with a smooth alpha effect
     */
    public void playAnimationTextAlpha() {
        if (!playAnimation) {
            return;
        }
        quizCardResponse.setAlpha(0f);
        quizCardResponse.animate().setDuration(animationTextAlphaDuration).alpha(1f);
    }

    /**
     * Display the "no words in this list" dialog
     */
    public void displayListEmptyDialog() {
        if (ActivityQuiz.isVisible) {
            new AlertDialog.Builder(ctx)
                    .setTitle(ctx.getString(R.string.error_list_empty_title))
                    .setMessage(ctx.getString(R.string.error_list_empty_content))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ((Activity) ctx).finish();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ((Activity) ctx).finish();
                        }
                    })
                    .show();
        }
    }

    /**
     * Update the loading circle visibility
     *
     * @param visible Set the visibility
     */
    public void setLoadingVisibility(boolean visible) {
        if (visible)
            ((Activity) ctx).findViewById(R.id.quiz_loading_list).setVisibility(View.VISIBLE);
        else
            ((Activity) ctx).findViewById(R.id.quiz_loading_list).setVisibility(View.GONE);
    }

    /**
     * Move the card out of screen, handle the answer and bring the card back with a new word
     *
     * @param isRight     True if the word was answer correctly
     * @param adapterQuiz The adapterQuiz for this activity
     */
    public void playAnimationDisplayNextCard(boolean isRight, final AdapterQuiz adapterQuiz) {
        if (!playAnimation) {
            // Prevent onTouch to occur too soon
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setAnswerVisibility(false);
                    LogicQuiz.askQuestion(ctx, adapterQuiz);
                }
            }, 1);
            return;
        }
        Display display = ((Activity) ctx).getWindowManager().getDefaultDisplay();
        // Get the screen size
        display.getSize(screenSize);

        // Default target is up
        int targetTranslation = -screenSize.y;

        // Set the border color for the card
        if (isRight) {
            quizCard.setBackgroundResource(R.drawable.cardview_border_right);
        } else {
            targetTranslation *= -1;
            quizCard.setBackgroundResource(R.drawable.cardview_border_wrong);
        }

        // Play the animation
        quizCard.animate().setDuration(animationSlideDuration).translationY(targetTranslation).withEndAction(new Runnable() {
            @Override
            public void run() {
                // Load the new word
                LogicQuiz.askQuestion(ctx, adapterQuiz);
                // Move back the card
                quizCard.animate().translationY(0).withLayer();
                // Reset the background
                quizCard.setBackground(ctx.getResources().getDrawable(R.color.white));
                // Hide the answer
                setAnswerVisibility(false);
            }
        });
    }

    /**
     * Update the visibility of the answer
     *
     * @param visible Set the visibility
     */
    public void setAnswerVisibility(boolean visible) {
        if (visible)
            quizCardResponse.setVisibility(View.VISIBLE);
        else
            quizCardResponse.setVisibility(View.INVISIBLE);
    }

    /**
     * Add the slide and touch events to handle the user answer
     *
     * @param adapterQuiz The quiz adapter for the activity
     */
    private void addEventListeners(final AdapterQuiz adapterQuiz) {
        this.quizCard.setOnTouchListener(new OnSwipeTouchListener(ctx) {
            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                // If the answer is visible, slide up the card and set it right
                if (quizCardResponse.getVisibility() == View.VISIBLE) {
                    LogicQuiz.handleUserResponse(true, adapterQuiz);
                }
            }

            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                // If the answer is visible, slide down the card and set it right
                if (quizCardResponse.getVisibility() == View.VISIBLE) {
                    LogicQuiz.handleUserResponse(false, adapterQuiz);
                }
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                // If the answer is not visible, rotate the card
                if (quizCardQuestion.getVisibility() == View.INVISIBLE) {
                    playAnimationDisplayAnswer();
                }
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                // If the answer is not visible, rotate the card
                if (quizCardResponse.getVisibility() == View.INVISIBLE) {
                    playAnimationDisplayAnswer();
                }
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                super.onTouch(v, event);
                // Touching can also be used to display the answer
                if (quizCardResponse.getVisibility() == View.INVISIBLE) {
                    playAnimationDisplayAnswer();
                }
                return true;
            }
        });
    }

    /**
     * Display or hide the tutorial (hand) icon
     *
     * @param visible Set the visibility
     */
    public void setDemoImageVisibility(boolean visible) {
        if (visible)
            ((Activity) ctx).findViewById(R.id.tutorial_image).setVisibility(View.VISIBLE);
        else
            ((Activity) ctx).findViewById(R.id.tutorial_image).setVisibility(View.INVISIBLE);
    }

    /**
     * Set the demo image to display (slide up/down or left/right)
     *
     * @param firstImage True for the left / right hand, false for the up / down one
     */
    public void setDemoImage(boolean firstImage) {
        if (firstImage)
            ((Activity) ctx).findViewById(R.id.tutorial_image).setBackgroundResource(R.mipmap.ic_get_answer);
        else
            ((Activity) ctx).findViewById(R.id.tutorial_image).setBackgroundResource(R.mipmap.ic_next_word);
    }

    /**
     * Add custom event listener for the demo, allow to close the demo by sliding the finger
     * (Slide left / right events)
     *
     * @param materialShowcaseView The materialShowcaseView for the current demo
     */
    public void addDemoDisplayAnswerListener(final MaterialShowcaseView materialShowcaseView) {
        materialShowcaseView.setOnTouchListener(new OnSwipeTouchListener(ctx) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                // Display the answer
                playAnimationDisplayAnswer();
                // Display the next step of the tutorial
                materialShowcaseView.onClick(materialShowcaseView);
                // Hide current image
                setDemoImageVisibility(false);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                // Same as above
                playAnimationDisplayAnswer();
                materialShowcaseView.onClick(materialShowcaseView);
                setDemoImageVisibility(false);
            }
        });
    }

    /**
     * Add custom event listener for the demo, allow to close the demo by sliding the finger
     * (Slide up / down events)
     *
     * @param materialShowcaseView The materialShowcaseView for the current demo
     * @param adapterQuiz          The adapterQuiz for the current activity
     */
    public void addDemoNextWordListener(final MaterialShowcaseView materialShowcaseView, final AdapterQuiz adapterQuiz) {
        // Handle swipe event on demo
        materialShowcaseView.setOnTouchListener(new OnSwipeTouchListener(ctx) {
            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                LogicQuiz.handleUserResponse(true, adapterQuiz);
                materialShowcaseView.onClick(materialShowcaseView);
            }

            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                LogicQuiz.handleUserResponse(false, adapterQuiz);
                materialShowcaseView.onClick(materialShowcaseView);
            }
        });
    }

    /**
     * Return the DatabaseHelper for the current activity.
     *
     * @return DatabaseHelper
     */
    public DatabaseHelper getDatabaseHelper() {
        return new DatabaseHelper(ctx);
    }

    /**
     * Restart the current category, by clearing all the progress
     * Display the "are you sure?" dialog
     */
    public void restartCategory() {
        final AdapterQuiz adapterQuiz = this;
        // Display the adapter
        new AlertDialog.Builder(ctx)
                .setTitle(R.string.quiz_restart)
                .setMessage(R.string.setting_progress_loss)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Hide current answer
                        adapterQuiz.setAnswerVisibility(false);
                        // Reset learning progress
                        (new DatabaseManager(ctx)).resetWordFromCategory(LogicQuiz.currentCategory.getId());
                        // Restart the round
                        LogicQuiz.currentCategory.resetAll();
                        LogicQuiz.startNewRound();
                        LogicQuiz.askQuestion(ctx, adapterQuiz);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
