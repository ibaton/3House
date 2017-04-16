package treehou.se.habit.ui.settings.subsettings.general;


public class ThemeItem {

    public final int theme;
    public final String name;

    public ThemeItem(int theme, String name) {
        this.theme = theme;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
