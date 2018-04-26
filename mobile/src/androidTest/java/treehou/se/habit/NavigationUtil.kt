package treehou.se.habit

import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.contrib.DrawerActions

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.view.View
import org.hamcrest.Matchers.allOf

object NavigationUtil {

    fun navigateToSettings() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(allOf<View>(withId(R.id.lbl_name), withText(R.string.settings))).perform(ViewActions.click())
    }

    fun navigateToServer() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(allOf<View>(withId(R.id.lbl_name), withText(R.string.servers))).perform(ViewActions.click())
    }

    fun navigateToSitemap() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(allOf<View>(withId(R.id.lbl_name), withText(R.string.sitemaps))).perform(ViewActions.click())
    }

    fun navigateToController() {
        onView(withId(R.id.drawerLayout)).perform(DrawerActions.open())
        onView(allOf<View>(withId(R.id.lbl_name), withText(R.string.controllers))).perform(ViewActions.click())
    }
}
