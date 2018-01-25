package treehou.se.habit.core.db.model.controller

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import treehou.se.habit.core.db.model.ItemDB

open class VoiceCellDB : RealmObject() {

    var icon: String? = null
    var item: ItemDB? = null

    companion object {

        fun save(realm: Realm, item: VoiceCellDB) {
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()
        }
    }
}
