package treehou.se.habit.ui.colorpicker;


import dagger.Subcomponent;
import treehou.se.habit.module.ActivityScope;
import treehou.se.habit.module.FragmentComponent;
import treehou.se.habit.module.FragmentComponentBuilder;

@ActivityScope
@Subcomponent(
        modules = LightModule.class
)
public interface LightComponent extends FragmentComponent<LightFragment> {

    @Subcomponent.Builder
    interface Builder extends FragmentComponentBuilder<LightModule, LightComponent> {

    }
}
