package treehou.se.habit.connector

import android.app.Activity
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle



class Analytics(val analytics: FirebaseAnalytics) {

    fun logScreenView(activity: Activity, name: String){
        analytics.setCurrentScreen(activity, name, name)
    }
}