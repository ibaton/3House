package treehou.se.habit.ui.settings;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.filters.SmallTest;

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
import treehou.se.habit.util.Settings;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class GeneralTest {

    @Rule
    public DaggerActivityTestRule<MainActivity> activityRule = TestUtil.TestRule();

    @Before
    public void setup(){
        Settings.instance(activityRule.getActivity()).getAutoloadSitemapRx().set(false);
    }

    @Test
    public void testOpenSettings() {
        NavigationUtil.navigateToSettings();
        onView(withText(R.string.settings_general)).perform(ViewActions.click());
        ViewInteraction cbxLoadLast = onView(withText(R.string.open_last_sitemap_on_upstart));
        cbxLoadLast.check(ViewAssertions.matches(CoreMatchers.not(isChecked())));
        cbxLoadLast.perform(ViewActions.click());
        cbxLoadLast.check(ViewAssertions.matches(isChecked()));
        cbxLoadLast.perform(ViewActions.click());
        cbxLoadLast.check(ViewAssertions.matches(CoreMatchers.not(isChecked())));
    }
}