package treehou.se.habit.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by ibaton on 2014-10-18.
 */
public class Util {

    public static Gson createGsonBuilder(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(new TypeToken<List<Widget>>() {}.getType(), new WidgetDeserializer());
        gsonBuilder.registerTypeAdapter(new TypeToken<List<Sitemap>>() {}.getType(), new SitemapDeserializer());
        gsonBuilder.registerTypeAdapter(new TypeToken<List<Widget.Mapping>>() {}.getType(), new WidgetMappingDeserializer());
        gsonBuilder.registerTypeAdapter(Item.class, new ItemDeserializer());
        return gsonBuilder.create();
    }
}
