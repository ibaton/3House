package treehou.se.habit.core.wrappers.settings;

import se.treehou.ng.ohcommunicator.core.db.OHRealm;
import treehou.se.habit.Constants;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;

public class WidgetSettings {

    private static final String TAG = "WidgetSettings";
    public static final String PREF_GLOBAL = "NotificationSettings";

    public static final int MUTED_COLOR = 0;
    public static final int LIGHT_MUTED_COLOR = 1;
    public static final int DARK_MUTED_COLOR = 2;
    public static final int VIBRANT_COLOR = 3;
    public static final int LIGHT_VIBRANT_COLOR = 4;
    public static final int DARK_VIBRANT_COLOR = 5;

    private WidgetSettingsDB widgetSettingsDB;

    public WidgetSettings() {
        super();
        widgetSettingsDB = new WidgetSettingsDB();
        widgetSettingsDB.setTextSize(Constants.DEFAULT_TEXT_ADDON);
        widgetSettingsDB.setImageBackground(DARK_MUTED_COLOR);
    }

    public WidgetSettings(WidgetSettingsDB widgetSettingsDB) {
        this.widgetSettingsDB = widgetSettingsDB;
    }

    public WidgetSettingsDB getWidgetSettingsDB() {
        return widgetSettingsDB;
    }

    public void setWidgetSettingsDB(WidgetSettingsDB widgetSettingsDB) {
        this.widgetSettingsDB = widgetSettingsDB;
    }

    public int getTextSize() {
        return widgetSettingsDB.getTextSize();
    }

    public void setTextSize(int textSize) {
        widgetSettingsDB.setTextSize(Math.min(Constants.MAX_TEXT_ADDON,Math.max(Constants.MIN_TEXT_ADDON, textSize)));
    }

    public int getIconSize() {
        return widgetSettingsDB.getIconSize();
    }

    public void setIconSize(int iconSize) {
        widgetSettingsDB.setIconSize(iconSize);
    }

    public int getImageBackground() {
        return widgetSettingsDB.getImageBackground();
    }

    public void setImageBackground(int imageBackground) {
        widgetSettingsDB.setImageBackground(imageBackground);
    }

    public boolean isCompressedSingleButton() {
        return widgetSettingsDB.isCompressedSingleButton();
    }

    public void setCompressedSingleButton(boolean compressedSingleButton) {
        widgetSettingsDB.setCompressedSlider(compressedSingleButton);
    }

    public boolean isCompressedSlider() {
        return widgetSettingsDB.isCompressedSlider();
    }

    public void setCompressedSlider(boolean compressedSingleButton) {
        widgetSettingsDB.setCompressedSingleButton(compressedSingleButton);
    }

    public static WidgetSettings loadGlobal(){
        return new WidgetSettings(OHRealm.realm().where(WidgetSettingsDB.class).findFirst());
    }
}
