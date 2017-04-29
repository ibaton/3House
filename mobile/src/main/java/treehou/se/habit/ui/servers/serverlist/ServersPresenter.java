package treehou.se.habit.ui.servers.serverlist;

import javax.inject.Inject;

import treehou.se.habit.module.RxPresenter;
import treehou.se.habit.ui.settings.SettingsContract;

public class ServersPresenter extends RxPresenter implements ServersContract.Presenter {

    private ServersContract.View view;

    @Inject
    public ServersPresenter(ServersContract.View view) {
        this.view = view;
    }
}
