package treehou.se.habit

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule

import treehou.se.habit.module.ApplicationComponent

/**
 * [ActivityTestRule] which provides hook for
 * [ActivityTestRule.beforeActivityLaunched] method. Can be used for test dependency
 * injection especially in Espresso based tests.
 *
 * @author Tomasz Rozbicki
 */
abstract class DaggerActivityTestRule<T : Activity> @JvmOverloads constructor(activityClass: Class<T>, initialTouchMode: Boolean = false,
                                                                              launchActivity: Boolean = true) : ActivityTestRule<T>(activityClass, initialTouchMode, launchActivity) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()

        val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as HabitApplication

        DatabaseUtil.init(application)

        val component = setupComponent(application, activity)
        application.setComponent(component)
    }


    abstract fun setupComponent(application: HabitApplication, activity: Activity?): ApplicationComponent
}