package treehou.se.habit.ui.colorpicker


import android.content.Context
import android.os.Bundle
import android.util.Log

import java.util.Locale

import javax.inject.Inject
import javax.inject.Named

import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHItem
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import treehou.se.habit.connector.Constants
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.RxPresenter
import treehou.se.habit.ui.sitemaps.page.PageContract
import treehou.se.habit.util.ConnectionFactory

class LightPresenter @Inject
constructor(private val context: Context, private val realm: Realm, @param:Named("arguments") private val args: Bundle, private val connectionFactory: ConnectionFactory) : RxPresenter(), LightContract.Presenter {
    private var serverDb: ServerDB? = null
    private var server: OHServer? = null

    override fun load(launchData: Bundle?, savedData: Bundle?) {
        super.load(launchData, savedData)

        val serverId = args.getLong(PageContract.ARG_SERVER)
        serverDb = ServerDB.load(realm, serverId)
        server = serverDb!!.toGeneric()
    }

    override fun setHSV(item: OHItem, hue: Int, saturation: Int, value: Int) {
        val server = server
        if(server != null) {
            val serverHandler = connectionFactory.createServerHandler(server, context)
            Log.d(TAG, "Color changed to " + String.format("%d,%d,%d", hue, saturation, value))
            if (value > 5) {
                serverHandler.sendCommand(item.name, String.format(Locale.getDefault(), Constants.COMMAND_COLOR, hue, saturation, value))
            } else {
                serverHandler.sendCommand(item.name, Constants.COMMAND_OFF)
            }
        }
    }

    companion object {

        private val TAG = LightPresenter::class.java.simpleName
    }
}
