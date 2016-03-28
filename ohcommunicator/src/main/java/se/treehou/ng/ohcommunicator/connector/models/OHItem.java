package se.treehou.ng.ohcommunicator.connector.models;

public class OHItem {

    public static final String TYPE_SWITCH = "SwitchItem";
    public static final String TYPE_STRING = "StringItem";
    public static final String TYPE_COLOR = "ColorItem";
    public static final String TYPE_NUMBER = "NumberItem";
    public static final String TYPE_CONTACT = "ContactItem";
    public static final String TYPE_ROLLERSHUTTER = "RollershutterItem";
    public static final String TYPE_GROUP = "GroupItem";
    public static final String TYPE_DIMMER = "DimmerItem";

    private long id;

    private OHServer server;
    private String type;
    private String name;
    private String link;
    private String state;
    private OHStateDescription stateDescription;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OHServer getServer() {
        return server;
    }

    public void setServer(OHServer server) {
        this.server = server;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public OHStateDescription getStateDescription() {
        return stateDescription;
    }

    public void setStateDescription(OHStateDescription stateDescription) {
        this.stateDescription = stateDescription;
    }

    public String getFormatedValue(){
        if(getStateDescription() != null && getStateDescription().getPattern() != null){

            String pattern = getStateDescription().getPattern();
            try {
                return String.format(pattern, Float.valueOf(getState()));
            }
            catch (Exception e){}

            try {
                return String.format(pattern, Integer.valueOf(getState()));
            }
            catch (Exception e){}

            try {
                return String.format(pattern, getState());
            }
            catch (Exception e){}
        }

        return getState();
    }

    public String printableName(){
        if(server != null) {
            return server.getDisplayName() + " \n"  + name.replaceAll("_|-", " ");
        }
        return name.replaceAll("_|-", " ");
    }

    @Override
    public String toString() {
        return printableName();
    }
}
