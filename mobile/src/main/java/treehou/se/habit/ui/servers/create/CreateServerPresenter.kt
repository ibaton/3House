package treehou.se.habit.ui.servers.create

import android.os.Bundle
import io.realm.Realm
import javax.inject.Inject

class CreateServerPresenter
@Inject
constructor(private val view: CreateServerContract.View, private val realm: Realm) : CreateServerContract.Presenter {

    override fun load(savedData: Bundle?) {}


    override fun subscribe() {

    }

    override fun unsubscribe() {

    }

    override fun save(savedData: Bundle?) {

    }

    override fun unload() {
        realm.close()
    }

    companion object {

        private val TAG = CreateServerPresenter::class.java.simpleName
    }
}
