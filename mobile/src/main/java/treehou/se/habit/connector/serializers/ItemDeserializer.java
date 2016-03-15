package treehou.se.habit.connector.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import se.treehou.ng.ohcommunicator.core.OHItemWrapper;
import se.treehou.ng.ohcommunicator.core.OHStateDescriptionWrapper;

public class ItemDeserializer implements JsonDeserializer<OHItemWrapper>, JsonSerializer<OHItemWrapper> {

    private static final String TAG = "ItemDeserializer";

    @Override
    public OHItemWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        OHItemWrapper item = new OHItemWrapper();

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

        if(jObject.has("stateDescription")) {
            item.setStateDescription(context.<OHStateDescriptionWrapper>deserialize(jObject.get("stateDescription"), OHStateDescriptionWrapper.class));
        }

        return item;
    }

    @Override
    public JsonElement serialize(OHItemWrapper src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject object = new JsonObject();
        object.addProperty("type", src.getType());
        object.addProperty("name", src.getName());
        object.addProperty("state", src.getState());
        object.addProperty("link", src.getLink());

        if(src.getStateDescription() != null) {
            object.add("stateDescription", context.serialize(src.getStateDescription(), OHStateDescriptionWrapper.class));
        }

        return object;
    }
}

