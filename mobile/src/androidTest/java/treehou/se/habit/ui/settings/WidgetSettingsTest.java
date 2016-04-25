package treehou.se.habit.ui.settings;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.SwitchCompat;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import butterknife.Bind;
import treehou.se.habit.DatabaseUtil;
import treehou.se.habit.MainActivity;
import treehou.se.habit.NavigationUtil;
import treehou.se.habit.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class WidgetSettingsTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void moveToWidget(){
        DatabaseUtil.init();
        NavigationUtil.navigateToSettings();
        onView(withText(R.string.settings_widgets)).perform(ViewActions.click());
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
    public void testCompressButton() {
        onView(withId(R.id.swt_compressed_button)).perform(scrollTo(), ViewActions.click());
    }

    @Test
    public void testCompressSlider() {
        onView(withId(R.id.swt_compressed_slider)).perform(scrollTo(), ViewActions.click());
    }
}