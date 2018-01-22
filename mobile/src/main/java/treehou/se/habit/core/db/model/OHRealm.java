package treehou.se.habit.core.db.model;

import android.content.Context;
import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmResults;
import io.realm.RealmSchema;
import treehou.se.habit.core.db.model.controller.ButtonCellDB;

public class OHRealm {

    private static final String TAG = "OHRealm";
    private Context context;

    public OHRealm(Context context) {
        this.context = context;
    }

    public void setup(Context context) {
        Realm.init(context);
        Realm.setDefaultConfiguration(configuration());
    }

    public RealmConfiguration configuration() {
        return new RealmConfiguration.Builder()
                .modules(new OHRealmModule())
                .migration(migration)
                .name("treehou.realm")
                .schemaVersion(4)
                .build();
    }

    public Realm realm(){
        return Realm.getDefaultInstance();
    }

    public RealmMigration migration = (realm, oldVersion, newVersion) -> {

        RealmSchema schema = realm.getSchema();
        switch ((int) oldVersion) {
            case 0:
                schema.create("SitemapSettingsDB")
                        .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                        .addField("display", boolean.class);

                schema.get("SitemapDB")
                        .addRealmObjectField("settingsDB", schema.get("SitemapSettingsDB"));
            case 1:
                RealmResults<DynamicRealmObject> buttonCellDB = realm.where("ButtonCellDB").findAll();
                RealmResults<DynamicRealmObject> cellColorDB = realm.where("ColorCellDB").findAll();
                RealmResults<DynamicRealmObject> cellIncDecDB = realm.where("IncDecCellDB").findAll();
                RealmResults<DynamicRealmObject> cellSliderDB = realm.where("SliderCellDB").findAll();
                RealmResults<DynamicRealmObject> cellVoiceDB = realm.where("VoiceCellDB").findAll();

                schema.get("CellDB")
                        .addRealmObjectField("cellButton", schema.get("ButtonCellDB"))
                        .addRealmObjectField("cellColor", schema.get("ColorCellDB"))
                        .addRealmObjectField("cellIncDec", schema.get("IncDecCellDB"))
                        .addRealmObjectField("cellSlider", schema.get("SliderCellDB"))
                        .addRealmObjectField("cellVoice", schema.get("VoiceCellDB"))
                        .removeField("type");

                for(DynamicRealmObject cell : buttonCellDB){
                    cell.getObject("cell").setObject("cellButton", cell);
                }
                for(DynamicRealmObject cell : cellColorDB){
                    cell.getObject("cell").setObject("cellColor", cell);
                }
                for(DynamicRealmObject cell : cellIncDecDB){
                    cell.getObject("cell").setObject("cellIncDec", cell);
                }
                for(DynamicRealmObject cell : cellSliderDB){
                    cell.getObject("cell").setObject("cellSlider", cell);
                }
                for(DynamicRealmObject cell : cellVoiceDB){
                    cell.getObject("cell").setObject("cellVoice", cell);
                }

                schema.get("ButtonCellDB").removePrimaryKey().removeField("id").removeField("cell");
                schema.get("ColorCellDB").removePrimaryKey().removeField("id").removeField("cell");
                schema.get("IncDecCellDB").removePrimaryKey().removeField("id").removeField("cell");
                schema.get("SliderCellDB").removePrimaryKey().removeField("id").removeField("cell");
                schema.get("VoiceCellDB").removePrimaryKey().removeField("id").removeField("cell");
            case 2:
                schema.get("CellRowDB").removePrimaryKey().removeField("id");
        }
    };
}
