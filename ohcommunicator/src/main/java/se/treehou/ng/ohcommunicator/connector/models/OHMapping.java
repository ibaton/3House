package se.treehou.ng.ohcommunicator.connector.models;

public class OHMapping {

    private String command;
    private String label;

    public OHMapping() {
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

    @Override
    public String toString() {
        return getLabel();
    }
}
