package treehou.se.habit.tasker.boundle;

import android.os.Bundle;

import treehou.se.habit.core.db.model.SitemapDB;
import treehou.se.habit.tasker.reciever.IFireReciever;

public class OpenSitemapBoundleManager {

    public static final String TAG = "OpenSitemapBoundleManager";

    public static final int TYPE_COMMAND = 2;

    /**
     * @param sitemap The toast message to be displayed by the plug-in. Cannot be null.
     * @return A plug-in bundle.
     */
    public static Bundle generateOpenSitemapBundle(final SitemapDB sitemap) {

        final Bundle result = new Bundle();
        result.putInt(IFireReciever.BUNDLE_EXTRA_TYPE, TYPE_COMMAND);

        return result;
    }

    /**
     * Private constructor prevents instantiation
     *
     * @throws UnsupportedOperationException because this class cannot be instantiated.
     */
    private OpenSitemapBoundleManager() {
        throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
    }
}
