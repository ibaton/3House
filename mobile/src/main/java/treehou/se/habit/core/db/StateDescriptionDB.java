package treehou.se.habit.core.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

@Table(name = "StateDescription", id = "_id")
public class StateDescriptionDB extends Model {

    private String pattern;

    private boolean readOnly;

    public StateDescriptionDB() {}

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
}
