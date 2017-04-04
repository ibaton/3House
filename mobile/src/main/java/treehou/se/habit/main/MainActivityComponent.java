package treehou.se.habit.main;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityComponent;
import treehou.se.habit.module.ActivityComponentBuilder;
import treehou.se.habit.module.ActivityScope;

@ActivityScope
@Subcomponent(
        modules = MainActivityModule.class
)
public interface MainActivityComponent extends ActivityComponent<MainActivity> {

    @Subcomponent.Builder
    interface Builder extends ActivityComponentBuilder<MainActivityModule, MainActivityComponent> {
    }
}