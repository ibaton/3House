package treehou.se.habit.mvp


import android.os.Bundle
import android.support.annotation.CallSuper

import treehou.se.habit.BaseActivity
import treehou.se.habit.HabitApplication
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders

abstract class BaseDaggerActivity<out T : BasePresenter>(val useSettingsTheme: Boolean = false) : BaseActivity(), BaseView<T> {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityComponent()
        getPresenter()?.load(intent.extras, savedInstanceState)
        if(useSettingsTheme) setTheme(settings.themeResourse)
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
