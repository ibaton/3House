package treehou.se.habit.ui.control;

import treehou.se.habit.R;

/**
* Created by ibaton on 2014-11-09.
*/
public class Icon {
    private String name;
    private int value;
    private int resource;

    public Icon() {
    }

    public Icon(String name, int value, int resource) {
        this.name = name;
        this.value = value;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getResource() {
        if (resource == 0){
            return R.drawable.cell_error;
        }

        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof Icon){
            Icon icon = (Icon) o;
            return icon.getValue() == getValue();
        }

        return super.equals(o);
    }
}
