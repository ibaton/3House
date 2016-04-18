package treehou.se.habit;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static org.hamcrest.Matchers.allOf;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import treehou.se.habit.ui.ServersFragment;
import treehou.se.habit.ui.SitemapListFragment;
import treehou.se.habit.ui.control.ControllsFragment;
import treehou.se.habit.ui.settings.SettingsFragment;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MenuTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testOpenSitemaps() {

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(allOf(withId(R.id.lbl_name), withText(R.string.sitemaps))).perform(ViewActions.click());
        Assert.assertTrue(activityRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.page_container) instanceof SitemapListFragment);
    }

    @Test
    public void testOpenControllers() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(allOf(withId(R.id.lbl_name), withText(R.string.controllers))).perform(ViewActions.click());
        Assert.assertTrue(activityRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.page_container) instanceof ControllsFragment);
    }

    @Test
    public void testOpenServers() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(allOf(withId(R.id.lbl_name), withText(R.string.servers))).perform(ViewActions.click());
        Assert.assertTrue(activityRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.page_container) instanceof ServersFragment);
    }

    @Test
    public void testOpenSettings() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(allOf(withId(R.id.lbl_name), withText(R.string.settings))).perform(ViewActions.click());
        Assert.assertTrue(activityRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.page_container) instanceof SettingsFragment);
    }
}