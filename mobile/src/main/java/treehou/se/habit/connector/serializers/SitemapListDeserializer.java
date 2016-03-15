package treehou.se.habit.connector.serializers;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.core.OHSitemapWrapper;

public class SitemapListDeserializer implements JsonDeserializer<List<OHSitemapWrapper>> {

    private static final String TAG = "SitemapListDeserializer";

    @Override
    public List<OHSitemapWrapper> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        Log.d(TAG, "Deserializing: " + json);

        List<OHSitemapWrapper> sitemaps = new ArrayList<>();
        if(json.isJsonObject()) {
            Log.d(TAG, "Deserializing single sitemap");
            JsonObject jSitemaps = json.getAsJsonObject();
            if(jSitemaps.has("sitemap")) {
                if(jSitemaps.get("sitemap").isJsonObject()) {
                    JsonElement jSitemap = jSitemaps.get("sitemap");
                    sitemaps.add(context.<OHSitemapWrapper>deserialize(jSitemap, OHSitemapWrapper.class));
                    return sitemaps;
                }
                else {
                    JsonArray jSitemap = jSitemaps.get("sitemap").getAsJsonArray();
                    return context.deserialize(jSitemap, new TypeToken<List<OHSitemapWrapper>>() {}.getType());
                }
            }
            else {
                OHSitemapWrapper sitemap = context.deserialize(json, OHSitemapWrapper.class);
                sitemaps.add(sitemap);
            }
        }else if(json.isJsonArray()){
            Log.d(TAG, "Deserializing multiple sitemap");
            JsonArray jWidgets = json.getAsJsonArray();
            for(JsonElement e : jWidgets){
                OHSitemapWrapper sitemap = context.deserialize(e, OHSitemapWrapper.class);
                sitemaps.add(sitemap);
            }
        }

        return sitemaps;
    }
}
