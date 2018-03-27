package treehou.se.habit.ui.servers.serverlist

import android.os.Bundle
import javax.inject.Inject

import treehou.se.habit.dagger.RxPresenter

class ServersPresenter @Inject
constructor(private val view: ServersContract.View) : RxPresenter(), ServersContract.Presenter {

    override fun load(launchData: Bundle?, savedData: Bundle?) {
        super.load(launchData, savedData)


    }
}
