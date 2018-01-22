package treehou.se.habit

import android.content.Context

import io.realm.Realm
import io.realm.RealmConfiguration
import treehou.se.habit.core.db.model.OHRealmModule
import treehou.se.habit.core.db.model.ServerDB

object DatabaseUtil {

    fun init(context: Context) {

        val configuration = RealmConfiguration.Builder()
                .modules(OHRealmModule())
                .name("treehou-test.realm")
                .inMemory()
                .schemaVersion(1)
                .build()
        Realm.setDefaultConfiguration(configuration)

        val serverSize = Realm.getDefaultInstance().where(ServerDB::class.java).findAll().size
        if (serverSize <= 0) {
            val server = ServerDB()
            server.name = "Test Server"
            server.localUrl = "http://127.0.0.1:8080"
            ServerDB.save(server)
        }
    }
}
