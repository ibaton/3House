package treehou.se.habit.ui.servers.serverlist;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityScope;
import treehou.se.habit.module.FragmentComponent;
import treehou.se.habit.module.FragmentComponentBuilder;

@ActivityScope
@Subcomponent(
        modules = ServersModule.class
)
public interface ServersComponent extends FragmentComponent<ServersFragment> {

    @Subcomponent.Builder
    interface Builder extends FragmentComponentBuilder<ServersModule, ServersComponent> {
    }
}