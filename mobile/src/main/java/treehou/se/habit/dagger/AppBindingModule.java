package treehou.se.habit.dagger;


import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;
import treehou.se.habit.dagger.activity.CreateServerActivityComponent;
import treehou.se.habit.dagger.activity.MainActivityComponent;
import treehou.se.habit.dagger.fragment.CreateMyOpenhabComponent;
import treehou.se.habit.dagger.fragment.GeneralSettingsComponent;
import treehou.se.habit.dagger.fragment.LightComponent;
import treehou.se.habit.dagger.fragment.PageComponent;
import treehou.se.habit.dagger.fragment.ScanServersComponent;
import treehou.se.habit.dagger.fragment.ServerMenuComponent;
import treehou.se.habit.dagger.fragment.ServersComponent;
import treehou.se.habit.dagger.fragment.SettingsComponent;
import treehou.se.habit.dagger.fragment.SetupServerComponent;
import treehou.se.habit.dagger.fragment.SitemapComponent;
import treehou.se.habit.dagger.fragment.SitemapListComponent;
import treehou.se.habit.dagger.fragment.SitemapSelectComponent;
import treehou.se.habit.dagger.fragment.SitemapSettingsComponent;
import treehou.se.habit.ui.colorpicker.LightFragment;
import treehou.se.habit.ui.main.MainActivity;
import treehou.se.habit.ui.servers.ServerMenuFragment;
import treehou.se.habit.ui.servers.create.CreateServerActivity;
import treehou.se.habit.ui.servers.create.custom.SetupServerFragment;
import treehou.se.habit.ui.servers.create.myopenhab.CreateMyOpenhabFragment;
import treehou.se.habit.ui.servers.create.scan.ScanServersFragment;
import treehou.se.habit.ui.servers.serverlist.ServersFragment;
import treehou.se.habit.ui.servers.sitemaps.list.SitemapSelectFragment;
import treehou.se.habit.ui.servers.sitemaps.sitemapsettings.SitemapSettingsFragment;
import treehou.se.habit.ui.settings.SettingsFragment;
import treehou.se.habit.ui.settings.subsettings.general.GeneralSettingsFragment;
import treehou.se.habit.ui.sitemaps.page.PageFragment;
import treehou.se.habit.ui.sitemaps.sitemap.SitemapFragment;
import treehou.se.habit.ui.sitemaps.sitemaplist.SitemapListFragment;

@Module(
        subcomponents = {
                MainActivityComponent.class,
                CreateServerActivityComponent.class,
                CreateMyOpenhabComponent.class,
                SetupServerComponent.class,
                SettingsComponent.class,
                ServerMenuComponent.class,
                GeneralSettingsComponent.class,
                SitemapComponent.class,
                ServersComponent.class,
                SitemapSettingsComponent.class,
                SitemapSelectComponent.class,
                PageComponent.class,
                SitemapListComponent.class,
                ScanServersComponent.class,
                LightComponent.class
        })
public abstract class AppBindingModule {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    public abstract ActivityComponentBuilder mainActivityComponentBuilder(MainActivityComponent.Builder impl);

    @Binds
    @IntoMap
    @ActivityKey(CreateServerActivity.class)
    public abstract ActivityComponentBuilder createServerActivityComponentBuilder(CreateServerActivityComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(ServerMenuFragment.class)
    public abstract FragmentComponentBuilder serverMenuComponentBuilder(ServerMenuComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(ScanServersFragment.class)
    public abstract FragmentComponentBuilder scanServersFragmentComponentBuilder(ScanServersComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(SitemapListFragment.class)
    public abstract FragmentComponentBuilder sitemapListComponentBuilder(SitemapListComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(SitemapFragment.class)
    public abstract FragmentComponentBuilder sitemapComponentBuilder(SitemapComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(CreateMyOpenhabFragment.class)
    public abstract FragmentComponentBuilder createMyOpenhabFragmentBuilder(CreateMyOpenhabComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(SetupServerFragment.class)
    public abstract FragmentComponentBuilder createSetupServerFragmentBuilder(SetupServerComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(LightFragment.class)
    public abstract FragmentComponentBuilder lightComponentBuilder(LightComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(ServersFragment.class)
    public abstract FragmentComponentBuilder serversComponentBuilder(ServersComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(SettingsFragment.class)
    public abstract FragmentComponentBuilder settingsComponentBuilder(SettingsComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(SitemapSelectFragment.class)
    public abstract FragmentComponentBuilder sitemapSelectComponentBuilder(SitemapSelectComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(SitemapSettingsFragment.class)
    public abstract FragmentComponentBuilder sitemapSettingsComponentBuilder(SitemapSettingsComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(GeneralSettingsFragment.class)
    public abstract FragmentComponentBuilder generalSettingsComponentBuilder(GeneralSettingsComponent.Builder impl);

    @Binds
    @IntoMap
    @FragmentKey(PageFragment.class)
    public abstract FragmentComponentBuilder pageComponentBuilder(PageComponent.Builder impl);
}
