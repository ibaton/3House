package treehou.se.habit.ViewActions;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import android.widget.SeekBar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SliderActions {

    private SliderActions() {}

    public static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                setSeekBarProgress(seekBar, progress, true);
            }
            @Override
            public String getDescription() {
                return "Set a progress on a SeekBar";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }

    private static void setSeekBarProgress(SeekBar seekBar, int newProgress, boolean fromUser) {

        Method privateSetProgressMethod = null;
        try {
            privateSetProgressMethod = SeekBar.class.getDeclaredMethod("setProgress", Integer.TYPE, Boolean.TYPE);
            privateSetProgressMethod.setAccessible(true);
            privateSetProgressMethod.invoke(seekBar, newProgress, fromUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Matcher<View> withProgress(final int expectedProgress) {

        return new BoundedMatcher<View, SeekBar>(SeekBar.class) {
            @Override
            protected boolean matchesSafely(SeekBar seekBar) {
                return seekBar.getProgress() == expectedProgress;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("expected: ");
                description.appendText(""+expectedProgress);
            }
        };
    }
}
