package treehou.se.habit.core.db.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import se.treehou.ng.ohcommunicator.connector.models.OHStateDescription

open class StateDescriptionDB : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var pattern: String? = null
    var readOnly: Boolean = false

    fun toGeneric(): OHStateDescription {
        val stateDescription = OHStateDescription()
        stateDescription.pattern = pattern
        stateDescription.isReadOnly = readOnly
        return stateDescription
    }
}
