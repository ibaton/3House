package treehou.se.habit.connector.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Channel {

    private String description;
    private String id;
    private String label;
    private List<String> tags;
    private String category;
    private boolean advance;

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

}
