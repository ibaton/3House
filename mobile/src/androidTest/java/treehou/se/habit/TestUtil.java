package treehou.se.habit;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.test.RenamingDelegatingContext;

import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;
import treehou.se.habit.data.TestAndroidModule;
import treehou.se.habit.ui.menu.NavigationDrawerFragment;
import treehou.se.habit.ui.servers.ServersFragment;
import treehou.se.habit.ui.settings.subsettings.GeneralSettingsFragment;
import treehou.se.habit.ui.sitemaps.PageFragment;
import treehou.se.habit.ui.sitemaps.SitemapFragment;
import treehou.se.habit.ui.sitemaps.SitemapListFragment;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Settings;

public class TestUtil {

    public static DaggerActivityTestRule<MainActivity> TestRule() {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();

        return new DaggerActivityTestRule<>(MainActivity.class, new DaggerActivityTestRule.OnBeforeActivityLaunchedListener<MainActivity>() {
            @Override
            public void beforeActivityLaunched(@NonNull Application application, @NonNull MainActivity activity) {
                ((HabitApplication) application).setTestComponent(createComponent(application));
                Context renamedContext = new RenamingDelegatingContext(application, "Testus");
                DatabaseUtil.init(renamedContext);
            }
        });
    }

    @Singleton
    @Component(modules = TestAndroidModule.class)
    public interface ApplicationComponent {
        void inject(HabitApplication application);
        void inject(MainActivity homeActivity);
        void inject(SitemapListFragment sitemapListFragment);
        void inject(SitemapFragment sitemapFragment);
        void inject(GeneralSettingsFragment fragment);
        void inject(ServersFragment serversFragment);
        void inject(NavigationDrawerFragment drawerFragment);
        void inject(Fragment drawerFragment);
        void inject(PageFragment pageFragment);
        void inject(Settings settings);
        void inject(ConnectionFactory connectionFactory);
    }

    protected static HabitApplication.ApplicationComponent createComponent(Context context){
        HabitApplication.ApplicationComponent component = DaggerHabitApplication_ApplicationComponent.builder()
                .androidModule(new TestAndroidModule(context))
                .build();
        return component;
    }
}
