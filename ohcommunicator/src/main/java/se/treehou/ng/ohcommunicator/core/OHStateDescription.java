package se.treehou.ng.ohcommunicator.core;

public class OHStateDescription {

    private String pattern;

    private boolean readOnly;

    public OHStateDescription() {}

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
