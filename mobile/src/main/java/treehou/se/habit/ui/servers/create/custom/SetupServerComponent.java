package treehou.se.habit.ui.servers.create.custom;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityScope;
import treehou.se.habit.module.FragmentComponent;
import treehou.se.habit.module.FragmentComponentBuilder;

@ActivityScope
@Subcomponent(
        modules = SetupServerModule.class
)
public interface SetupServerComponent extends FragmentComponent<SetupServerFragment> {

    @Subcomponent.Builder
    interface Builder extends FragmentComponentBuilder<SetupServerModule, SetupServerComponent> {
    }
}