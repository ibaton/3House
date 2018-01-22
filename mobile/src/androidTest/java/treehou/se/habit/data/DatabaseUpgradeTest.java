package treehou.se.habit.data;

import android.app.Activity;
import android.content.Context;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.Provides;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import treehou.se.habit.DaggerActivityTestRule;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.core.db.model.OHRealmModule;
import treehou.se.habit.main.MainActivity;
import treehou.se.habit.module.ApplicationComponent;
import treehou.se.habit.module.DaggerApplicationComponent;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DatabaseUpgradeTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    public RealmConfiguration configurationV4() {

        OHRealm ohRealm = new OHRealm(activityRule.getActivity());

        return new RealmConfiguration.Builder()
                .modules(new OHRealmModule())
                .migration(ohRealm.migration)
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
        try{
            Realm realm = Realm.getInstance(realmConfiguration);
        } catch (Exception e) {
            Log.e(DatabaseUpgradeTest.class.getSimpleName(), "Database migration failed", e);
            migrationSuccess = false;
        }
        Assert.assertTrue(migrationSuccess);
    }
}
