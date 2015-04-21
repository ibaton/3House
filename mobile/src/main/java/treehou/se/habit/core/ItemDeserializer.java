package treehou.se.habit.core;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
* Created by ibaton on 2014-10-18.
*/
public class ItemDeserializer implements JsonDeserializer<Item>, JsonSerializer<Item> {

    private static final String TAG = "ItemDeserializer";

    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        Item item = new Item();

        JsonObject jObject = json.getAsJsonObject();
        if(jObject.has("type")) {
            item.setType(jObject.get("type").getAsString());
        }

        if(jObject.has("name")) {
            item.setName(jObject.get("name").getAsString());
        }

        if(jObject.has("state")) {
            item.setState(jObject.get("state").getAsString());
        }

        if(jObject.has("link")) {
            item.setLink(jObject.get("link").getAsString());
        }

        return item;
    }

    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject object = new JsonObject();
        object.addProperty("type", src.getType());
        object.addProperty("name", src.getName());
        object.addProperty("state", src.getState());
        object.addProperty("link", src.getLink());

        return object;
    }
}

