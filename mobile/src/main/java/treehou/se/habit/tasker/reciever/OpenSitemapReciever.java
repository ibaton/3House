package treehou.se.habit.tasker.reciever;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import se.treehou.ng.ohcommunicator.core.OHSitemapWrapper;
import treehou.se.habit.MainActivity;
import treehou.se.habit.tasker.boundle.OpenSitemapBoundleManager;

public class OpenSitemapReciever implements IFireReciever {

    public static final String TAG = "OpenSitemapReciever";

    public static final int TYPE = OpenSitemapBoundleManager.TYPE_COMMAND;

    public static final String BUNDLE_EXTRA_SITEMAP = "treehou.se.habit.extra.SITEMAP";

    public boolean isBundleValid(Bundle bundle) {
        if (null == bundle) {
            Log.e(TAG, "Bundle cant be null");
            return false;
        }

        if (!bundle.containsKey(BUNDLE_EXTRA_SITEMAP)) {
            Log.e(TAG, String.format("bundle must contain extra %s", BUNDLE_EXTRA_SITEMAP));
            return false;
        }

        if (2 != bundle.keySet().size()) {
            Log.e(TAG, String.format("bundle must contain 2 keys, but currently contains %d keys: %s", bundle.keySet().size(), bundle.keySet()));
            return false;
        }

        return true;
    }


    @Override
    public boolean fire(Context context, Bundle bundle) {

        if (isBundleValid(bundle)) {
            OHSitemapWrapper sitemap = OHSitemapWrapper.load(bundle.getInt(BUNDLE_EXTRA_SITEMAP));
            Log.d(TAG, "Open sitemap.");
            if(sitemap != null){
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(MainActivity.EXTRA_SHOW_SITEMAP, sitemap.getId());
                context.startActivity(intent);
            }
        }else {
            Log.d(TAG, "Boundle not valid.");
        }

        return false;
    }
}
