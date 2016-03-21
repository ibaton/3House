package se.treehou.ng.ohcommunicator.connector.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHMapping;

public class WidgetMappingDeserializer implements JsonDeserializer<List<OHMapping>> {

    private static final String TAG = "WidgetMappingDeserializer";

    @Override
    public List<OHMapping> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        List mapping = new ArrayList();

        if(json.isJsonObject()) {
            OHMapping entry = context.deserialize(json.getAsJsonObject(), OHMapping.class);
            mapping.add(entry);
        }else if(json.isJsonArray()){
            JsonArray jWidgets = json.getAsJsonArray();
            for(JsonElement e : jWidgets){
                OHMapping entry = context.deserialize(e, OHMapping.class);
                mapping.add(entry);
            }
        }

        return mapping;
    }
}
