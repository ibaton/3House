package treehou.se.habit.ui.servers

import io.realm.Realm
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.RxPresenter
import javax.inject.Inject

class ServerMenuPresenter @Inject
constructor() : RxPresenter(), ServerMenuContract.Presenter {

    @Inject lateinit var view: ServerMenuContract.View
    @Inject lateinit var realm: Realm

    override fun editServerClicked(serverId: Long) {
        val server = realm.where(ServerDB::class.java).equalTo("id", serverId).findFirst()

        val isMyOpenhabServer = server?.isMyOpenhabServer ?: false;
        if(isMyOpenhabServer){
            view.openEditMyOpenhabServerPage(serverId)
        } else {
            view.openEditServerPage(serverId)
        }
    }
}
