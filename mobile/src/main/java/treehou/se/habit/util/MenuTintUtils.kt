package treehou.se.habit.util


import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView

object MenuTintUtils {

    fun tintAllIcons(context: Context, menu: Menu) {
        tintAllIcons(menu, getMenuColor(context))
    }

    fun tintAllIcons(menu: Menu, color: Int) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            tintMenuItemIcon(color, item)
            tintShareIconIfPresent(color, item)
        }
    }

    @ColorInt
    fun getMenuColor(context: Context): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        return typedValue.data
    }

    private fun tintMenuItemIcon(color: Int, item: MenuItem) {
        val drawable = item.icon
        if (drawable != null) {
            val wrapped = DrawableCompat.wrap(drawable)
            drawable.mutate()
            DrawableCompat.setTint(wrapped, color)
            item.icon = drawable
        }
    }

    private fun tintShareIconIfPresent(color: Int, item: MenuItem) {
        if (item.actionView != null) {
            val actionView = item.actionView
            val expandActivitiesButton = actionView.findViewById<View>(android.support.v7.appcompat.R.id.expand_activities_button)
            if (expandActivitiesButton != null) {
                val image = expandActivitiesButton.findViewById<View>(android.support.v7.appcompat.R.id.image) as ImageView
                val drawable = image.drawable
                val wrapped = DrawableCompat.wrap(drawable)
                drawable.mutate()
                DrawableCompat.setTint(wrapped, color)
                image.setImageDrawable(drawable)
            }
        }
    }
}
