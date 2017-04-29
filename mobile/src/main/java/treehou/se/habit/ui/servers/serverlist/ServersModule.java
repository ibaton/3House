package treehou.se.habit.ui.servers.serverlist;


import dagger.Module;
import dagger.Provides;
import treehou.se.habit.module.ViewModule;

@Module
public class ServersModule extends ViewModule<ServersFragment> {

    public ServersModule(ServersFragment fragment) {
        super(fragment);
    }

    @Provides
    public ServersContract.View provideView() {
        return view;
    }

    @Provides
    public ServersContract.Presenter providePresenter(ServersPresenter presenter) {
        return presenter;
    }
}
