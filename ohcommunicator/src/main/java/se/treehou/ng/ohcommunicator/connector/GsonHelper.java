package se.treehou.ng.ohcommunicator.connector;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import io.realm.RealmList;
import io.realm.RealmObject;
import se.treehou.ng.ohcommunicator.connector.serializers.ItemDeserializer;
import se.treehou.ng.ohcommunicator.core.OHItemWrapper;
import se.treehou.ng.ohcommunicator.core.db.OHSitemap;

public class GsonHelper {

    public static Gson gson = null;

    private GsonHelper() {}

    public synchronized static Gson createGsonBuilder(){

        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(OHItemWrapper.class, new ItemDeserializer());
            gson = gsonBuilder.create();
        }

        return gson;
    }

    public synchronized static Gson createRealmGsonBuilder() {
        /*return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(new TypeToken<RealmList<OHSitemap>>() {
                }.getType(), new JsonDeserializer<RealmList<OHSitemap>>() {
                    @Override
                    public RealmList<OHSitemap> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        RealmList<OHSitemap> tags = new RealmList<>();
                        JsonArray ja = json.getAsJsonArray();
                        for (JsonElement je : ja) {
                            tags.add((OHSitemap) context.deserialize(je, OHSitemap.class));
                        }
                        return tags;
                    }
                })
                .create();*/
        return null;
    }
}
