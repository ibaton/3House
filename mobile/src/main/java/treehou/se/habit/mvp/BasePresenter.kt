package treehou.se.habit.mvp

import android.os.Bundle

interface BasePresenter {
    fun load(launchData: Bundle?, savedData: Bundle?)
    fun subscribe()
    fun unsubscribe()
    fun unload()
    fun save(savedData: Bundle?)
}
