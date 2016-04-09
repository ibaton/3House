package se.treehou.ng.ohcommunicator.connector.serializers;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;

public class LinkedPageDeserializer implements JsonDeserializer<OHLinkedPage> {

    private static final String TAG = "LinkedPageDeserializer";

    @Override
    public OHLinkedPage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if(json.isJsonObject()) {
            JsonObject jObject = json.getAsJsonObject();

            OHLinkedPage linkedPage = new OHLinkedPage();

            if(jObject.has("id")) {
                linkedPage.setId(jObject.get("id").getAsString());
            }

            if(jObject.has("title")) {
                linkedPage.setTitle(jObject.get("title").getAsString());
            }

            if(jObject.has("link")) {
                linkedPage.setLink(jObject.get("link").getAsString());
            }

            if(jObject.has("leaf")) {
                linkedPage.setLeaf(jObject.get("leaf").getAsBoolean());
            }

            Log.d(TAG, "No problems go on " + jObject.has("widgets") + " " + jObject.isJsonObject());

            JsonElement jWidgets = null;
            if(jObject.has("widgets")) {
                jWidgets = jObject.get("widgets");
            } else if(jObject.has("widget")) {
                jWidgets = jObject.get("widget");
            }

            if(jWidgets != null) {
                linkedPage.setWidgets(context.<List<OHWidget>>deserialize(jWidgets, new TypeToken<List<OHWidget>>() {}.getType()));
            }

            return linkedPage;
        }

        Log.d(TAG, "Failed to parse correctly " + json);
        return null;
    }
}
