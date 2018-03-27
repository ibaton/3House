package treehou.se.habit.dagger

import android.app.Activity
import android.support.v4.app.Fragment

interface HasActivitySubcomponentBuilders {
    fun getActivityComponentBuilder(activityClass: Class<out Activity>): ActivityComponentBuilder<*, *>
    fun getFragmentComponentBuilder(fragmentClass: Class<out Fragment>): FragmentComponentBuilder<*, *>
}