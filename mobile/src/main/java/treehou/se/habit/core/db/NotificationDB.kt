package treehou.se.habit.core.db

import java.util.Date

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class NotificationDB : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var message: String? = ""
    var date: Date? = null
    var viewed: Boolean = false

    companion object {

        fun save(item: NotificationDB) {
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
                val num = realm.where(NotificationDB::class.java).max("id")
                var newId: Long = 1
                if (num != null) newId = num.toLong() + 1
                realm.close()
                return newId
            }
    }
}
