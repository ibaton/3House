package treehou.se.habit.tasker.boundle;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import treehou.se.habit.core.Item;
import treehou.se.habit.core.db.SitemapDB;
import treehou.se.habit.tasker.reciever.CommandReciever;
import treehou.se.habit.tasker.reciever.IFireReciever;
import treehou.se.habit.tasker.reciever.OpenSitemapReciever;

/**
 * Created by ibaton on 2015-03-08.
 */
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
        result.putLong(OpenSitemapReciever.BUNDLE_EXTRA_SITEMAP, sitemap.getId());

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
