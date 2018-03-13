package treehou.se.habit.util

import android.content.Intent
import android.net.Uri

object IntentHelper {

    fun helpTranslateIntent(): Intent {
        val url = "https://oswmdvr.oneskyapp.com/collaboration/project?id=71199"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        return intent
    }
}
