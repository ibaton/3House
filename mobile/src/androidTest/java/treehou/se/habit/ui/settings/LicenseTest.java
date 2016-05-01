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
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class LicenseTest {

    @Rule
    public DaggerActivityTestRule<MainActivity> activityRule = TestUtil.TestRule();

    @Before
    public void setup(){
    }

    @Test
    public void testOpenAboutPage() {
        NavigationUtil.navigateToSettings();
        onView(withText(R.string.open_source_libraries)).perform(ViewActions.click());
        onView(withText(R.string.library_AboutLibraries_libraryName)).check(matches(isDisplayed()));
    }
}