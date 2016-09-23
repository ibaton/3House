package treehou.se.habit;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class NavigationUtil {

    public static void navigateToSettings(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(allOf(withId(R.id.lbl_name), withText(R.string.settings))).perform(ViewActions.click());
    }

    public static void navigateToServer(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(allOf(withId(R.id.lbl_name), withText(R.string.servers))).perform(ViewActions.click());
    }

    public static void navigateToSitemap(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(allOf(withId(R.id.lbl_name), withText(R.string.sitemaps))).perform(ViewActions.click());
    }

    public static void navigateToController(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(allOf(withId(R.id.lbl_name), withText(R.string.controllers))).perform(ViewActions.click());
    }
}
