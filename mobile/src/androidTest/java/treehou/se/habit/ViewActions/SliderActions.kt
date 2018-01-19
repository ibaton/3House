package treehou.se.habit.ViewActions

import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers
import android.view.View
import android.widget.SeekBar

import org.hamcrest.Description
import org.hamcrest.Matcher

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object SliderActions {

    fun setProgress(progress: Int): ViewAction {
        return object : ViewAction {
            override fun perform(uiController: UiController, view: View) {
                val seekBar = view as SeekBar
                setSeekBarProgress(seekBar, progress, true)
            }

            override fun getDescription(): String {
                return "Set a progress on a SeekBar"
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(SeekBar::class.java)
            }
        }
    }

    private fun setSeekBarProgress(seekBar: SeekBar, newProgress: Int, fromUser: Boolean) {

        var privateSetProgressMethod: Method? = null
        try {
            privateSetProgressMethod = SeekBar::class.java.getDeclaredMethod("setProgress", Integer.TYPE, java.lang.Boolean.TYPE)
            privateSetProgressMethod!!.isAccessible = true
            privateSetProgressMethod.invoke(seekBar, newProgress, fromUser)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun withProgress(expectedProgress: Int): Matcher<View> {

        return object : BoundedMatcher<View, SeekBar>(SeekBar::class.java) {
            override fun matchesSafely(seekBar: SeekBar): Boolean {
                return seekBar.progress == expectedProgress
            }

            override fun describeTo(description: Description) {
                description.appendText("expected: ")
                description.appendText("" + expectedProgress)
            }
        }
    }
}
