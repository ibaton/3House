package se.treehou.ng.ohcommunicator.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHMapping;
import se.treehou.ng.ohcommunicator.connector.models.OHSitemap;
import se.treehou.ng.ohcommunicator.connector.models.OHStateDescription;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.connector.serializers.ItemDeserializer;
import se.treehou.ng.ohcommunicator.connector.serializers.ItemListDeserializer;
import se.treehou.ng.ohcommunicator.connector.serializers.ItemStateDeserializer;
import se.treehou.ng.ohcommunicator.connector.serializers.LinkedPageDeserializer;
import se.treehou.ng.ohcommunicator.connector.serializers.SitemapListDeserializer;
import se.treehou.ng.ohcommunicator.connector.serializers.WidgetDeserializer;
import se.treehou.ng.ohcommunicator.connector.serializers.WidgetMappingDeserializer;

public class GsonHelper {

    public static Gson gson = null;

    private GsonHelper() {}

    public synchronized static Gson createGsonBuilder(){

        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(OHItem.class, new ItemDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<OHWidget>>() {}.getType(), new WidgetDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<OHSitemap>>() {}.getType(), new SitemapListDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<OHMapping>>() {}.getType(), new WidgetMappingDeserializer());
            gsonBuilder.registerTypeAdapter(OHLinkedPage.class, new LinkedPageDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<OHItem>>() {}.getType(), new ItemListDeserializer());
            gsonBuilder.registerTypeAdapter(OHStateDescription.class, new ItemStateDeserializer());
            gson = gsonBuilder.create();
        }

        return gson;
    }
}
