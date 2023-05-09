package app.web.jkimtech.drpatientappointment.model.Common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class NonSwipeViewPager extends ViewPager {

    // Constructor method that takes only a context parameter
    public NonSwipeViewPager(@NonNull Context context) {
        super(context);
        setMyScroller();
    }

    // Constructor method that takes a context and an AttributeSet parameter
    public NonSwipeViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }

    // Override the default onInterceptTouchEvent method to always return false.
    // This will prevent the ViewPager from intercepting touch events and handling them itself.
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    // Override the default onTouchEvent method to always return false.
    // This will prevent the ViewPager from handling touch events and scrolling itself.
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    // Private method that sets a custom Scroller for the ViewPager.
    private void setMyScroller() {
        try {
            // Get the class object for the ViewPager class
            Class<?> viewPager = ViewPager.class;
            // Get the Scroller field from the ViewPager class
            Field scroller = viewPager.getDeclaredField("mScroller");
            // Make the Scroller field accessible
            scroller.setAccessible(true);
            // Create a new instance of the MyScroller class and set it as the ViewPager's Scroller
            scroller.set(this, new MyScroller(getContext()));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // Private inner class that extends the Scroller class and overrides the startScroll method.
    private class MyScroller extends Scroller {

        // Constructor method that takes a context parameter.
        public MyScroller(Context context) {
            // Call the parent Scroller constructor with a DecelerateInterpolator.
            super(context, new DecelerateInterpolator());
        }

        // Override the startScroll method to set a fixed duration of 350ms for all scrolls.
        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 350);
        }
    }
}
