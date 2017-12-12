package treehou.se.habit.ui.servers.create.scan;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityScope;
import treehou.se.habit.module.FragmentComponent;
import treehou.se.habit.module.FragmentComponentBuilder;
import treehou.se.habit.ui.servers.create.custom.ScanServersModule;

@ActivityScope
@Subcomponent(
        modules = ScanServersModule.class
)
public interface ScanServersComponent extends FragmentComponent<ScanServersFragment> {

    @Subcomponent.Builder
    interface Builder extends FragmentComponentBuilder<ScanServersModule, ScanServersComponent> {
    }
}