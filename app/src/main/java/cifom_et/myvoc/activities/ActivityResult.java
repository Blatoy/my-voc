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
import cifom_et.myvoc.adapters.AdapterResult;
import cifom_et.myvoc.logic.LogicDemo;
import cifom_et.myvoc.logic.LogicQuiz;
import cifom_et.myvoc.logic.LogicResult;
import cifom_et.myvoc.utils.Utils;

public class ActivityResult extends AppCompatActivity {
    private AdapterResult adapterResult;

    /**
     * Add items to the menu
     *
     * @param menu -
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add items to the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_settings, menu);
        // Enable "Restart list" button
        menu.findItem(R.id.action_bar_button_restart_list).setVisible(true);
        return true;
    }

    /**
     * Handle selected items
     *
     * @param item -
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Do the right action when a button is pressed on the menu
        return Utils.handleToolbarAction(this, item, null, adapterResult, null);
    }

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState -
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Instantiate the adapters
        adapterResult = new AdapterResult(this);
        AdapterDemo adapterDemo = new AdapterDemo(this);

        // Add the toolbar
        Utils.addToolbarToActivity(this);

        // Update the UI
        LogicResult.updateQuizStatus(adapterResult);

        // Start the demo (will only be displayed once)
        LogicDemo.startResultDemo(adapterDemo);
    }

    /**
     * Called on click from button "Continue"
     *
     * @param view -
     */
    public void startNextRound(View view) {
        // Create the new round
        LogicQuiz.startNewRound();
        // Start the Quiz Activity and close this one
        startActivity(new Intent(this, ActivityQuiz.class));
        finish();
    }
}
