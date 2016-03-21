package treehou.se.habit.core.db.model;

public class MappingDB /*extends RealmObject*/ {

    private String command;
    private String label;

    public MappingDB() {
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
