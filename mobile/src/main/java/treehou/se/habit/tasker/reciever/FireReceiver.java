package treehou.se.habit.tasker.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import treehou.se.habit.tasker.boundle.CommandBoundleManager;
import treehou.se.habit.tasker.boundle.CommandBoundleScrubber;

public final class FireReceiver extends BroadcastReceiver {

    private static final String TAG = "FireReceiver";

    private static final Map<Integer, IFireReciever> recievers = new HashMap<>();
    static {
        recievers.put(CommandReciever.TYPE, new CommandReciever());
        recievers.put(OpenSitemapReciever.TYPE, new OpenSitemapReciever());
        recievers.put(IncDecReciever.TYPE, new IncDecReciever());
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!treehou.se.habit.tasker.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
            Log.e(TAG, "Received unexpected Intent action " + intent.getAction());
            return;
        }
        Log.d(TAG, "Received Intent action " + intent.getAction());

        CommandBoundleScrubber.scrub(intent);

        final Bundle bundle = intent.getBundleExtra(treehou.se.habit.tasker.locale.Intent.EXTRA_BUNDLE);
        CommandBoundleScrubber.scrub(bundle);

        int type = bundle.getInt(IFireReciever.BUNDLE_EXTRA_TYPE, -1);
        IFireReciever reciever = recievers.get(type);
        if(reciever != null){
            reciever.fire(context, bundle);
        }else{
            Log.d(TAG, "No vald recievers found, Type: " + type + " Size: " + recievers.size());
        }
    }
}