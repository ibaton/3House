package treehou.se.habit.core.db.model

import android.text.TextUtils

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import se.treehou.ng.ohcommunicator.connector.models.OHServer

open class ServerDB : RealmObject() {

    @PrimaryKey
    var id: Long = 0

    var name: String? = ""
        set(name) {
            var name = name
            if (name == null) name = ""
            field = name
        }
    var username: String? = ""
        set(username) {
            var username = username
            if (username == null) username = ""
            field = username
        }
    var password: String? = ""
        set(password) {
            var password = password
            if (password == null) password = ""
            field = password
        }
    var localurl: String? = ""
        set(localurl) {
            var localurl = localurl
            if (localurl == null) localurl = ""
            field = localurl
        }
    var remoteurl: String? = ""
        set(remoteurl) {
            var remoteurl = remoteurl
            if (remoteurl == null) remoteurl = ""
            field = remoteurl
        }
    var isMyOpenhabServer = false

    /**
     * Set the major version of server
     *
     * @param majorversion
     */
    var majorversion: Int = 0

    val displayName: String?
        get() = name

    fun requiresAuth(): Boolean {
        return !TextUtils.isEmpty(this.username) && !TextUtils.isEmpty(this.password)
    }

    /**
     * Convert this object to a generic object that can be handled by openhab lib
     * @return generic server compatable with openhab lib.
     */
    fun toGeneric(): OHServer {
        return OHServer(
                id,
                name,
                username,
                password,
                localurl,
                remoteurl,
                majorversion)
    }

    companion object {

        fun load(realm: Realm, id: Long): ServerDB? {
            return realm.where(ServerDB::class.java).equalTo("id", id).findFirst()
        }

        fun save(item: ServerDB) {

            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            if (item.id <= 0) {
                item.id = uniqueId
            }

            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()
            realm.close()
        }

        val uniqueId: Long
            get() {
                val realm = Realm.getDefaultInstance()
                var id: Long = 1
                val num = realm.where(ServerDB::class.java).max("id")
                if (num != null) id = num.toLong() + 1
                realm.close()

                return id
            }

        /**
         * Convert this object to a db object that can be stored in db.
         * @return database server object.
         */
        fun fromGeneric(server: OHServer): ServerDB {
            val serverDB = ServerDB()
            serverDB.id = ServerDB.uniqueId
            serverDB.name = server.name
            serverDB.localurl = server.localUrl
            serverDB.remoteurl = server.remoteUrl
            serverDB.username = server.username
            serverDB.password = server.password
            return serverDB
        }
    }
}
