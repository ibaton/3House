package treehou.se.habit.core.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import treehou.se.habit.core.Item;

/**
* Created by ibaton on 2014-10-18.
*/
public class ItemSerializer implements JsonSerializer<Item> {

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

