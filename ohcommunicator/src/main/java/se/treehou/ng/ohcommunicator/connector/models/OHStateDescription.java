package se.treehou.ng.ohcommunicator.connector.models;

public class OHStateDescription /*extends RealmObject*/ {

    //@PrimaryKey
    private long id = 0;
    private String pattern;
    private boolean readOnly;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
