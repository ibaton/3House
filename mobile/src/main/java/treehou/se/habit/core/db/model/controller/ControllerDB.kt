package treehou.se.habit.core.db.model.controller

import android.graphics.Color

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ControllerDB : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var name: String? = null
    var color = Color.parseColor("#33000000")
    var showNotification = false
    var showTitle = true
    var cellRows = RealmList<CellRowDB>()

    fun addRow(realm: Realm): CellRowDB {
        val cellRow = CellRowDB()
        cellRow.controller = this
        realm.beginTransaction()
        cellRows.add(cellRow)
        realm.commitTransaction()

        val cell = CellDB()
        cell.cellRow = cellRow
        CellDB.save(realm, cell)

        return cellRow
    }

    override fun toString(): String {
        return name ?: "ControllerDB"
    }

    companion object {

        fun load(realm: Realm, id: Long): ControllerDB? {
            return realm.where(ControllerDB::class.java).equalTo("id", id).findFirst()
        }

        fun save(realm: Realm, item: ControllerDB) {
            realm.beginTransaction()
            if (item.id <= 0) {
                item.id = getUniqueId(realm)
            }
            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()
        }

        fun getUniqueId(realm: Realm): Long {
            val num = realm.where(ControllerDB::class.java).max("id")
            var newId: Long = 1
            if (num != null) newId = num.toLong() + 1
            return newId
        }
    }
}
