package treehou.se.habit.ui.servers.serverlist

import javax.inject.Inject

import treehou.se.habit.module.RxPresenter
import treehou.se.habit.ui.settings.SettingsContract

class ServersPresenter @Inject
constructor(private val view: ServersContract.View) : RxPresenter(), ServersContract.Presenter
