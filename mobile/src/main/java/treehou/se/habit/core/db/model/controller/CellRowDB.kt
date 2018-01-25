package treehou.se.habit.core.db.model.controller

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject

open class CellRowDB : RealmObject() {

    var controller: ControllerDB? = null
    var cells = RealmList<CellDB>()

    fun addCell(realm: Realm): CellDB {
        val cell = CellDB()
        cell.cellRow = this
        val cellDB = CellDB.save(realm, cell)

        realm.beginTransaction()
        cells.add(cellDB)
        realm.commitTransaction()

        return cell
    }
}
