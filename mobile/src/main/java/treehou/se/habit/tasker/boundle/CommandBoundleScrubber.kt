package treehou.se.habit.tasker.boundle

import android.content.Intent
import android.os.Bundle

object CommandBoundleScrubber {

    fun scrub(intent: Intent?): Boolean {
        return null != intent && scrub(intent.extras)

    }

    fun scrub(bundle: Bundle?): Boolean {
        if (null == bundle) {
            return false
        }

        try {
            bundle.containsKey(null)
        } catch (e: Exception) {
            bundle.clear()
            return true
        }

        return false
    }
}
