package treehou.se.habit.ui.settings;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import treehou.se.habit.MainActivity;
import treehou.se.habit.R;
import treehou.se.habit.ui.control.ControllsFragment;
import treehou.se.habit.ui.servers.ServersFragment;
import treehou.se.habit.ui.settings.SettingsFragment;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class LicenseTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testOpenSettings() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(allOf(withId(R.id.lbl_name), withText(R.string.settings))).perform(ViewActions.click());
        onView(withText(R.string.open_source_libraries)).perform(ViewActions.click());
    }
}