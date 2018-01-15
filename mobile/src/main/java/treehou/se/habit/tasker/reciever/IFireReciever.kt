package treehou.se.habit.tasker.reciever

import android.content.Context
import android.os.Bundle

interface IFireReciever {

    fun fire(context: Context, bundle: Bundle): Boolean

    companion object {

        val BUNDLE_EXTRA_TYPE = "treehou.se.habit.extra.TYPE"
    }
}
