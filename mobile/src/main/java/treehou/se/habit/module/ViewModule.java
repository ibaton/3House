package treehou.se.habit.module;


import dagger.Module;
import dagger.Provides;

@Module
public abstract class ViewModule<T> {
    protected final T view;

    public ViewModule(T view) {
        this.view = view;
    }

    @Provides
    @ActivityScope
    public T provideActivity() {
        return view;
    }
}