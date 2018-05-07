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

    return typedValue.data
}


fun Resources.dpToPixels(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getDisplayMetrics())
}