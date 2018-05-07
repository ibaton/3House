package treehou.se.habit.util

import android.content.res.Resources
import android.content.res.Resources.Theme
import android.os.Build
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.util.Log
import android.util.TypedValue


@ColorInt
fun Resources.getColorAttr(@AttrRes colorAttribute: Int, theme: Theme): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(colorAttribute, typedValue, true)

    val colorRes = typedValue.resourceId
    var color = -1
    try {
        color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getColor(colorRes, theme)
        } else {
            getColor(colorRes)
        }
    } catch (e: Resources.NotFoundException) {
        Log.w("Resources", "Not found color resource by id: $colorRes")
    }

    return color
}


fun Resources.dpToPixels(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getDisplayMetrics())
}