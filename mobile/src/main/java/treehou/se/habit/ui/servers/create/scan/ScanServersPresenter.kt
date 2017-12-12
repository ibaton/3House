package treehou.se.habit.ui.servers.create.custom

import treehou.se.habit.module.RxPresenter
import javax.inject.Inject

class ScanServersPresenter
@Inject
constructor(private val view: ScanServersContract.View) : RxPresenter(), ScanServersContract.Presenter {

}
