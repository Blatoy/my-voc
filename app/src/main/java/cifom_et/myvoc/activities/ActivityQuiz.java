package cifom_et.myvoc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import cifom_et.myvoc.R;
import cifom_et.myvoc.adapters.AdapterDemo;
import cifom_et.myvoc.adapters.AdapterQuiz;
import cifom_et.myvoc.logic.LogicDemo;
import cifom_et.myvoc.logic.LogicQuiz;
import cifom_et.myvoc.logic.LogicSelect;
import cifom_et.myvoc.utils.Utils;


public class ActivityQuiz extends AppCompatActivity {
    public static boolean isVisible; // Track if the activity is visible, prevent alertDialog errors
    private AdapterQuiz adapterQuiz;

    /**
     * Add items to the menu
     *
     * @param menu -
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add buttons to the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_settings, menu);
        // Enable "Restart list" button
        menu.findItem(R.id.action_bar_button_restart_list).setVisible(true);
        return true;
    }

    /**
     * Handle menu items's click
     *
     * @param item -
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Do the right action when a button is pressed on the menu
        return Utils.handleToolbarAction(this, item, adapterQuiz, null, null);
    }

    /**
     * Called when the activity is started
     *
     * @param savedInstanceState -
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Instantiate adapters
        adapterQuiz = new AdapterQuiz(this);
        AdapterDemo adapterDemo = new AdapterDemo(this);

        // Add the activity's toolbar
        Utils.addToolbarToActivity(this);
        // Save activity's visibility
        ActivityQuiz.isVisible = true;

        // Start a new quiz if specified by the intent
        if (getIntent().hasExtra(LogicSelect.INTENT_EXTRA_CATEGORY_ID)) {
            LogicQuiz.startQuestioner(this, adapterQuiz, getIntent().getExtras().getInt(LogicSelect.INTENT_EXTRA_CATEGORY_ID, -1));
        } else {
            LogicQuiz.askQuestion(this, adapterQuiz);
        }

        // Start the demo (will only be displayed once)
        LogicDemo.startQuizDemo(adapterDemo, adapterQuiz);
    }

    /**
     * Called when the activity is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Save activity's visibility
        ActivityQuiz.isVisible = false;
    }

    /**
     * Called when the activity is stopped
     */
    @Override
    protected void onStop() {
        super.onStop();
        ActivityQuiz.isVisible = false;
    }

    /**
     * Called when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        adapterQuiz = new AdapterQuiz(this);
        ActivityQuiz.isVisible = true;
    }

    /**
     * Start the result activity
     *
     * @param view -
     */
    public void startResultActivity(View view) {
        startActivity(new Intent(this, ActivityResult.class));
        finish();
    }
}
