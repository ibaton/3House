package treehou.se.habit.util.logging;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

public class FirebaseLogger implements Logger {

    @Override
    public void d(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void i(String tag, String message) {
        Log.i(tag, message);
    }

    @Override
    public void w(String tag, String message) {
        Log.w(tag, message);
    }

    @Override
    public void w(String tag, String message, Throwable error) {
        Log.e(tag, message, error);
        FirebaseCrash.report(error);
    }

    @Override
    public void e(String tag, String message, Throwable error) {
        Log.e(tag, message, error);
        FirebaseCrash.report(error);
    }

    @Override
    public void e(String tag, String message) {
        Log.e(tag, message);
    }
}
