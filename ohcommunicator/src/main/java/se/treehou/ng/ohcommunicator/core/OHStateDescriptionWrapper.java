package se.treehou.ng.ohcommunicator.core;

public class OHStateDescriptionWrapper {

    private String pattern;

    private boolean readOnly;

    public OHStateDescriptionWrapper() {}

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
