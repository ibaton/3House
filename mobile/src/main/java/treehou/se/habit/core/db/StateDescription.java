package treehou.se.habit.core.db;

public class StateDescription {

    private String pattern;

    private boolean readOnly;

    public StateDescription() {}

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
