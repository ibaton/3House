package se.treehou.ng.ohcommunicator.connector.serializers;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHMapping;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;

public class WidgetDeserializer implements JsonDeserializer<List<OHWidget>> {

    private static final String TAG = "WidgetDeserializer";

    private static final String KEY_LINKED_PAGE = "linkedPage";

    private OHWidget deserializeWidget(JsonObject jObject, JsonDeserializationContext context){

        OHWidget widget = new OHWidget();
        widget.setType(jObject.get("type").getAsString());
        widget.setLabel(jObject.get("label").getAsString());
        widget.setWidgetId(jObject.get("widgetId").getAsString());
        widget.setIcon(jObject.get("icon").getAsString());

        if(jObject.has("period")) {
            widget.setPeriod(jObject.get("period").getAsString());
        }

        if(jObject.has("service")) {
            widget.setService(jObject.get("service").getAsString());
        }

        if(jObject.has("url")) {
            widget.setUrl(jObject.get("url").getAsString());
        }

        if(jObject.has("item")){
            OHItem item = context.deserialize(jObject.get("item").getAsJsonObject(), OHItem.class);
            widget.setItem(item);
        }

        if(jObject.has("minValue")){
            widget.setMinValue(jObject.get("minValue").getAsInt());
        }

        if(jObject.has("maxValue")){
            widget.setMaxValue(jObject.get("maxValue").getAsInt());
        }

        if(jObject.has("step") && jObject.get("step") != null){
            try {
                widget.setStep(jObject.get("step").getAsFloat());
            } catch (NumberFormatException nfe) {
                Log.e(TAG, "Cannot parse " + jObject.get("step").getAsString() + " as float.");
            }
        }

        if(jObject.has(KEY_LINKED_PAGE)){
            JsonObject jLinkedPage = jObject.getAsJsonObject(KEY_LINKED_PAGE);
            OHLinkedPage linkedPage = context.deserialize(jLinkedPage, OHLinkedPage.class);
            widget.setLinkedPage(linkedPage);
        }

        if(jObject.has("widget")) {
            JsonElement jWidgetElement = jObject.get("widget");
            List<OHWidget> widgets = context.deserialize(jWidgetElement, new TypeToken<List<OHWidget>>() {}.getType());
            widget.setWidget(widgets);
        }
        else if(jObject.has("widgets")) { // openhab2 compat
            JsonElement jWidgetElement = jObject.get("widgets");
            List<OHWidget> widgets = context.deserialize(jWidgetElement, new TypeToken<List<OHWidget>>() {}.getType());
            widget.setWidget(widgets);
        }

        if(jObject.has("mapping")) {
            JsonElement jMappingElement = jObject.get("mapping");
            List<OHMapping> mapping = context.deserialize(jMappingElement, new TypeToken<List<OHMapping>>(){}.getType());
            widget.setMapping(mapping);
        }

        if(jObject.has("mappings")) {
            JsonElement jMappingElement = jObject.get("mappings");
            List<OHMapping> mapping = context.deserialize(jMappingElement, new TypeToken<List<OHMapping>>(){}.getType());
            widget.setMapping(mapping);
        }


        return widget;
    }

    @Override
    public List<OHWidget> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        List<OHWidget> widgets = new ArrayList<>();

        if(json.isJsonObject()) {
            OHWidget widget = deserializeWidget(json.getAsJsonObject(), context);
            widgets.add(widget);
        }else if(json.isJsonArray()){
            JsonArray jWidgets = json.getAsJsonArray();
            for(JsonElement e : jWidgets){
                OHWidget widget = deserializeWidget(e.getAsJsonObject(), context);
                widgets.add(widget);
            }
        }

        return widgets;
    }
}
