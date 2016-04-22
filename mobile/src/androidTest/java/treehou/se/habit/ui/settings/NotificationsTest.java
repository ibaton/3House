package treehou.se.habit.ui.settings;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import treehou.se.habit.MainActivity;
import treehou.se.habit.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class NotificationsTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testOpenSettings() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(allOf(withId(R.id.lbl_name), withText(R.string.settings))).perform(ViewActions.click());
        onView(withText(R.string.settings_notification)).perform(ViewActions.click());
        onView(withText(R.string.notification_to_speech_detail)).perform(ViewActions.click());
    }
}