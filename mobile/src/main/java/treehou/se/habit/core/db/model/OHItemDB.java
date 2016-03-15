package treehou.se.habit.core.db.model;

public class OHItemDB /*extends RealmObject*/ {

    public static String TYPE_SWITCH = "SwitchItem";
    public static String TYPE_STRING = "StringItem";
    public static String TYPE_COLOR = "ColorItem";
    public static String TYPE_NUMBER = "NumberItem";
    public static String TYPE_CONTACT = "ContactItem";
    public static String TYPE_ROLLERSHUTTER = "RollershutterItem";
    public static String TYPE_GROUP = "GroupItem";
    public static String TYPE_DIMMER = "DimmerItem";

    //@PrimaryKey
    private long id;

    //@Ignore
    private OHserver server;


    private String type;
    private String name;
    private String link;
    private String state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OHserver getServer() {
        return server;
    }

    public void setServer(OHserver server) {
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

    public static void save(OHItemDB item) {
        /*Realm realm = OHRealm.realm();
        realm.beginTransaction();
        if (item.id <= 0) {
            item.id = uniqueId();
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();*/
    }

    /*public OHStateDescription getStateDescription() {
        return stateDescription;
    }

    public void setStateDescription(OHStateDescription stateDescription) {
        this.stateDescription = stateDescription;
    }*/


    /* inner class ItemHolder {
        var item: List<OHItemDB>
    }*/

    public static String printableName(OHItemDB itemDB) {
        if (itemDB.server != null) {
            return itemDB.server + ": " + itemDB.name;
        }
        return itemDB.name;
    }

    public static long uniqueId() {
        /*Number num = OHRealm.realm().where(OHItemDB.class).max("id");
        if (num == null)
            return 1;
        else
            return num.longValue() + 1;*/
        return 0;
    }
}
