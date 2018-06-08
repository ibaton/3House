package treehou.se.habit.mvp


import android.os.Bundle
import com.crashlytics.android.Crashlytics
import treehou.se.habit.HabitApplication

import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.util.Constants

abstract class BaseDaggerFragment<out T : BasePresenter> : BaseFragment(), BaseView<T> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupComponent()
        getPresenter()?.load(arguments, savedInstanceState)

        Crashlytics.setString(Constants.FIREABASE_DEBUG_KEY_FRAGMENT, javaClass.name);
    }

    override fun onResume() {
        super.onResume()
        getPresenter()?.subscribe()
    }

    override fun onPause() {
        super.onPause()
        getPresenter()?.unsubscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        getPresenter()?.unload()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getPresenter()?.save(outState)
    }

    protected fun setupComponent() {
        injectMembers(HabitApplication.get(context))
    }

    protected abstract fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders)
}
