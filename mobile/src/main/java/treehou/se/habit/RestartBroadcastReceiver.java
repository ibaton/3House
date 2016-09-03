package treehou.se.habit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import treehou.se.habit.ui.control.ControllerUtil;

public class RestartBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            ControllerUtil.showNotifications(context.getApplicationContext());
        }
    }
}
