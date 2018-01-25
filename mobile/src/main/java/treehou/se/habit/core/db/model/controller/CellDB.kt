package treehou.se.habit.core.db.model.controller

import android.graphics.Color

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CellDB : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var cellRow: CellRowDB? = null
    var color = Color.parseColor("#33000000")
    var label: String? = ""

    private var cellButton: ButtonCellDB? = null
    private var cellColor: ColorCellDB? = null
    private var cellIncDec: IncDecCellDB? = null
    private var cellSlider: SliderCellDB? = null
    private var cellVoice: VoiceCellDB? = null

    val type: Int
        get() {
            if (cellButton != null) {
                return TYPE_BUTTON
            } else if (cellVoice != null) {
                return TYPE_VOICE
            } else if (cellColor != null) {
                return TYPE_COLOR
            } else if (cellSlider != null) {
                return TYPE_SLIDER
            } else if (cellIncDec != null) {
                return TYPE_INC_DEC
            }

            return TYPE_EMPTY
        }

    fun setCellButton(cellButton: ButtonCellDB) {
        clearCellData()
        this.cellButton = cellButton

    }

    fun setCellColor(cellColor: ColorCellDB) {
        clearCellData()
        this.cellColor = cellColor
    }

    fun setCellIncDec(cellIncDec: IncDecCellDB) {
        clearCellData()
        this.cellIncDec = cellIncDec
    }

    fun setCellSlider(cellSlider: SliderCellDB) {
        clearCellData()
        this.cellSlider = cellSlider
    }

    fun setCellVoice(cellVoice: VoiceCellDB) {
        clearCellData()
        this.cellVoice = cellVoice
    }

    fun getCellButton(): ButtonCellDB? {
        return cellButton
    }

    fun getCellColor(): ColorCellDB? {
        return cellColor
    }

    fun getCellIncDec(): IncDecCellDB? {
        return cellIncDec
    }

    fun getCellSlider(): SliderCellDB? {
        return cellSlider
    }

    fun getCellVoice(): VoiceCellDB? {
        return cellVoice
    }

    private fun clearCellData() {
        cellButton = null
        cellColor = null
        cellIncDec = null
        cellSlider = null
        cellVoice = null
    }

    companion object {

        val TYPE_EMPTY = 0
        val TYPE_BUTTON = 1
        val TYPE_VOICE = 2
        val TYPE_COLOR = 3
        val TYPE_SLIDER = 4
        val TYPE_INC_DEC = 5

        fun load(realm: Realm, id: Long): CellDB? {
            return realm.where(CellDB::class.java).equalTo("id", id).findFirst()
        }

        fun save(realm: Realm, item: CellDB): CellDB {
            realm.beginTransaction()
            if (item.id <= 0) {
                item.id = getUniqueId(realm)
            }
            val cell = realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()

            return cell
        }

        fun getUniqueId(realm: Realm): Long {
            val num = realm.where(CellDB::class.java).max("id")
            var newId: Long = 1
            if (num != null) newId = num.toLong() + 1
            return newId
        }
    }
}
