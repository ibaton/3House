package treehou.se.habit.core.db.model.controller

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import treehou.se.habit.core.db.model.ItemDB

open class IncDecCellDB : RealmObject() {

    var icon: String? = null
    var type: Int = 0
    var item: ItemDB? = null
    var value = 0
    var min = 0
    var max = 100

    companion object {

        fun save(realm: Realm, item: IncDecCellDB) {
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()
        }
    }
}
