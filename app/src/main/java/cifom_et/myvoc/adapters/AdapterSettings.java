package cifom_et.myvoc.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import cifom_et.myvoc.R;
import cifom_et.myvoc.activities.ActivitySelect;
import cifom_et.myvoc.activities.ActivitySettings;
import cifom_et.myvoc.data.api.logic.ApiManager;
import cifom_et.myvoc.data.database.DatabaseManager;
import cifom_et.myvoc.data.settings.SettingsHelper;
import cifom_et.myvoc.data.settings.SettingsStructure;
import cifom_et.myvoc.logic.LogicSettings;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */

public class AdapterSettings {
    private Context ctx;

    /**
     * @param ctx -
     */
    public AdapterSettings(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * Kill all activity and start selectActivity
     */
    public void restartApplication() {
        // Kill all activities and start selectActivity
        Intent intent = new Intent(ctx.getApplicationContext(), ActivitySelect.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
    }

    /**
     * Display the "change api" dialog
     */
    public void displayChangeApiDialog() {
        final EditText userResult = new EditText(ctx);

        // Parameter the textView
        userResult.setText(SettingsHelper.getString(ctx, SettingsStructure.API_BASE_PATH));
        userResult.setSingleLine();

        // Show the dialog
        new AlertDialog.Builder(ctx)
                .setTitle(ctx.getString(R.string.setting_api_server))
                .setMessage(ctx.getString(R.string.setting_api_server_desc))
                .setView(userResult, 80, 10, 100, 10)
                .setPositiveButton(ctx.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Set the value
                        SettingsHelper.setString(ctx, SettingsStructure.API_BASE_PATH, userResult.getText().toString());
                        reloadUi();
                    }
                })
                .setNegativeButton(ctx.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * Display the "change quiz way" dialog
     */
    public void displayQuizOrderDialog() {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1);

        // Add the 3 possibles choices (ask, base -> trad, trad -> base)
        arrayAdapter.add(ctx.getString(R.string.setting_quiz_order_ask));
        arrayAdapter.add(ctx.getString(R.string.setting_quiz_order_l1_l2));
        arrayAdapter.add(ctx.getString(R.string.setting_quiz_order_l2_l1));

        // Display the dialog
        new AlertDialog.Builder(ctx)
                .setTitle(ctx.getString(R.string.select_list_order))
                .setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle item's list click
                        SettingsHelper.setInt(ctx, SettingsStructure.DEFAULT_QUIZ_ORDER, which);
                        reloadUi();
                    }
                })
                .setNegativeButton(ctx.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * Display the "confirm clear cache" dialog
     */
    public void displayClearCacheDialog() {
        // Display confirmation dialog
        new AlertDialog.Builder(ctx)
                .setTitle(ctx.getString(R.string.setting_clear_cache))
                .setMessage(ctx.getString(R.string.setting_progress_loss))
                .setPositiveButton(ctx.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Reset the database and restart the app
                        (new DatabaseManager(ctx.getApplicationContext())).resetDatabase();
                        restartApplication();
                    }
                })
                .setNegativeButton(ctx.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }


    /**
     * Display the "confirm reset?" dialog
     */
    public void displayResetAppDialog() {
        // Display confirmation dialog
        new AlertDialog.Builder(ctx)
                .setTitle(ctx.getString(R.string.setting_reset_title))
                .setMessage(ctx.getString(R.string.setting_progress_loss))
                .setPositiveButton(ctx.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Reset default values and tutorial
                        LogicSettings.restoreDefaultSettings(ctx.getApplicationContext());
                        LogicSettings.resetTutorial(ctx.getApplicationContext());
                        // Reset the database
                        (new DatabaseManager(ctx.getApplicationContext())).resetDatabase();
                        // Restart the app
                        restartApplication();
                    }
                })
                .setNegativeButton(ctx.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * Open a web browser in the page
     */
    public void openEditor() {
        // Start the webbrowser with the API_BASE_PATH
        Uri uriUrl = Uri.parse(SettingsHelper.getString(ctx, SettingsStructure.API_BASE_PATH));
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        ctx.startActivity(launchBrowser);
    }

    /**
     * Reload every element of the UI
     */
    public void reloadUi() {
        Activity activityCtx = (Activity) ctx;
        TextView quizOrder = (TextView) activityCtx.findViewById(R.id.setting_value_quiz_order);
        TextView languageSearch1 = (TextView) activityCtx.findViewById(R.id.setting_value_language1);
        TextView languageSearch2 = (TextView) activityCtx.findViewById(R.id.setting_value_language2);
        TextView apiServer = (TextView) activityCtx.findViewById(R.id.setting_value_api_server);
        Switch syncOnWifi = (Switch) activityCtx.findViewById(R.id.setting_value_wifi_switch);

        // Update values
        apiServer.setText(SettingsHelper.getString(ctx, SettingsStructure.API_BASE_PATH));
        syncOnWifi.setChecked(SettingsHelper.getBoolean(ctx, SettingsStructure.WIFI_SYNC));

        // Get language ID
        int idLanguage1 = SettingsHelper.getInt(ctx, SettingsStructure.SEARCH_BASE_LANGUAGE_ID);
        int idLanguage2 = SettingsHelper.getInt(ctx, SettingsStructure.SEARCH_TRANSLATION_LANGUAGE_ID);

        // Update textview value
        setLanguageValue(idLanguage1, languageSearch1);
        setLanguageValue(idLanguage2, languageSearch2);

        // Update the quiz order
        switch (SettingsHelper.getInt(ctx, SettingsStructure.DEFAULT_QUIZ_ORDER)) {
            case SettingsStructure.QUIZ_ORDER_ASK:
                quizOrder.setText(R.string.setting_quiz_order_ask);
                break;
            case SettingsStructure.QUIZ_ORDER_BASE_TRANSLATION:
                quizOrder.setText(R.string.setting_quiz_order_l1_l2);
                break;
            case SettingsStructure.QUIZ_ORDER_TRANSLATION_BASE:
                quizOrder.setText(R.string.setting_quiz_order_l2_l1);
                break;

        }
    }

    /**
     * Display the dialog for the 2 "default search" buttons
     *
     * @param settingId The id of the setting to change
     */
    public void displayDefaultSearchDialog(final String settingId) {
        // Create an adapter
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1);
        // Add default search
        arrayAdapter.add(ctx.getResources().getString(R.string.select_spinner_all));
        // Add other languages
        for (int i = 0; i < ApiManager.languages.size(); i++) {
            arrayAdapter.add(ApiManager.languages.get(i).getName());
        }

        // Display the dialog
        new AlertDialog.Builder(ctx)
                .setTitle(ctx.getString(R.string.select_list_order))
                .setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivitySettings.requestSelectRefresh = true;
                        // Update the settings
                        SettingsHelper.setInt(ctx, settingId, which - 1);
                        reloadUi();
                    }
                })
                .setNegativeButton(ctx.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * Switch the "sync on wifi only" switch
     */
    public void updateWifiSwitch() {
        // Handle the wifi state on card / switch click
        Switch aSwitch = (Switch) ((Activity) ctx).findViewById(R.id.setting_value_wifi_switch);
        aSwitch.setChecked(!SettingsHelper.getBoolean(ctx, SettingsStructure.WIFI_SYNC));
        SettingsHelper.setBoolean(ctx, SettingsStructure.WIFI_SYNC, aSwitch.isChecked());
        reloadUi();
    }

    /**
     * Update the spinner
     *
     * @param idLanguage The id of the language to display
     * @param textView   The id of the TextView to use
     */
    private void setLanguageValue(int idLanguage, TextView textView) {
        // -1 = no filter
        if (idLanguage == -1) {
            textView.setText(R.string.select_spinner_all);
        } else {
            // Prevent out of bound exception
            if (idLanguage < ApiManager.languages.size()) {
                // Set current language
                textView.setText(ApiManager.languages.get(idLanguage).getName());
            }
        }
    }
}
