package treehou.se.habit.util;

import android.content.Context;

import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;

public class ConnectionFactory {

    public IServerHandler createServerHandler(OHServer server, Context context){
        return new Connector.ServerHandler(server, context);
    }
}
