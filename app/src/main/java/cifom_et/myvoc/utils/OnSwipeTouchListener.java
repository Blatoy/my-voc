package cifom_et.myvoc.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Source for this file: http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
 */

public class OnSwipeTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;

    /**
     * @param ctx -
     */
    public OnSwipeTouchListener(Context ctx) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    /**
     * @param v     The touched view
     * @param event The motion event
     * @return boolean
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * Called on swipe right
     */
    public void onSwipeRight() {
    }

    /**
     * Called on swipe left
     */
    public void onSwipeLeft() {
    }

    /**
     * Called on swipe top
     */
    public void onSwipeTop() {
    }

    /**
     * Called on swipe bottom
     */
    public void onSwipeBottom() {
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        /**
         * @param e MotionEvent
         * @return boolean
         */
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /**
         * @param e1        MotionEvent
         * @param e2        MotionEvent
         * @param velocityX float
         * @param velocityY float
         * @return boolean
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}