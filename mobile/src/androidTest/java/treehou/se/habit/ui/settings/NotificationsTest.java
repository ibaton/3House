package treehou.se.habit.ui.settings;

import android.support.test.espresso.action.ViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

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
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class NotificationsTest {

    @Rule
    public DaggerActivityTestRule<MainActivity> activityRule = TestUtil.TestRule();

    @Before
    public void setup(){
    }

    @Test
    public void testOpenSettings() {
        NavigationUtil.navigateToSettings();
        onView(withText(R.string.settings_notification)).perform(ViewActions.click());
        onView(withText(R.string.notification_to_speech_detail)).perform(ViewActions.click());
    }
}