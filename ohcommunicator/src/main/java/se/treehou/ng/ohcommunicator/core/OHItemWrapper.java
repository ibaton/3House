package se.treehou.ng.ohcommunicator.core;

import se.treehou.ng.ohcommunicator.core.db.OHItemDB;

public class OHItemWrapper {

    public static final String TYPE_SWITCH          = "SwitchItem";
    public static final String TYPE_STRING          = "StringItem";
    public static final String TYPE_COLOR           = "ColorItem";
    public static final String TYPE_NUMBER          = "NumberItem";
    public static final String TYPE_CONTACT         = "ContactItem";
    public static final String TYPE_ROLLERSHUTTER   = "RollershutterItem";
    public static final String TYPE_GROUP           = "GroupItem";
    public static final String TYPE_DIMMER          = "DimmerItem";

    private OHItemDB itemDB;

    private OHStateDescriptionWrapper stateDescription;

    public OHItemWrapper() {}

    public OHItemWrapper(OHItemDB itemDB) {
        this.itemDB = itemDB;
    }

    public long getId(){
        return itemDB.getId();
    }

    private void setId(long id){
        itemDB.setId(id);
    }

    public OHItemDB getDB() {
        return itemDB;
    }

    public void setDB(OHItemDB itemDB) {
        this.itemDB = itemDB;
    }

    public OHServerWrapper getServer() {
        return OHServerWrapper.toOH(itemDB.getServer());
    }

    public void setServer(OHServerWrapper server) {
        itemDB.setServer(server.getDB());
    }

    public String getType() {
        return itemDB.getType();
    }

    public void setType(String type) {
        itemDB.setType(type);
    }

    public String getName() {
        return itemDB.getName();
    }

    public void setName(String name) {
        itemDB.setName(name);
    }

    public String getLink() {
        return itemDB.getLink();
    }

    public void setLink(String link) {
        itemDB.setLink(link);
    }

    public String getState() {
        return itemDB.getState();
    }

    public void setState(String state) {
        itemDB.setState(state);
    }

    public OHStateDescriptionWrapper getStateDescription() {
        return stateDescription;
    }

    public void setStateDescription(OHStateDescriptionWrapper stateDescription) {
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
        if(getServer() != null) {
            return getServer() + ": "  + getName().replaceAll("_|-", " ");
        }
        return getName().replaceAll("_|-", " ");
    }

    @Override
    public String toString() {
        return printableName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof OHItemWrapper))return false;

        OHItemWrapper item = (OHItemWrapper) obj;
        return getType().equals(item.getType()) && getName().equals(item.getName());
    }

    public void save(){
        OHItemDB.save(itemDB);
    }

    public static OHItemWrapper load(long id){
        return OHItemWrapper.load(id);
    }
}
