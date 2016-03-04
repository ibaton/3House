package se.treehou.ng.ohcommunicator.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import se.treehou.ng.ohcommunicator.connector.serializers.ItemDeserializer;
import se.treehou.ng.ohcommunicator.core.OHItem;

public class GsonHelper {

    public static Gson gson = null;

    private GsonHelper() {}

    public synchronized static Gson createGsonBuilder(){

        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(OHItem.class, new ItemDeserializer());
            gson = gsonBuilder.create();
        }

        return gson;
    }
}
