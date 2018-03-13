package treehou.se.habit.ui.util

import android.graphics.PorterDuff
import android.widget.RemoteViews

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Contains code helping to create views.
 */
object ViewHelper {

    fun colorRemoteDrawable(cellView: RemoteViews, resource: Int, color: Int) {
        try {
            val c = Class.forName("android.widget.RemoteViews")
            val m = c.getMethod("setDrawableParameters", Int::class.javaPrimitiveType, Boolean::class.javaPrimitiveType, Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, PorterDuff.Mode::class.java, Int::class.javaPrimitiveType)
            m.invoke(cellView, resource, true, -1, color, PorterDuff.Mode.MULTIPLY, -1)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }
}
