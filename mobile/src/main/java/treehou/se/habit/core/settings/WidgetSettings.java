package treehou.se.habit.core.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import treehou.se.habit.Constants;

/**
 * Created by ibaton on 2015-01-24.
 */
@Table(name = "WidgetSettings")
public class WidgetSettings extends Model {

    private static final String TAG = "WidgetSettings";
    public static final String PREF_GLOBAL = "NotificationSettings";

    public static final int MUTED_COLOR = 0;
    public static final int LIGHT_MUTED_COLOR = 1;
    public static final int DARK_MUTED_COLOR = 2;
    public static final int VIBRANT_COLOR = 3;
    public static final int LIGHT_VIBRANT_COLOR = 4;
    public static final int DARK_VIBRANT_COLOR = 5;

    /*public static final String IMAGE_ = "NotificationSettings";
    public static final String PREF_GLOBAL = "NotificationSettings";
    public static final String PREF_GLOBAL = "NotificationSettings";
    public static final String PREF_GLOBAL = "NotificationSettings";
    public static final String PREF_GLOBAL = "NotificationSettings";*/

    @Column(name = "textSize")
    private int textSize;

    @Column(name = "imageBackground")
    private int imageBackground;

    @Column(name = "iconSize")
    private int iconSize = 100;

    @Column(name = "compressedSingleButton")
    private boolean compressedSingleButton = true;

    @Column(name = "compressedSlider")
    private boolean compressedSlider = true;

    public WidgetSettings() {
        super();
        textSize = Constants.DEFAULT_TEXT_ADDON;
        imageBackground = DARK_MUTED_COLOR;
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
        save();
    }

    public boolean isCompressedSlider() {
        return compressedSlider;
    }

    public void setCompressedSlider(boolean compressedSingleButton) {
        this.compressedSlider = compressedSingleButton;
        save();
    }

    public static WidgetSettings loadGlobal(Context context){

        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERENCE_SERVER, Context.MODE_PRIVATE);
        long id = preferences.getLong(PREF_GLOBAL,-1);

        WidgetSettings notificationSettings = null;

        if(id != -1) {
            notificationSettings = WidgetSettings.load(WidgetSettings.class, id);
        }

        if(notificationSettings == null) {
            notificationSettings = new WidgetSettings();
            notificationSettings.save();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(PREF_GLOBAL, notificationSettings.getId());
            editor.apply();
        }

        return notificationSettings;
    }
}
