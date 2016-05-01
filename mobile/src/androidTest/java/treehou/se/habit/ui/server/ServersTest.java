package treehou.se.habit.ui.server;

import android.support.test.espresso.action.ViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import treehou.se.habit.DaggerActivityTestRule;
import treehou.se.habit.MainActivity;
import treehou.se.habit.NavigationUtil;
import treehou.se.habit.R;
import treehou.se.habit.TestUtil;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.AnyOf.anyOf;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ServersTest {

    @Rule
    public DaggerActivityTestRule<MainActivity> activityRule = TestUtil.TestRule();

    @Before
    public void setup(){}

    @Test
    public void testCreateRemoveServer() {
        String testServerName = "Test Server " + new Random().nextInt(100);
        NavigationUtil.navigateToServer();
        onView(anyOf(allOf(withId(R.id.fab_add), isDisplayed()), allOf(withText(R.string.new_server), isDisplayed()))).perform(ViewActions.click());
        onView(withId(R.id.txt_server_name)).perform(ViewActions.typeText(testServerName));
        closeSoftKeyboard();
        pressBack();

        onView(withText(testServerName)).perform(ViewActions.longClick());
        onView(withText(R.string.delete)).perform(ViewActions.click());
        onView(withText(R.string.ok)).perform(ViewActions.click());
        onView(withId(R.id.txt_server_name)).check(doesNotExist());
    }
}