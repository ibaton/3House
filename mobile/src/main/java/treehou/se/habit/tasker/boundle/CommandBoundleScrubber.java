package treehou.se.habit.tasker.boundle;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ibaton on 2015-03-08.
 */
public final class CommandBoundleScrubber {

    public static boolean scrub(final Intent intent) {
        if (null == intent) {
            return false;
        }

        return scrub(intent.getExtras());
    }

    public static boolean scrub(final Bundle bundle) {
        if (null == bundle) {
            return false;
        }

        try {
            bundle.containsKey(null);
        } catch (final Exception e) {
            bundle.clear();
            return true;
        }

        return false;
    }
}
