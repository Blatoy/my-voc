package cifom_et.myvoc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import cifom_et.myvoc.R;
import cifom_et.myvoc.activities.ActivitySettings;
import cifom_et.myvoc.adapters.AdapterQuiz;
import cifom_et.myvoc.adapters.AdapterResult;
import cifom_et.myvoc.adapters.AdapterSelect;
import cifom_et.myvoc.logic.LogicSelect;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public abstract class Utils {
    /**
     * Add the action bar to the current activity
     *
     * @param ctx -
     */
    public static void addToolbarToActivity(Context ctx) {
        // Add the action (top) bar to the activity
        Toolbar toolbar = (Toolbar) ((Activity) ctx).findViewById(R.id.action_bar);
        ((AppCompatActivity) ctx).setSupportActionBar(toolbar);

        // Set a white arrow for back button
        // Source: http://stackoverflow.com/questions/26788464/how-to-change-color-of-the-back-arrow-in-the-new-material-theme
        final Drawable upArrow = ctx.getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ctx.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) ctx).getSupportActionBar().setHomeAsUpIndicator(upArrow);
        ((AppCompatActivity) ctx).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Add items to the action bar
     *
     * @param ctx           Context
     * @param item          Item
     * @param adapterQuiz   The adapterQuiz if present
     * @param adapterResult The adapterQuiz if present
     * @return boolean
     */
    public static boolean handleToolbarAction(Context ctx, MenuItem item, @Nullable AdapterQuiz adapterQuiz, @Nullable AdapterResult adapterResult, @Nullable AdapterSelect adapterSelect) {
        switch (item.getItemId()) {
            case R.id.action_open_settings:
                // Start the settings without closing the quiz
                ctx.startActivity(new Intent(ctx, ActivitySettings.class));
                break;
            case R.id.action_bar_button_restart_list:
                // Ask and restart the quiz
                if (adapterQuiz != null) {
                    adapterQuiz.restartCategory();
                } else if (adapterResult != null) {
                    adapterResult.restartCategory();
                }
                break;
            case R.id.action_refresh_result:
                LogicSelect.updateCategoriesAndLanguages(ctx, adapterSelect);
                return true;
            default:
                return false;
        }
        return true;
    }
}
