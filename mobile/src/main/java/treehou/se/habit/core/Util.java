package treehou.se.habit.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Util {

    public static Gson gson = null;

    public synchronized static Gson createGsonBuilder(){

        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Widget>>() {}.getType(), new WidgetDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Sitemap>>() {}.getType(), new SitemapDeserializer());
            gsonBuilder.registerTypeAdapter(new TypeToken<List<Widget.Mapping>>() {}.getType(), new WidgetMappingDeserializer());
            gsonBuilder.registerTypeAdapter(Item.class, new ItemDeserializer());
            gson = gsonBuilder.create();
        }

        return gson;
    }
}
