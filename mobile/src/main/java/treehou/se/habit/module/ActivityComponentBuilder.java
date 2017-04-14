package treehou.se.habit.module;

public interface ActivityComponentBuilder<M extends ViewModule, C extends ActivityComponent> {
    ActivityComponentBuilder<M, C> activityModule(M activityModule);
    C build();
}