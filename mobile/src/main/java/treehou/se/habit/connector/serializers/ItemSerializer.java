package treehou.se.habit.connector.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import treehou.se.habit.core.db.ItemDB;

public class ItemSerializer implements JsonSerializer<ItemDB> {

    @Override
    public JsonElement serialize(ItemDB src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject object = new JsonObject();
        object.addProperty("type", src.getType());
        object.addProperty("name", src.getName());
        object.addProperty("state", src.getState());
        object.addProperty("link", src.getLink());

        return object;
    }
}

