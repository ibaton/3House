package se.treehou.ng.ohcommunicator.connector.models;

import java.util.List;

public class OHBinding {

    private String id;
    private String name;
    private String author;
    private String description;
    private List<ThingType> thingTypes;

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

    public List<ThingType> getThingTypes() {
        return thingTypes;
    }
}
