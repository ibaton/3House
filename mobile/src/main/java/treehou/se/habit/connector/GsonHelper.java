package treehou.se.habit.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import se.treehou.ng.ohcommunicator.core.OHItemWrapper;
import se.treehou.ng.ohcommunicator.core.OHLinkedPageWrapper;
import se.treehou.ng.ohcommunicator.core.OHSitemapWrapper;
import se.treehou.ng.ohcommunicator.core.OHStateDescriptionWrapper;
import se.treehou.ng.ohcommunicator.core.OHWidgetWrapper;
import se.treehou.ng.ohcommunicator.core.db.OHMapping;
import treehou.se.habit.connector.serializers.ItemDeserializer;
import treehou.se.habit.connector.serializers.ItemListDeserializer;
import treehou.se.habit.connector.serializers.ItemStateDeserializer;
import treehou.se.habit.connector.serializers.LinkedPageDeserializer;
import treehou.se.habit.connector.serializers.SitemapListDeserializer;
import treehou.se.habit.connector.serializers.WidgetDeserializer;
import treehou.se.habit.connector.serializers.WidgetMappingDeserializer;

public class GsonHelper {

    public static Gson gson = null;

    private GsonHelper() {}

    public synchronized static Gson createGsonBuilder(){

        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(new TypeToken<List<OHWidgetWrapper>>() {}.getType(), new WidgetDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<OHSitemapWrapper>>() {}.getType(), new SitemapListDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<OHMapping>>() {}.getType(), new WidgetMappingDeserializer());
            gsonBuilder.registerTypeAdapter(OHLinkedPageWrapper.class, new LinkedPageDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<OHItemWrapper>>() {}.getType(), new ItemListDeserializer());
            gsonBuilder.registerTypeAdapter(OHItemWrapper.class, new ItemDeserializer());
            gsonBuilder.registerTypeAdapter(OHStateDescriptionWrapper.class, new ItemStateDeserializer());
            gson = gsonBuilder.create();
        }

        return gson;
    }
}
