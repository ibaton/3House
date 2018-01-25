package treehou.se.habit.core.db.model

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import se.treehou.ng.ohcommunicator.connector.models.OHItem

open class ItemDB : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var server: ServerDB? = null
    var type: String? = null
    var name: String? = null
    var link: String? = null
    var state: String? = null
    var stateDescription: StateDescriptionDB? = null

    val formatedValue: String?
        get() {
            if (stateDescription != null && stateDescription!!.pattern != null) {

                val pattern = stateDescription!!.pattern ?: ""
                try {
                    return String.format(pattern, java.lang.Float.valueOf(state))
                } catch (e: Exception) {
                }

                try {
                    return String.format(pattern, Integer.valueOf(state))
                } catch (e: Exception) {
                }

                try {
                    return String.format(pattern, state)
                } catch (e: Exception) {
                }

            }

            return state
        }

    fun printableName(): String {
        return if (server != null) {
            server.toString() + ": " + name!!.replace("_|-".toRegex(), " ")
        } else name!!.replace("_|-".toRegex(), " ")
    }

    override fun toString(): String {
        return printableName()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj !is ItemDB) return false

        val item = obj as ItemDB?
        return type == item!!.type && name == item.name
    }

    fun toGeneric(): OHItem {
        val item = OHItem()
        item.name = name
        item.link = link
        item.state = state
        if (stateDescription != null) {
            item.stateDescription = stateDescription!!.toGeneric()
        }
        if (server != null) {
            item.server = server!!.toGeneric()
        }
        item.type = type
        return item
    }

    companion object {

        var TYPE_SWITCH = "SwitchItem"
        var TYPE_STRING = "StringItem"
        var TYPE_COLOR = "ColorItem"
        var TYPE_NUMBER = "NumberItem"
        var TYPE_CONTACT = "ContactItem"
        var TYPE_ROLLERSHUTTER = "RollershutterItem"
        var TYPE_GROUP = "GroupItem"
        var TYPE_DIMMER = "DimmerItem"

        fun save(realm: Realm, item: ItemDB) {
            realm.beginTransaction()
            if (item.id <= 0) {
                item.id = uniqueId(realm)
            }
            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()
        }

        fun printableName(itemDB: ItemDB): String? {
            return if (itemDB.server != null) {
                itemDB.server.toString() + ": " + itemDB.name
            } else itemDB.name
        }

        fun uniqueId(realm: Realm): Long {
            val num = realm.where(ItemDB::class.java).max("id")
            return if (num == null)
                1
            else
                num.toLong() + 1
        }

        fun load(realm: Realm, id: Long): ItemDB? {
            return realm.where(ItemDB::class.java).equalTo("id", id).findFirst()
        }

        fun createOrLoadFromGeneric(realm: Realm, item: OHItem): ItemDB? {
            var itemDB = realm.where(ItemDB::class.java).equalTo("name", item.name).findFirst()
            if (itemDB == null) {
                itemDB = ItemDB()
                itemDB.id = ItemDB.uniqueId(realm)
                itemDB.server = ServerDB.load(realm, item.server.id)
                itemDB.type = item.type
                itemDB.name = item.name
                itemDB.link = item.link
                itemDB.state = item.state
                itemDB = realm.copyToRealm(itemDB)
            }
            return itemDB
        }
    }
}
