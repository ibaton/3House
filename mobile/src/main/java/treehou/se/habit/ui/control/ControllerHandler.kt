package treehou.se.habit.ui.control


import android.util.Log
import java.util.HashSet

import io.reactivex.Observable
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import treehou.se.habit.core.db.model.controller.ControllerDB

class ControllerHandler(realm: Realm, private val controllerUtil: ControllerUtil) {

    private val realm: Realm
    private var notificationIds: Set<Long> = HashSet()

    private val controllerDBListener = RealmChangeListener<RealmResults<ControllerDB>> { this.handleControllerUpdates(it) }

    private// Needed for listener
    var controllers: RealmResults<ControllerDB>? = null

    init {
        this.realm = Realm.getDefaultInstance()
    }

    fun init() {
        controllers = realm.where(ControllerDB::class.java).findAll()
        updateNotifications(controllers)
        controllers!!.addChangeListener(controllerDBListener)
    }

    private fun handleControllerUpdates(controllerDBS: RealmResults<ControllerDB>) {
        val updateNotificationIds = HashSet<Long>()
        Observable.fromIterable(controllerDBS).map<Long>({ it.id }).subscribe({ updateNotificationIds.add(it) }, {Log.e(TAG, "Failed to load inbox items", it)})

        val notificationsDeleted = HashSet(notificationIds)
        notificationsDeleted.removeAll(updateNotificationIds)

        deleteNotifications(notificationsDeleted)
        updateNotifications(controllerDBS)

        notificationIds = updateNotificationIds
    }

    private fun deleteNotifications(controllerIds: Collection<Long>) {
        for (controllerDbId in controllerIds) {
            controllerUtil.hideNotification(controllerDbId.toInt())
        }
    }

    private fun updateNotifications(controllerDBs: List<ControllerDB>?) {
        for (controllerDB in controllerDBs!!) {
            if (controllerDB.showNotification) {
                controllerUtil.showNotification(controllerDB)
            } else {
                controllerUtil.hideNotification(controllerDB.id.toInt())
            }
        }
    }

    companion object {
        val TAG = "ControllerHandler"
    }
}
