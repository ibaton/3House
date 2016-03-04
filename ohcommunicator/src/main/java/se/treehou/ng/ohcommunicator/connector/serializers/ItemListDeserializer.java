package se.treehou.ng.ohcommunicator.connector.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;

public class ItemListDeserializer implements JsonDeserializer<List<OHItem>> {

    private static final String TAG = "ItemDeserializer";

    @Override
    public List<OHItem> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        List<OHItem> itemList = new ArrayList<>();

        if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            for (JsonElement e : jsonArray) {
                OHItem item = context.deserialize(e, OHItem.class);
                itemList.add(item);
            }
        }
        else if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("item")) {
                return context.deserialize(jsonObject.get("item"), typeOfT);
            }
        }

        return itemList;
    }
}

