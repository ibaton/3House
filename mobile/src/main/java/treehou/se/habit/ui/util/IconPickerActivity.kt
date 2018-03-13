package treehou.se.habit.ui.util

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity

import javax.inject.Inject

import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.util.Settings

class IconPickerActivity : AppCompatActivity() {

    @Inject lateinit var settings: Settings

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as HabitApplication).component().inject(this)
        setTheme(settings.themeResourse)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_icon_picker)

        val fragmentManager = supportFragmentManager
        if (fragmentManager.findFragmentById(R.id.content) == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.content, CategoryPickerFragment.newInstance())
                    .commit()
        }
    }

    companion object {

        val RESULT_ICON = IconPickerFragment.RESULT_ICON
    }
}
