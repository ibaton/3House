package treehou.se.habit.ui.colorpicker

import android.os.Bundle

import com.google.gson.Gson

import javax.inject.Inject

import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.util.GsonHelper
import treehou.se.habit.BaseActivity
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.util.Settings

class ColorpickerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as HabitApplication).component().inject(this)
        setTheme(settings.themeResourse)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_colorpicker)

        val bundle = intent.extras
        val serverId = bundle!!.getLong(EXTRA_SERVER)
        val jWidget = bundle.getString(EXTRA_WIDGET)
        val color = bundle.getInt(EXTRA_COLOR)

        val gson = GsonHelper.createGsonBuilder()
        val widget = gson.fromJson(jWidget, OHWidget::class.java)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, LightFragment.newInstance(serverId, widget, color))
                    .commit()
        }
    }

    companion object {

        val EXTRA_SERVER = "EXTRA_SERVER"
        val EXTRA_WIDGET = "EXTRA_SITEMAP"
        val EXTRA_COLOR = "EXTRA_COLOR"
    }
}
