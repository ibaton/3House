package treehou.se.habit.ui.settings;

import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import treehou.se.habit.DaggerActivityTestRule;
import treehou.se.habit.MainActivity;
import treehou.se.habit.NavigationUtil;
import treehou.se.habit.R;
import treehou.se.habit.TestUtil;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static treehou.se.habit.ViewActions.SliderActions.setProgress;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class WidgetSettingsTest {

    @Rule
    public DaggerActivityTestRule<MainActivity> activityRule = TestUtil.TestRule();

    @Before
    public void moveToWidget(){
        NavigationUtil.navigateToSettings();
        onView(withText(R.string.settings_widgets)).perform(ViewActions.click());
    }

    @Test
    public void testImageClearBackgroundColor() {
        onView(withId(R.id.lou_icon_backgrounds)).check(matches(isDisplayed()));
        onView(withId(R.id.cbx_enable_image_background)).perform(scrollTo(), ViewActions.click());
        onView(withId(R.id.lou_icon_backgrounds)).check(matches(CoreMatchers.not(isDisplayed())));
        onView(withId(R.id.cbx_enable_image_background)).perform(scrollTo(), ViewActions.click());
    }

    @Test
    public void testImageColor() {
        onView(withId(R.id.img_widget_icon1)).perform(scrollTo(), ViewActions.click());
        onView(withId(R.id.img_widget_icon2)).perform(scrollTo(), ViewActions.click());
        onView(withId(R.id.img_widget_icon3)).perform(scrollTo(), ViewActions.click());
        onView(withId(R.id.img_widget_icon4)).perform(scrollTo(), ViewActions.click());
        onView(withId(R.id.img_widget_icon5)).perform(scrollTo(), ViewActions.click());
        onView(withId(R.id.img_widget_icon6)).perform(scrollTo(), ViewActions.click());
    }

    @Test
    public void testImageSize() {
        onView(withId(R.id.bar_image_size)).perform(scrollTo(), setProgress(12));
        onView(withId(R.id.bar_image_size)).perform(setProgress(100));
        onView(withId(R.id.bar_image_size)).perform(setProgress(0));
        onView(withId(R.id.bar_image_size)).perform(setProgress(50));
    }

    @Test
    public void testTextSize() {
        onView(withId(R.id.bar_text_size)).perform(scrollTo(), setProgress(12));
        onView(withId(R.id.bar_text_size)).perform(setProgress(100));
        onView(withId(R.id.bar_text_size)).perform(setProgress(0));
        onView(withId(R.id.bar_text_size)).perform(setProgress(50));
    }

    @Test
    public void testCompressButton() {
        onView(withId(R.id.swt_compressed_button)).perform(scrollTo(), ViewActions.click());
    }

    @Test
    public void testCompressSlider() {
        onView(withId(R.id.swt_compressed_slider)).perform(scrollTo(), ViewActions.click());
    }
}