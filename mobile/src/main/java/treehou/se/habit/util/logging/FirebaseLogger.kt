package treehou.se.habit.util.logging

import android.util.Log

import com.google.firebase.crash.FirebaseCrash

class FirebaseLogger : Logger {

    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun w(tag: String, message: String) {
        Log.w(tag, message)
    }

    override fun w(tag: String, message: String, error: Throwable) {
        Log.e(tag, message, error)
        FirebaseCrash.report(error)
    }

    override fun e(tag: String, message: String, error: Throwable) {
        Log.e(tag, message, error)
        FirebaseCrash.report(error)
    }

    override fun e(tag: String, message: String) {
        Log.e(tag, message)
    }
}
