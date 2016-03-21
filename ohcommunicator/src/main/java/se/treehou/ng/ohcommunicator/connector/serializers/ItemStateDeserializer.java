package se.treehou.ng.ohcommunicator.connector.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import se.treehou.ng.ohcommunicator.connector.models.OHStateDescription;

public class ItemStateDeserializer implements JsonDeserializer<OHStateDescription>, JsonSerializer<OHStateDescription> {

    private static final String TAG = "ItemDeserializer";

    @Override
    public OHStateDescription deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        OHStateDescription stateDescription = new OHStateDescription();

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
    public JsonElement serialize(OHStateDescription src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject object = new JsonObject();
        object.addProperty("pattern", src.getPattern());
        object.addProperty("readOnly", src.isReadOnly());

        return object;
    }
}

