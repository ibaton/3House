package treehou.se.habit.core.db.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import se.treehou.ng.ohcommunicator.connector.models.OHStateDescription;

public class StateDescriptionDB extends RealmObject {

    @PrimaryKey
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

    public OHStateDescription toGeneric(){
        OHStateDescription stateDescription = new OHStateDescription();
        stateDescription.setPattern(pattern);
        stateDescription.setReadOnly(readOnly);
        return stateDescription;
    }
}
