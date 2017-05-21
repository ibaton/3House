package treehou.se.habit.ui.menu;


public class DrawerItem {

    private String name;
    private int resource;
    @NavigationDrawerFragment.NavigationItems int value;

    public DrawerItem(String name, int resource, int value) {
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

    public @NavigationDrawerFragment.NavigationItems
    int getValue() {
        return value;
    }

    public void setValue(@NavigationDrawerFragment.NavigationItems int value) {
        this.value = value;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return name;
    }
}
