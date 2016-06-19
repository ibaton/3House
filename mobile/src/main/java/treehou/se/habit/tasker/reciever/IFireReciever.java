package treehou.se.habit.tasker.reciever;

import android.content.Context;
import android.os.Bundle;

public interface IFireReciever {

    String BUNDLE_EXTRA_TYPE    = "treehou.se.habit.extra.TYPE";

    boolean fire(Context context, Bundle bundle);
}
