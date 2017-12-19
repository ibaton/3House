package treehou.se.habit.mvp


import android.os.Bundle
import android.support.annotation.CallSuper

import treehou.se.habit.BaseActivity
import treehou.se.habit.HabitApplication
import treehou.se.habit.module.HasActivitySubcomponentBuilders
import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

abstract class BaseDaggerActivity<out T : BasePresenter> : BaseActivity(), BaseView<T> {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityComponent()
        getPresenter()?.load(intent.extras, savedInstanceState)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        getPresenter()?.subscribe()
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        getPresenter()?.unsubscribe()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        getPresenter()?.unload()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        getPresenter()?.save(outState)
    }

    protected fun setupActivityComponent() {
        injectMembers(HabitApplication.get(this))
    }

    protected abstract fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders)
}
