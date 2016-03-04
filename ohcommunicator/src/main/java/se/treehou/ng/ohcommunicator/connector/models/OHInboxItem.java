package se.treehou.ng.ohcommunicator.connector.models;

import java.util.Map;

public class OHInboxItem {

    public static final String FLAG_NEW = "NEW";
    public static final String FLAG_IGNORED = "IGNORED";

    private String flag;
    private String label;
    private Map<String, String> properties;
    private String thingUID;

    public OHInboxItem() {}

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isIgnored(){
        return FLAG_IGNORED.equals(flag);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setThingUID(String thingUID) {
        this.thingUID = thingUID;
    }

    public String getThingUID() {
        return thingUID;
    }
}