package treehou.se.habit.ui


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup

import com.trello.rxlifecycle2.components.RxDialogFragment
import com.trello.rxlifecycle2.components.support.RxFragment

import javax.inject.Inject

import io.realm.Realm
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.util.MenuTintUtils
import treehou.se.habit.util.logging.Logger

class BaseDialogFragment : RxDialogFragment() {

    protected lateinit var realm: Realm
    @Inject @JvmField
    var logger: Logger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as HabitApplication).component().inject(this)
        realm = Realm.getDefaultInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
