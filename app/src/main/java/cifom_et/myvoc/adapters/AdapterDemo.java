package cifom_et.myvoc.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import cifom_et.myvoc.utils.OnSwipeTouchListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */

public class AdapterDemo {
    private Context ctx;

    /**
     * @param ctx -
     */
    public AdapterDemo(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * Add an item to the demo sequence (= one "blue" screen with text)
     *
     * @param sequence         -
     * @param resourceId       Target resource
     * @param stringResourceId String to display
     */
    public void addSequenceItem(MaterialShowcaseSequence sequence, int resourceId, int stringResourceId) {
        sequence.addSequenceItem(((Activity) ctx).findViewById(resourceId), ctx.getResources().getString(stringResourceId), "");
    }

    /**
     * Get the MaterialShowcaseSequence for the current activity
     *
     * @return
     */
    public MaterialShowcaseSequence getSequence() {
        return new MaterialShowcaseSequence((Activity) ctx);
    }

    /**
     * Dismiss "blue" screens when the user touch the screen
     *
     * @param materialShowcaseView The material showcase for the current demo
     */
    public void addDemoTouchListener(final MaterialShowcaseView materialShowcaseView) {
        materialShowcaseView.setOnTouchListener(new OnSwipeTouchListener(ctx) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                materialShowcaseView.onClick(materialShowcaseView);
                super.onTouch(v, event);
                return true;
            }
        });
    }
}
