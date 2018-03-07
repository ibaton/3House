package treehou.se.habit.core.db

import android.content.Context
import io.realm.*
import io.realm.exceptions.RealmMigrationNeededException

open class OHRealm(private val context: Context) {

    var migration = object: RealmMigration{

        override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
            val schema = realm.schema
            if (oldVersion <= 0) upgradeFromVersion0(realm, schema)
            if (oldVersion <= 1) upgradeFromVersion1(realm, schema)
            if (oldVersion <= 2) upgradeFromVersion2(realm, schema)
            if (oldVersion <= 4) upgradeFromVersion4(realm, schema)
        }

        override fun equals(other: Any?): Boolean {
            return hashCode() == other?.hashCode()
        }

        override fun hashCode(): Int {
            return 163
        }
    }

    open fun setup(context: Context) {
        Realm.init(context)
        Realm.setDefaultConfiguration(configuration())
    }

    open fun configuration(): RealmConfiguration {
        return RealmConfiguration.Builder()
                .modules(OHRealmModule())
                .migration(migration)
                .name("treehou.realm")
                .schemaVersion(5)
                .build()
    }

    fun realm(): Realm {
        try {
            return Realm.getDefaultInstance()
        } catch (e: RealmMigrationNeededException) {
            Realm.deleteRealm(configuration())
            return Realm.getDefaultInstance()
        }

    }

    fun upgradeFromVersion0(realm: DynamicRealm, schema: RealmSchema) {
        schema.create("SitemapSettingsDB")
                .addField("id", Long::class.javaPrimitiveType!!, FieldAttribute.PRIMARY_KEY)
                .addField("display", Boolean::class.javaPrimitiveType)

        schema.get("SitemapDB")!!
                .addRealmObjectField("settingsDB", schema.get("SitemapSettingsDB")!!)
        val buttonCellDB = realm.where("ButtonCellDB").findAll()
        val cellColorDB = realm.where("ColorCellDB").findAll()
        val cellIncDecDB = realm.where("IncDecCellDB").findAll()
        val cellSliderDB = realm.where("SliderCellDB").findAll()
        val cellVoiceDB = realm.where("VoiceCellDB").findAll()

        schema.get("CellDB")!!
                .addRealmObjectField("cellButton", schema.get("ButtonCellDB")!!)
                .addRealmObjectField("cellColor", schema.get("ColorCellDB")!!)
                .addRealmObjectField("cellIncDec", schema.get("IncDecCellDB")!!)
                .addRealmObjectField("cellSlider", schema.get("SliderCellDB")!!)
                .addRealmObjectField("cellVoice", schema.get("VoiceCellDB")!!)
                .removeField("type")

        for (cell in buttonCellDB) {
            cell.getObject("cell")!!.setObject("cellButton", cell)
        }
        for (cell in cellColorDB) {
            cell.getObject("cell")!!.setObject("cellColor", cell)
        }
        for (cell in cellIncDecDB) {
            cell.getObject("cell")!!.setObject("cellIncDec", cell)
        }
        for (cell in cellSliderDB) {
            cell.getObject("cell")!!.setObject("cellSlider", cell)
        }
        for (cell in cellVoiceDB) {
            cell.getObject("cell")!!.setObject("cellVoice", cell)
        }

        schema.get("ButtonCellDB")!!.removePrimaryKey().removeField("id").removeField("cell")
        schema.get("ColorCellDB")!!.removePrimaryKey().removeField("id").removeField("cell")
        schema.get("IncDecCellDB")!!.removePrimaryKey().removeField("id").removeField("cell")
        schema.get("SliderCellDB")!!.removePrimaryKey().removeField("id").removeField("cell")
        schema.get("VoiceCellDB")!!.removePrimaryKey().removeField("id").removeField("cell")
        schema.get("CellRowDB")!!.removePrimaryKey().removeField("id")
        schema.get("ServerDB")!!.addField("isMyOpenhabServer", Boolean::class.javaPrimitiveType)
    }

    fun upgradeFromVersion1(realm: DynamicRealm, schema: RealmSchema) {
        val buttonCellDB = realm.where("ButtonCellDB").findAll()
        val cellColorDB = realm.where("ColorCellDB").findAll()
        val cellIncDecDB = realm.where("IncDecCellDB").findAll()
        val cellSliderDB = realm.where("SliderCellDB").findAll()
        val cellVoiceDB = realm.where("VoiceCellDB").findAll()
        schema.get("CellDB")!!.addRealmObjectField("cellButton", schema.get("ButtonCellDB")!!).addRealmObjectField("cellColor", schema.get("ColorCellDB")!!).addRealmObjectField("cellIncDec", schema.get("IncDecCellDB")!!).addRealmObjectField("cellSlider", schema.get("SliderCellDB")!!).addRealmObjectField("cellVoice", schema.get("VoiceCellDB")!!).removeField("type")
        for (cell in buttonCellDB) {
            cell.getObject("cell")!!.setObject("cellButton", cell)
        }
        for (cell in cellColorDB) {
            cell.getObject("cell")!!.setObject("cellColor", cell)
        }
        for (cell in cellIncDecDB) {
            cell.getObject("cell")!!.setObject("cellIncDec", cell)
        }
        for (cell in cellSliderDB) {
            cell.getObject("cell")!!.setObject("cellSlider", cell)
        }
        for (cell in cellVoiceDB) {
            cell.getObject("cell")!!.setObject("cellVoice", cell)
        }
        schema.get("ButtonCellDB")!!.removePrimaryKey().removeField("id").removeField("cell")
        schema.get("ColorCellDB")!!.removePrimaryKey().removeField("id").removeField("cell")
        schema.get("IncDecCellDB")!!.removePrimaryKey().removeField("id").removeField("cell")
        schema.get("SliderCellDB")!!.removePrimaryKey().removeField("id").removeField("cell")
        schema.get("VoiceCellDB")!!.removePrimaryKey().removeField("id").removeField("cell")
        schema.get("CellRowDB")!!.removePrimaryKey().removeField("id")
        schema.get("ServerDB")!!.addField("isMyOpenhabServer", Boolean::class.javaPrimitiveType)
    }

    fun upgradeFromVersion2(realm: DynamicRealm, schema: RealmSchema) {
        schema.get("CellRowDB")!!.removePrimaryKey().removeField("id")
    }

    fun upgradeFromVersion4(realm: DynamicRealm, schema: RealmSchema) {
        if(!schema.get("ServerDB")!!.hasField("isMyOpenhabServer")) {
            schema.get("ServerDB")!!.addField("isMyOpenhabServer", Boolean::class.javaPrimitiveType)
        }
    }

    companion object {

        private val TAG = "OHRealm"
    }
}
