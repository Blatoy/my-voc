package cifom_et.myvoc.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cifom_et.myvoc.R;
import cifom_et.myvoc.adapters.AdapterDemo;
import cifom_et.myvoc.adapters.AdapterSelect;
import cifom_et.myvoc.data.settings.SettingsHelper;
import cifom_et.myvoc.data.settings.SettingsStructure;
import cifom_et.myvoc.logic.LogicDemo;
import cifom_et.myvoc.logic.LogicSelect;
import cifom_et.myvoc.logic.LogicSettings;
import cifom_et.myvoc.utils.Utils;

public class ActivitySelect extends AppCompatActivity {
    public static final String INTENT_RESET = "reset";
    private AdapterSelect adapterSelect;

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
        // Display the "refresh result" button
        menu.findItem(R.id.action_refresh_result).setVisible(true);
        return true;
    }

    /**
     * Handle menu click
     *
     * @param item -
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Do the right action when a button is pressed on the menu
        return Utils.handleToolbarAction(this, item, null, null, adapterSelect);
    }

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState -
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        // Instantiate the adapters
        adapterSelect = new AdapterSelect(this);
        adapterSelect.addChangeListener(adapterSelect);
        AdapterDemo adapterDemo = new AdapterDemo(this);

        // Check if another activity requested activity's reset
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getBoolean(INTENT_RESET, false)) {
                // Refresh categories
                LogicSelect.updateCategoriesAndLanguages(this, adapterSelect);
            }
        }

        // Add the toolbar
        Utils.addToolbarToActivity(this);
        // Hide "back" icon -> we're on the main activity
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Reset settings on first launch
        if (!SettingsHelper.getBoolean(this, SettingsStructure.SET_UP_DONE)) {
            LogicSettings.restoreDefaultSettings(this);
            // Set true, will prevent to call again this method
            SettingsHelper.setBoolean(this, SettingsStructure.SET_UP_DONE, true);
        }

        // Update categories and languages from API / DB
        LogicSelect.updateCategoriesAndLanguages(this, adapterSelect);
        // Start the demo (will only be displayed once)
        LogicDemo.startSelectDemo(adapterDemo);
    }

    /**
     * Called when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Check if the Settings called for a refresh
        if (ActivitySettings.requestSelectRefresh) {
            ActivitySettings.requestSelectRefresh = false;
            LogicSelect.updateCategoriesAndLanguages(this, adapterSelect);
        }
    }
}
