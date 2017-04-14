package treehou.se.habit.module;

public interface FragmentComponentBuilder<M extends ViewModule, C extends FragmentComponent> {
    FragmentComponentBuilder<M, C> fragmentModule(M fragmentModule);
    C build();
}