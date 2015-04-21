package treehou.se.habit.tasker.reciever;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by ibaton on 2015-03-08.
 */
public interface IFireReciever {

    public static final String BUNDLE_EXTRA_TYPE    = "treehou.se.habit.extra.TYPE";

    public boolean fire(Context context, Bundle bundle);
}
