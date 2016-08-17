package treehou.se.habit.ui.util;

import android.graphics.PorterDuff;
import android.widget.RemoteViews;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Contains code helping to create views.
 */
public class ViewHelper {

    private ViewHelper() {}

    public static void colorRemoteDrawable(RemoteViews cellView, int resource, int color){
    try {
        Class c = Class.forName("android.widget.RemoteViews");
        Method m = c.getMethod("setDrawableParameters", int.class, boolean.class, int.class, int.class, PorterDuff.Mode.class, int.class);
        m.invoke(cellView, resource, true, -1, color, PorterDuff.Mode.MULTIPLY, -1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
