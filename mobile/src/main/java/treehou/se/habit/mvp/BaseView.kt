package treehou.se.habit.mvp

interface BaseView<out T : BasePresenter> {
    fun getPresenter(): T?
}