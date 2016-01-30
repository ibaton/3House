package se.treehou.ng.ohcommunicator.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {

    public static Gson gson = null;

    private GsonHelper() {}

    public synchronized static Gson createGsonBuilder(){

        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        }

        return gson;
    }
}
