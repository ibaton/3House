package treehou.se.habit.core.db.model;

import android.content.Context;

import javax.inject.Inject;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import io.realm.annotations.PrimaryKey;

public class OHRealm {

    private Context context;

    public OHRealm(Context context) {
        this.context = context;
    }

    public void setup(Context context) {
        Realm.setDefaultConfiguration(configuration(context));
    }

    public RealmConfiguration configuration(Context context) {
        return new RealmConfiguration.Builder(context)
                .modules(new OHRealmModule())
                .migration(migration)
                .name("TreehouTest.realm")
                .schemaVersion(2)
                .build();
    }

    public Realm realm(){
        return Realm.getDefaultInstance();
    }

    RealmMigration migration = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            RealmSchema schema = realm.getSchema();
            switch ((int) oldVersion) {
                case 1:
                    schema.create("SitemapSettingsDB")
                            .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                            .addField("display", boolean.class);

                    schema.get("SitemapDB")
                            .addRealmObjectField("settingsDB", schema.get("SitemapSettingsDB"));
            }
        }
    };
}
