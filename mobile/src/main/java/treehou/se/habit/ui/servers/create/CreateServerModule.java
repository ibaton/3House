package treehou.se.habit.ui.servers.create;


import dagger.Module;
import dagger.Provides;
import treehou.se.habit.module.ViewModule;

@Module
public class CreateServerModule extends ViewModule<CreateServerActivity> {
    public CreateServerModule(CreateServerActivity activity) {
        super(activity);
    }

    @Provides
    public CreateServerContract.View provideView() {
        return view;
    }
}
