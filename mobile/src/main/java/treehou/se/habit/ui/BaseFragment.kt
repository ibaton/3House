package treehou.se.habit.ui


import android.os.Bundle
import android.view.Menu
import com.trello.rxlifecycle2.components.support.RxFragment
import io.realm.Realm
import treehou.se.habit.HabitApplication
import treehou.se.habit.util.MenuTintUtils
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

open class BaseFragment : RxFragment() {

    protected lateinit var realm: Realm
    @Inject
    lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity!!.application as HabitApplication).component().inject(this)
        realm = Realm.getDefaultInstance()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        MenuTintUtils.tintAllIcons(context!!, menu!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
