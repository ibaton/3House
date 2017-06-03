package treehou.se.habit.ui.colorpicker;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.RxPresenter;
import treehou.se.habit.ui.sitemaps.page.PageContract;
import treehou.se.habit.util.ConnectionFactory;

public class LightPresenter extends RxPresenter implements LightContract.Presenter {

    private static final String TAG = LightPresenter.class.getSimpleName();

    private ConnectionFactory connectionFactory;
    private Context context;
    private Bundle args;
    private Realm realm;
    private ServerDB serverDb;
    private OHServer server;

    @Inject
    public LightPresenter(Context context, Realm realm, @Named("arguments") Bundle args, ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.context = context;
        this.args = args;
        this.realm = realm;
    }

    @Override
    public void load(Bundle savedData) {
        super.load(savedData);

        long serverId = args.getLong(PageContract.ARG_SERVER);
        serverDb = ServerDB.load(realm, serverId);
        server = serverDb.toGeneric();
    }

    @Override
    public void setHSV(OHItem item, int hue, int saturation, int value) {
        IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
        Log.d(TAG, "Color changed to " + String.format("%d,%d,%d", hue, saturation, value));
        if (value > 5) {
            serverHandler.sendCommand(item.getName(), String.format(Locale.getDefault(), Constants.COMMAND_COLOR, hue, saturation, value));
        } else {
            serverHandler.sendCommand(item.getName(), Constants.COMMAND_OFF);
        }
    }
}
