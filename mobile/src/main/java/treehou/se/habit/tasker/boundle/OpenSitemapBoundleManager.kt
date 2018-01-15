package treehou.se.habit.tasker.boundle

import android.os.Bundle

import treehou.se.habit.core.db.model.SitemapDB
import treehou.se.habit.tasker.reciever.IFireReciever

class OpenSitemapBoundleManager
/**
 * Private constructor prevents instantiation
 *
 * @throws UnsupportedOperationException because this class cannot be instantiated.
 */
private constructor() {

    init {
        throw UnsupportedOperationException("This class is non-instantiable") //$NON-NLS-1$
    }

    companion object {

        val TAG = "OpenSitemapBoundleManager"

        val TYPE_COMMAND = 2

        /**
         * @param sitemap The toast message to be displayed by the plug-in. Cannot be null.
         * @return A plug-in bundle.
         */
        fun generateOpenSitemapBundle(sitemap: SitemapDB): Bundle {

            val result = Bundle()
            result.putInt(IFireReciever.BUNDLE_EXTRA_TYPE, TYPE_COMMAND)

            return result
        }
    }
}
