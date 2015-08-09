package treehou.se.habit.connector.models;

import com.google.gson.annotations.SerializedName;

public class Binding {

    private String id;
    private String name;
    private String author;
    private String description;

    @SerializedName("UID")
    private String uID;

    private boolean bridge;

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getuID() {
        return uID;
    }

    public boolean isBridge() {
        return bridge;
    }
}
