package treehou.se.habit.ui.settings;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import treehou.se.habit.NavigationUtil;
import treehou.se.habit.R;
import treehou.se.habit.ui.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class LicenseTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup(){
    }

    @Test
    public void testOpenAboutPage() {
        NavigationUtil.INSTANCE.navigateToSettings();
        onView(withText(R.string.open_source_libraries)).perform(ViewActions.click());
        onView(withText(R.string.library_AboutLibraries_libraryName)).check(matches(isDisplayed()));
    }
}