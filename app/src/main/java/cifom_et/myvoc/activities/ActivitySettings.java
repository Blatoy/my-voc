package cifom_et.myvoc.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cifom_et.myvoc.R;
import cifom_et.myvoc.adapters.AdapterSettings;
import cifom_et.myvoc.data.settings.SettingsStructure;
import cifom_et.myvoc.logic.LogicSettings;
import cifom_et.myvoc.utils.Utils;

public class ActivitySettings extends AppCompatActivity {
    public static boolean requestSelectRefresh;
    private AdapterSettings adapterSettings;

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState -
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Add the toolbar
        Utils.addToolbarToActivity(this);

        // Instantiate adapters
        adapterSettings = new AdapterSettings(this);

        // Update the UI
        adapterSettings.reloadUi();
    }

    /**
     * Reset the tutorial, called on click
     *
     * @param view -
     */
    public void resetTutorial(View view) {
        LogicSettings.resetTutorial(this);
        adapterSettings.restartApplication();
    }


    /**
     * Open the "change api server" dialog, called on click
     *
     * @param view -
     */
    public void changeApiServer(View view) {
        adapterSettings.displayChangeApiDialog();
    }

    /**
     * Open the "change order dialog" dialog, called on click
     *
     * @param view -
     */
    public void updateQuizOrder(View view) {
        adapterSettings.displayQuizOrderDialog();
    }

    /**
     * Open the "change search lang 1" dialog, called on click
     *
     * @param view -
     */
    public void setLanguage1(View view) {
        adapterSettings.displayDefaultSearchDialog(SettingsStructure.SEARCH_BASE_LANGUAGE_ID);
    }

    /**
     * Open the "change search lang 2" dialog, called on click
     *
     * @param view -
     */
    public void setLanguage2(View view) {
        adapterSettings.displayDefaultSearchDialog(SettingsStructure.SEARCH_TRANSLATION_LANGUAGE_ID);
    }

    /**
     * Toggle the "sync on wifi only" switch, called on click
     *
     * @param view -
     */
    public void setWifiSync(View view) {
        adapterSettings.updateWifiSwitch();
    }

    /**
     * Display the cache confirmation dialog
     *
     * @param view -
     */
    public void clearCache(View view) {
        adapterSettings.displayClearCacheDialog();
    }

    /**
     * Display the "reset all settings" confirmation dialog
     *
     * @param view -
     */
    public void resetAllSettings(View view) {
        adapterSettings.displayResetAppDialog();
    }

    /**
     * Open the editor webpage
     *
     * @param view -
     */
    public void openEditor(View view) {
        adapterSettings.openEditor();
    }
}
