package treehou.se.habit.core.db.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;

public class ItemDB extends RealmObject {

    public static String TYPE_SWITCH = "SwitchItem";
    public static String TYPE_STRING = "StringItem";
    public static String TYPE_COLOR = "ColorItem";
    public static String TYPE_NUMBER = "NumberItem";
    public static String TYPE_CONTACT = "ContactItem";
    public static String TYPE_ROLLERSHUTTER = "RollershutterItem";
    public static String TYPE_GROUP = "GroupItem";
    public static String TYPE_DIMMER = "DimmerItem";

    @PrimaryKey
    private long id;
    private ServerDB server;
    private String type;
    private String name;
    private String link;
    private String state;
    private StateDescriptionDB stateDescription;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ServerDB getServer() {
        return server;
    }

    public void setServer(ServerDB server) {
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

    public static void save(Realm realm, ItemDB item) {
        realm.beginTransaction();
        if (item.id <= 0) {
            item.id = uniqueId(realm);
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public StateDescriptionDB getStateDescription() {
        return stateDescription;
    }

    public void setStateDescription(StateDescriptionDB stateDescription) {
        this.stateDescription = stateDescription;
    }

    public static String printableName(ItemDB itemDB) {
        if (itemDB.server != null) {
            return itemDB.server + ": " + itemDB.name;
        }
        return itemDB.name;
    }

    public static long uniqueId(Realm realm) {
        Number num = realm.where(ItemDB.class).max("id");
        if (num == null)
            return 1;
        else
            return num.longValue() + 1;
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
            return server + ": "  + name.replaceAll("_|-", " ");
        }
        return name.replaceAll("_|-", " ");
    }

    @Override
    public String toString() {
        return printableName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof ItemDB))return false;

        ItemDB item = (ItemDB) obj;
        return type.equals(item.getType()) && name.equals(item.getName());
    }

    public OHItem toGeneric(){
        OHItem item = new OHItem();
        item.setName(name);
        item.setLink(link);
        item.setState(state);
        if(getStateDescription() != null) {
            item.setStateDescription(getStateDescription().toGeneric());
        }
        if(server != null) {
            item.setServer(server.toGeneric());
        }
        item.setType(type);
        return item;
    }

    public static ItemDB load(Realm realm, long id) {
        return realm.where(ItemDB.class).equalTo("id", id).findFirst();
    }

    public static ItemDB createOrLoadFromGeneric(Realm realm, OHItem item){
        ItemDB itemDB = realm.where(ItemDB.class).equalTo("name", item.getName()).findFirst();
        if(itemDB == null){
            itemDB = new ItemDB();
            itemDB.setId(ItemDB.uniqueId(realm));
            itemDB.setServer(ServerDB.load(realm, item.getServer().getId()));
            itemDB.setType(item.getType());
            itemDB.setName(item.getName());
            itemDB.setLink(item.getLink());
            itemDB.setState(item.getState());
            itemDB = realm.copyToRealm(itemDB);
        }
        return itemDB;
    }
}
