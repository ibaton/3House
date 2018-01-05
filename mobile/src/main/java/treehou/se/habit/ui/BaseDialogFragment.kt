package treehou.se.habit.ui


import android.os.Bundle

import com.trello.rxlifecycle2.components.RxDialogFragment

import javax.inject.Inject

import io.realm.Realm
import treehou.se.habit.HabitApplication
import treehou.se.habit.util.logging.Logger

class BaseDialogFragment : RxDialogFragment() {

    protected lateinit var realm: Realm
    @Inject lateinit var logger: Logger

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
