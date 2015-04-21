package treehou.se.habit.core;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
* Created by ibaton on 2014-10-18.
*/
public class SitemapDeserializer implements JsonDeserializer<List<Sitemap>> {

    private static final String TAG = "SitemapDeserializer";

    @Override
    public List<Sitemap> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        Log.d(TAG, "Deserializing: " + json);

        List<Sitemap> sitemaps = new ArrayList<>();
        if(json.isJsonObject()) {
            Log.d(TAG, "Deserializing single sitemap");
            Sitemap sitemap = context.deserialize(json, Sitemap.class);
            sitemaps.add(sitemap);
        }else if(json.isJsonArray()){
            Log.d(TAG, "Deserializing multiple sitemap");
            JsonArray jWidgets = json.getAsJsonArray();
            for(JsonElement e : jWidgets){
                Sitemap sitemap = context.deserialize(e, Sitemap.class);
                sitemaps.add(sitemap);
            }
        }

        return sitemaps;
    }
}
