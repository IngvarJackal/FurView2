package ru.furry.furview2.UI;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class OnSwipeAncClickTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    protected Context context;

    private int SWIPE_DISTANCE_THRESHOLD = 250;
    private int SWIPE_VELOCITY_THRESHOLD = 3500;

    public OnSwipeAncClickTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
        this.context = context;
    }

    public abstract void onSwipeLeft();

    public abstract void onSwipeRight();

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {;
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            } else {
                return false;
            }
        }

    }
}
