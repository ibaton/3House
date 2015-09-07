package treehou.se.habit.core.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Items")
public class ItemDB extends Model {

    public static final String TYPE_SWITCH          = "SwitchItem";
    public static final String TYPE_STRING          = "StringItem";
    public static final String TYPE_COLOR           = "ColorItem";
    public static final String TYPE_NUMBER          = "NumberItem";
    public static final String TYPE_CONTACT         = "ContactItem";
    public static final String TYPE_ROLLERSHUTTER   = "RollershutterItem";
    public static final String TYPE_GROUP           = "GroupItem";
    public static final String TYPE_DIMMER          = "DimmerItem";

    @Column(name = "Server", onDelete = Column.ForeignKeyAction.CASCADE)
    private ServerDB server;

    @Column(name = "type")
    private String type = "";

    @Column(name = "name")
    private String name = "";

    @Column(name = "link")
    private String link = "";

    @Column(name = "state")
    private String state = "";

    private StateDescription stateDescription;

    public ItemDB() {}

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

    public StateDescription getStateDescription() {
        return stateDescription;
    }

    public void setStateDescription(StateDescription stateDescription) {
        this.stateDescription = stateDescription;
    }

    public class ItemHolder{
        public List<ItemDB> item;
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
}
