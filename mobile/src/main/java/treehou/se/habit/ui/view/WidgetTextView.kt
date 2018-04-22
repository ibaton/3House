package treehou.se.habit.ui.view

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.Spanned
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_widget_text.view.*
import treehou.se.habit.R
import treehou.se.habit.util.OpenHabUtil

class WidgetTextView : FrameLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_widget_text, this, true)
    }

    fun setText(text: String, color: String?){
        widgetText.text = createLabel(text, color)
    }

    /**
     * Create label text correctly formated to display values.
     *
     * @param context calling context.
     * @param name the label.
     * @return formated label
     */
    private fun createLabel(name: String, valueColor: String? = null): Spanned {
        val nameSpaned = name.replace("(\\[)(.*)(\\])".toRegex(), colorToHex(valueColor))
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(nameSpaned, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(nameSpaned)
        }
    }

    private fun colorToHex(valueColor: String? = null): String {
        return "<font color='" + String.format("#%06X", 0xFFFFFF and getValueColor(context, valueColor)) + "'>$2</font>"
    }

    /**
     * Get value color
     *
     * @param context
     * @param value
     * @return
     */
    private fun getValueColor(context: Context, value: String?): Int {
        return getColor(context, value, R.color.colorAccent)
    }

    /**
     * Get value color
     * @param context
     * @param value
     * @return
     */
    private fun getColor(context: Context, value: String?, @ColorRes defaultColorRes: Int): Int {
        val colorRes = OpenHabUtil.openhabColors[value]
        if (colorRes != null) {
            return ContextCompat.getColor(context, colorRes)
        }

        try {
            return Color.parseColor(value)
        } catch (ignore: Exception) {
        }

        return ContextCompat.getColor(context, defaultColorRes)
    }
}
