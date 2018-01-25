package treehou.se.habit.core.db.model.controller

import io.realm.RealmObject
import treehou.se.habit.core.db.model.ItemDB

open class ButtonCellDB : RealmObject() {

    var icon: String? = null
    var command: String? = null
    var item: ItemDB? = null
}
