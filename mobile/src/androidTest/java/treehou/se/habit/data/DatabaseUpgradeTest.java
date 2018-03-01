package treehou.se.habit.data;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import treehou.se.habit.core.db.OHRealm;
import treehou.se.habit.core.db.OHRealmModule;
import treehou.se.habit.main.MainActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DatabaseUpgradeTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    public RealmConfiguration configurationV4() {

        OHRealm ohRealm = new OHRealm(activityRule.getActivity());

        return new RealmConfiguration.Builder()
                .modules(new OHRealmModule())
                .migration(ohRealm.getMigration())
                .assetFile("treehou_04.realm")
                .name("treehou_04.realm")
                .schemaVersion(5)
                .build();
    }

    @Test
    public void testOpenSitemaps() {

        RealmConfiguration configuration = configurationV4();
        new File(configuration.getPath()).delete();

        RealmConfiguration realmConfiguration = configurationV4();
        boolean migrationSuccess = true;
        try {
            Realm realm = Realm.getInstance(realmConfiguration);
        } catch (Exception e) {
            Log.e(DatabaseUpgradeTest.class.getSimpleName(), "Database migration failed", e);
            migrationSuccess = false;
        }
        Assert.assertTrue(migrationSuccess);
    }
}
