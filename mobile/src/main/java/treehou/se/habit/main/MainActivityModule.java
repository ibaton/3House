package treehou.se.habit.main;


import dagger.Module;
import dagger.Provides;
import treehou.se.habit.module.ViewModule;

@Module
public class MainActivityModule extends ViewModule<MainActivity> {
    public MainActivityModule(MainActivity activity) {
        super(activity);
    }

    @Provides
    public MainContract.View provideView() {
        return view;
    }
}
