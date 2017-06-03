package treehou.se.habit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import treehou.se.habit.ui.control.ControllerUtil;

public class RestartBroadcastReceiver extends BroadcastReceiver {

    @Inject ControllerUtil controllerUtil;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((HabitApplication)context.getApplicationContext()).component().inject(this);

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            controllerUtil.showNotifications(context.getApplicationContext());
        }
    }
}
