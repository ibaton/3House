package treehou.se.habit.connector.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import treehou.se.habit.core.db.StateDescription;

public class ItemStateDeserializer implements JsonDeserializer<StateDescription>, JsonSerializer<StateDescription> {

    private static final String TAG = "ItemDeserializer";

    @Override
    public StateDescription deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        StateDescription stateDescription = new StateDescription();

        JsonObject jObject = json.getAsJsonObject();
        if(jObject.has("pattern")) {
            stateDescription.setPattern(jObject.get("pattern").getAsString());
        }
        if(jObject.has("readOnly")) {
            stateDescription.setReadOnly(jObject.get("readOnly").getAsBoolean());
        }

        return stateDescription;
    }

    @Override
    public JsonElement serialize(StateDescription src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject object = new JsonObject();
        object.addProperty("pattern", src.getPattern());
        object.addProperty("readOnly", src.isReadOnly());

        return object;
    }
}

