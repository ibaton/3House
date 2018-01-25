package treehou.se.habit.core.db.model.controller

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import treehou.se.habit.core.db.model.ItemDB

open class SliderCellDB : RealmObject() {

    var icon: String? = null
    var type: Int = 0
    var item: ItemDB? = null
    var min = 0
    var max = 100

    companion object {

        val TYPE_MAX = 0
        val TYPE_MIN = 1
        val TYPE_SLIDER = 2
        val TYPE_CHART = 3

        fun save(realm: Realm, item: SliderCellDB) {
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()
        }
    }
}
