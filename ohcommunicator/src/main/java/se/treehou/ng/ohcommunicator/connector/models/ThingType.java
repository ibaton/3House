package se.treehou.ng.ohcommunicator.connector.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ThingType {

    private List<Channel> channels = new ArrayList<>();
    private String description;
    private String label;

    @SerializedName("UID")
    private String uID;

    private boolean bridge;

    public List<Channel> getChannels() {
        return channels;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public String getuID() {
        return uID;
    }

    public boolean isBridge() {
        return bridge;
    }
}
