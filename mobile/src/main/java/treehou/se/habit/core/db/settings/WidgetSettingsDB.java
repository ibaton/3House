package treehou.se.habit.core.db.settings;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import se.treehou.ng.ohcommunicator.core.db.OHRealm;
import treehou.se.habit.Constants;

public class WidgetSettingsDB extends RealmObject {

    private static final String TAG = "WidgetSettings";
    public static final String PREF_GLOBAL = "NotificationSettings";

    public static final int MUTED_COLOR = 0;
    public static final int LIGHT_MUTED_COLOR = 1;
    public static final int DARK_MUTED_COLOR = 2;
    public static final int VIBRANT_COLOR = 3;
    public static final int LIGHT_VIBRANT_COLOR = 4;
    public static final int DARK_VIBRANT_COLOR = 5;

    @PrimaryKey
    private long id = 0;
    private int textSize;
    private int imageBackground;
    private int iconSize = 100;
    private boolean compressedSingleButton = true;
    private boolean compressedSlider = true;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = Math.min(Constants.MAX_TEXT_ADDON,Math.max(Constants.MIN_TEXT_ADDON, textSize));
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }

    public int getImageBackground() {
        return imageBackground;
    }

    public void setImageBackground(int imageBackground) {
        this.imageBackground = imageBackground;
    }

    public boolean isCompressedSingleButton() {
        return compressedSingleButton;
    }

    public void setCompressedSingleButton(boolean compressedSingleButton) {
        this.compressedSingleButton = compressedSingleButton;
    }

    public boolean isCompressedSlider() {
        return compressedSlider;
    }

    public void setCompressedSlider(boolean compressedSingleButton) {
        this.compressedSlider = compressedSingleButton;
    }

    public static void save(WidgetSettingsDB item){
        Realm realm = OHRealm.realm();
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId());
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public static long getUniqueId() {
        Realm realm = OHRealm.realm();
        Number num = realm.where(WidgetSettingsDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        realm.close();
        return newId;
    }
}
