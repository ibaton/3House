package treehou.se.habit.ui.servers.create;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityComponent;
import treehou.se.habit.module.ActivityComponentBuilder;
import treehou.se.habit.module.ActivityScope;

@ActivityScope
@Subcomponent(
        modules = CreateServerModule.class
)
public interface CreateServerActivityComponent extends ActivityComponent<CreateServerActivity> {

    @Subcomponent.Builder
    interface Builder extends ActivityComponentBuilder<CreateServerModule, CreateServerActivityComponent> {
    }
}