package treehou.se.habit.core.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.db.ItemDB;

public class WidgetDeserializer implements JsonDeserializer<List<Widget>> {

    private static final String TAG = "WidgetDeserializer";

    private static final String KEY_LINKED_PAGE = "linkedPage";

    private Widget deserializeWidget(JsonObject jObject, JsonDeserializationContext context){

        Widget widget = new Widget();
        widget.setType(jObject.get("type").getAsString());
        widget.setLabel(jObject.get("label").getAsString());
        widget.setWidgetId(jObject.get("widgetId").getAsString());
        widget.setIcon(jObject.get("icon").getAsString());

        if(jObject.has("period")) {
            widget.setPeriod(jObject.get("period").getAsString());
        }

        if(jObject.has("url")) {
            widget.setUrl(jObject.get("url").getAsString());
        }

        if(jObject.has("item")){
            ItemDB item = context.deserialize(jObject.get("item").getAsJsonObject(), ItemDB.class);
            widget.setItem(item);
        }

        if(jObject.has(KEY_LINKED_PAGE)){
            JsonObject jLinkedPage = jObject.getAsJsonObject(KEY_LINKED_PAGE);
            LinkedPage linkedPage = context.deserialize(jLinkedPage, LinkedPage.class);
            widget.setLinkedPage(linkedPage);
        }

        JsonElement jWidgetElement = jObject.get("widget");
        List<Widget> widgets = context.deserialize(jWidgetElement, new TypeToken<List<Widget>>() {}.getType());
        widget.setWidget(widgets);

        JsonElement jMappingElement = jObject.get("mapping");
        List<Widget.Mapping> mapping = context.deserialize(jMappingElement, new TypeToken<List<Widget.Mapping>>() {}.getType());
        widget.setMapping(mapping);

        return widget;
    }

    @Override
    public List<Widget> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){

        List widgets = new ArrayList();

        if(json.isJsonObject()) {
            Widget widget = deserializeWidget(json.getAsJsonObject(), context);
            widgets.add(widget);
        }else if(json.isJsonArray()){
            JsonArray jWidgets = json.getAsJsonArray();
            for(JsonElement e : jWidgets){
                Widget widget = deserializeWidget(e.getAsJsonObject(), context);
                widgets.add(widget);
            }
        }

        return widgets;
    }
}
