package treehou.se.habit.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import treehou.se.habit.connector.serializers.ItemDeserializer;
import treehou.se.habit.connector.serializers.ItemListDeserializer;
import treehou.se.habit.connector.serializers.ItemStateDeserializer;
import treehou.se.habit.connector.serializers.LinkedPageDeserializer;
import treehou.se.habit.connector.serializers.SitemapListDeserializer;
import treehou.se.habit.connector.serializers.WidgetDeserializer;
import treehou.se.habit.connector.serializers.WidgetMappingDeserializer;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Sitemap;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.db.ItemDB;
import treehou.se.habit.core.db.StateDescriptionDB;

public class GsonHelper {

    public static Gson gson = null;

    private GsonHelper() {}

    public synchronized static Gson createGsonBuilder(){

        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Widget>>() {}.getType(), new WidgetDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Sitemap>>() {}.getType(), new SitemapListDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Widget.Mapping>>() {}.getType(), new WidgetMappingDeserializer());
            gsonBuilder.registerTypeAdapter(LinkedPage.class, new LinkedPageDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<ItemDB>>() {}.getType(), new ItemListDeserializer());
            gsonBuilder.registerTypeAdapter(ItemDB.class, new ItemDeserializer());
            gsonBuilder.registerTypeAdapter(StateDescriptionDB.class, new ItemStateDeserializer());
            gson = gsonBuilder.create();
        }

        return gson;
    }
}
