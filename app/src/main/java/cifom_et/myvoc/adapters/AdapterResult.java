package cifom_et.myvoc.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import cifom_et.myvoc.R;
import cifom_et.myvoc.activities.ActivityQuiz;
import cifom_et.myvoc.data.database.DatabaseManager;
import cifom_et.myvoc.logic.LogicQuiz;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public class AdapterResult {
    private Context ctx;
    private TextView textCategoryName;
    private TextView textProgression;
    private Button buttonContinue;
    private TableLayout tableWordList;

    /**
     * @param ctx -
     */
    public AdapterResult(Context ctx) {
        this.ctx = ctx;
        textCategoryName = ((TextView) ((Activity) ctx).findViewById(R.id.result_category_name));
        textProgression = ((TextView) ((Activity) ctx).findViewById(R.id.result_progression));
        buttonContinue = ((Button) ((Activity) ctx).findViewById(R.id.result_button_continue));
        tableWordList = (TableLayout) ((Activity) ctx).findViewById(R.id.result_word_list);
    }

    /**
     * Update the display of the progression
     *
     * @param categoryName The name of the category
     * @param asked        The number of word asked
     * @param total        The number of word total
     */
    public void setProgression(String categoryName, int asked, int total) {
        textCategoryName.setText(categoryName);
        textProgression.setText(String.format(ctx.getString(R.string.quiz_progression), asked, total));
    }

    /**
     * Add the words into the result list
     *
     * @param base        The base word
     * @param translation The translation word
     */
    public void addWordToResultList(String base, String translation) {
        // Create elements
        TextView textView = new TextView(ctx);

        // Parameter the textView
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ctx.getResources().getDimensionPixelSize(R.dimen.select_card_text_size));
        textView.setPadding(ctx.getResources().getDimensionPixelSize(R.dimen.card_margin),
                20, 20, ctx.getResources().getDimensionPixelSize(R.dimen.card_margin));

        // Set the text
        textView.setText(String.format(ctx.getString(R.string.result_word_separation), base, translation));

        // Add it to the tableLayout
        tableWordList.addView(textView);
    }

    /**
     * Change the action of the onClick event and the text for the "continue" button
     */
    public void displayStopButton() {
        buttonContinue.setText(R.string.result_quit_quiz);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Restart the select activity
                ((Activity) ctx).finish();
            }
        });
    }

    /**
     * Add an header into the word list
     *
     * @param numberMissed The number of time the word is missed
     * @param percentage   The percentage of the total
     */
    public void addCategoryHeaderToResultList(int numberMissed, double percentage) {
        TextView textView = new TextView(ctx);

        // Set size, padding and bold
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ctx.getResources().getDimensionPixelSize(R.dimen.select_card_text_size));
        textView.setPadding(ctx.getResources().getDimensionPixelSize(R.dimen.card_margin), 20, 20, ctx.getResources().getDimensionPixelSize(R.dimen.card_margin));

        // Display the correct header
        if (numberMissed == 0) {
            textView.setTextColor(ctx.getResources().getColor(R.color.greenButton));
            if (percentage != -1) {
                // Final progress
                textView.setText(String.format(ctx.getString(R.string.result_never_missed_final), (int) percentage));
            } else {
                // Round progress
                textView.setText(ctx.getString(R.string.result_never_missed));
            }
        } else {
            textView.setTextColor(ctx.getResources().getColor(R.color.redButton));
            if (percentage != -1) {
                // Final progress
                textView.setText(String.format(ctx.getString(R.string.result_missed_amount_final), numberMissed, (int) percentage));
            } else {
                // Round progress
                textView.setText(String.format(ctx.getString(R.string.result_missed_amount), numberMissed));
            }
        }

        tableWordList.addView(textView);
    }

    /**
     * Display the dialog "Restart current category?"
     */
    public void restartCategory() {
        // Display confirmation dialog
        new AlertDialog.Builder(ctx)
                .setTitle(R.string.quiz_restart)
                .setMessage(R.string.setting_progress_loss)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Reset database
                        (new DatabaseManager(ctx)).resetWordFromCategory(LogicQuiz.currentCategory.getId());
                        // Restart the current category
                        LogicQuiz.currentCategory.resetAll();
                        LogicQuiz.startNewRound();
                        // Restart the activity
                        ctx.startActivity(new Intent(ctx, ActivityQuiz.class));
                        ((Activity) ctx).finish();
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
