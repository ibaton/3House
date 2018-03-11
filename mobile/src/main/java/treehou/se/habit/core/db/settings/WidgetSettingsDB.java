package treehou.se.habit.core.db.settings;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import treehou.se.habit.util.Constants;

public class WidgetSettingsDB extends RealmObject {

    private static final String TAG = "WidgetSettings";
    public static final String PREF_GLOBAL = "NotificationSettings";

    public static final int NO_COLOR = -1;
    public static final int MUTED_COLOR = 0;
    public static final int LIGHT_MUTED_COLOR = 1;
    public static final int DARK_MUTED_COLOR = 2;
    public static final int VIBRANT_COLOR = 3;
    public static final int LIGHT_VIBRANT_COLOR = 4;
    public static final int DARK_VIBRANT_COLOR = 5;

    public static final int DEFAULT_TEXT_SIZE = 100;
    public static final int DEFAULT_ICON_SIZE = 100;

    @PrimaryKey
    private long id = -1;
    private int textSize = DEFAULT_TEXT_SIZE;
    private int imageBackground = NO_COLOR;
    private int iconSize = DEFAULT_ICON_SIZE;
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
        this.textSize = Math.min(Constants.INSTANCE.getMAX_TEXT_ADDON(),Math.max(Constants.INSTANCE.getMIN_TEXT_ADDON(), textSize));
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
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if(item.getId() <= 0) {
            item.setId(getUniqueId());
        }
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
    }

    public static long getUniqueId() {
        Realm realm = Realm.getDefaultInstance();
        Number num = realm.where(WidgetSettingsDB.class).max("id");
        long newId = 1;
        if (num != null) newId = num.longValue() + 1;
        realm.close();
        return newId;
    }

    public static WidgetSettingsDB loadGlobal(Realm realm){
        return loadGlobalRx(realm).blockingFirst().first();
    }

    public static Observable<RealmResults<WidgetSettingsDB>> loadGlobalRx(Realm realm){

        RealmResults<WidgetSettingsDB> result = realm.where(WidgetSettingsDB.class).findAll();
        WidgetSettingsDB widgetSettingsDB;
        if(result.size() <= 0){
            realm.beginTransaction();
            widgetSettingsDB = realm.createObject(WidgetSettingsDB.class, WidgetSettingsDB.getUniqueId());
            widgetSettingsDB.setTextSize(DEFAULT_TEXT_SIZE);
            widgetSettingsDB.setIconSize(DEFAULT_ICON_SIZE);
            realm.commitTransaction();
        }
        result = realm.where(WidgetSettingsDB.class).findAll();

        return result.asFlowable().toObservable();
    }
}
