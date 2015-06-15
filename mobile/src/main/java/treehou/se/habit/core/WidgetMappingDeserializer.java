package treehou.se.habit.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WidgetMappingDeserializer implements JsonDeserializer<List<Widget.Mapping>> {

    private static final String TAG = "WidgetMappingDeserializer";

    @Override
    public List<Widget.Mapping> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        List mapping = new ArrayList();

        if(json.isJsonObject()) {
            Widget.Mapping entry = context.deserialize(json.getAsJsonObject(), Widget.Mapping.class);
            mapping.add(entry);
        }else if(json.isJsonArray()){
            JsonArray jWidgets = json.getAsJsonArray();
            for(JsonElement e : jWidgets){
                Widget.Mapping entry = context.deserialize(e, Widget.Mapping.class);
                mapping.add(entry);
            }
        }

        return mapping;
    }
}
