package treehou.se.habit.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;

import com.mattyork.colours.Colour;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.module.ApplicationComponent;
import treehou.se.habit.ui.adapter.IconAdapter;

public class Util {

    private final static Map<String, IIcon> CELL_ICON_MAP = new HashMap<>();
    private final static List<IIcon> CELL_ICONS = new ArrayList<>();
    static {
        CELL_ICONS.addAll(Arrays.asList(CommunityMaterial.Icon.values()));
        CELL_ICONS.addAll(Arrays.asList(GoogleMaterial.Icon.values()));
        CELL_ICONS.addAll(Arrays.asList(Octicons.Icon.values()));
        CELL_ICONS.addAll(Arrays.asList(FontAwesome.Icon.values()));

        for(IIcon icon : CELL_ICONS){
            CELL_ICON_MAP.put(icon.getName(), icon);
        }
    }

    public enum IconCategory {
        EMPTY, SENSORS, MEDIA, COMMANDS, ARROWS, ALL
    }

    public final static Map<IconCategory, List<IIcon>> CAT_ICONS = new HashMap<>();
    static {
        List<IIcon> sensors = new ArrayList<>();
        sensors.add(CommunityMaterial.Icon.cmd_alarm);
        sensors.add(CommunityMaterial.Icon.cmd_alarm_plus);
        sensors.add(CommunityMaterial.Icon.cmd_alert);
        sensors.add(CommunityMaterial.Icon.cmd_bell);
        sensors.add(CommunityMaterial.Icon.cmd_bell_off);
        sensors.add(CommunityMaterial.Icon.cmd_bell_ring);
        sensors.add(CommunityMaterial.Icon.cmd_brightness_5);
        sensors.add(CommunityMaterial.Icon.cmd_brightness_6);
        sensors.add(CommunityMaterial.Icon.cmd_brightness_7);
        CAT_ICONS.put(IconCategory.SENSORS, sensors);

        List<IIcon> arrows = new ArrayList<>();
        arrows.add(CommunityMaterial.Icon.cmd_arrow_down);
        arrows.add(CommunityMaterial.Icon.cmd_arrow_down_bold);
        arrows.add(CommunityMaterial.Icon.cmd_chevron_down);
        arrows.add(CommunityMaterial.Icon.cmd_arrow_up);
        arrows.add(CommunityMaterial.Icon.cmd_arrow_up_bold);
        arrows.add(CommunityMaterial.Icon.cmd_chevron_up);
        arrows.add(CommunityMaterial.Icon.cmd_arrow_left);
        arrows.add(CommunityMaterial.Icon.cmd_arrow_left_bold);
        arrows.add(CommunityMaterial.Icon.cmd_chevron_left);
        arrows.add(CommunityMaterial.Icon.cmd_arrow_right);
        arrows.add(CommunityMaterial.Icon.cmd_arrow_right_bold);
        arrows.add(CommunityMaterial.Icon.cmd_chevron_right);
        CAT_ICONS.put(IconCategory.ARROWS, arrows);

        List<IIcon> media = new ArrayList<>();
        media.add(CommunityMaterial.Icon.cmd_play);
        media.add(CommunityMaterial.Icon.cmd_pause);
        media.add(CommunityMaterial.Icon.cmd_stop);
        media.add(CommunityMaterial.Icon.cmd_forward);
        media.add(CommunityMaterial.Icon.cmd_rewind);
        media.add(CommunityMaterial.Icon.cmd_skip_next);
        media.add(CommunityMaterial.Icon.cmd_skip_previous);
        media.add(CommunityMaterial.Icon.cmd_microphone_off);
        media.add(CommunityMaterial.Icon.cmd_microphone);
        media.add(CommunityMaterial.Icon.cmd_microphone_off);
        media.add(CommunityMaterial.Icon.cmd_volume_off);
        media.add(CommunityMaterial.Icon.cmd_volume_low);
        media.add(CommunityMaterial.Icon.cmd_volume_medium);
        media.add(CommunityMaterial.Icon.cmd_volume_high);
        CAT_ICONS.put(IconCategory.MEDIA, media);

        List<IIcon> commands = new ArrayList<>();
        commands.add(CommunityMaterial.Icon.cmd_airplane);
        commands.add(CommunityMaterial.Icon.cmd_airplane_off);
        commands.add(CommunityMaterial.Icon.cmd_bell_ring);
        commands.add(CommunityMaterial.Icon.cmd_lock);
        commands.add(CommunityMaterial.Icon.cmd_lock_open);
        commands.add(CommunityMaterial.Icon.cmd_power);
        commands.add(CommunityMaterial.Icon.cmd_coffee);
        commands.add(CommunityMaterial.Icon.cmd_beer);
        CAT_ICONS.put(IconCategory.COMMANDS, commands);

        CAT_ICONS.put(IconCategory.ALL, getIcons());
    }

    /**
     * Get a shallow copy of all available icons.
     *
     * @return list of icons.
     */
    public static List<IIcon> getIcons(){
        return new ArrayList<>(CELL_ICONS);
    }

    /**
     * Get icon from icon name.
     *
     * @param value name of icon
     * @return Icon coresponding to the name. Null if no match found
     */
    public static IIcon getIcon(String value){
        IIcon icon = CELL_ICON_MAP.get(value);

        return icon;
    }

    public static ApplicationComponent getApplicationComponent(Service service) {
        return ((HabitApplication) service.getApplication()).component();
    }

    public static ApplicationComponent getApplicationComponent(Activity activity) {
        return ((HabitApplication) activity.getApplication()).component();
    }

    public static ApplicationComponent getApplicationComponent(Fragment fragment) {
        return ((HabitApplication) fragment.getContext().getApplicationContext()).component();
    }

    public static ApplicationComponent getApplicationComponent(Context context) {
        return ((HabitApplication) context.getApplicationContext()).component();
    }

    /**
     * Create label text correctly formated to display values.
     *
     * @param context calling context.
     * @param name the label.
     * @return formated label
     */
    public static Spanned createLabel(Context context, String name){
        String nameSpaned = name.replaceAll("(\\[)(.*)(\\])", "<font color='"+ String.format("#%06X", 0xFFFFFF & ContextCompat.getColor(context, R.color.colorAccent)) +"'>$2</font>");
        return Html.fromHtml(nameSpaned);
    }

    /**
     * Get bitmap for icon based on icon name
     *
     * @param context
     * @param value icon name
     * @return bitmap for icon. Null if no bitmap found
     */
    public static Bitmap getIconBitmap(Context context, String value){
        IconicsDrawable drawable = getIconDrawable(context, value);
        if(drawable == null){
            return null;
        }

        return drawable.toBitmap();
    }

    /**
     * TODO move to dialog fragment
     * Create select icon dialog.
     *
     * @param context
     * @param listener triggers when icon is selected
     */
    public static void crateIconSelected(Context context, final IconAdapter.IconSelectListener listener){
        final AppCompatDialog dialog = new AppCompatDialog(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.icon_picker, null);
        RecyclerView iconList = (RecyclerView) view.findViewById(R.id.list);
        iconList.setItemAnimator(new DefaultItemAnimator());
        iconList.setLayoutManager(new GridLayoutManager(context, 4));
        IconAdapter adapter = new IconAdapter(context);

        adapter.setIconSelectListener(new IconAdapter.IconSelectListener() {
            @Override
            public void iconSelected(IIcon icon) {
                listener.iconSelected(icon);
                dialog.dismiss();
            }
        });
        iconList.setAdapter(adapter);

        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * Get drawable for icon.
     *
     * @param context
     * @param value name of icon
     * @return drawable for icon. Null if not found
     */
    public static IconicsDrawable getIconDrawable(Context context, String value){
        IIcon icon = Util.getIcon(value);
        if(icon == null){
            return null;
        }
        IconicsDrawable drawableIcon = new IconicsDrawable(context, icon).color(Color.BLACK).sizeDp(24);

        return drawableIcon;
    }

    public static int getBackground(Context context, Bitmap bitmap){
        return getBackground(context, bitmap, WidgetSettingsDB.NO_COLOR);
    }

    public static int getBackground(Context context, Bitmap bitmap, int type){
        if(type == WidgetSettingsDB.MUTED_COLOR) {
            return Palette.from(bitmap).generate().getMutedColor(context.getResources().getColor(R.color.image_background));
        }else if(type == WidgetSettingsDB.LIGHT_MUTED_COLOR) {
            return Palette.from(bitmap).generate().getLightMutedColor(context.getResources().getColor(R.color.image_background));
        }else if(type == WidgetSettingsDB.DARK_MUTED_COLOR) {
            return Palette.from(bitmap).generate().getDarkMutedColor(context.getResources().getColor(R.color.image_background));
        }else if(type == WidgetSettingsDB.VIBRANT_COLOR) {
            return Palette.from(bitmap).generate().getVibrantColor(context.getResources().getColor(R.color.image_background));
        }else if(type == WidgetSettingsDB.LIGHT_VIBRANT_COLOR) {
            return Palette.from(bitmap).generate().getLightVibrantColor(context.getResources().getColor(R.color.image_background));
        }else if(type == WidgetSettingsDB.DARK_VIBRANT_COLOR) {
            return Palette.from(bitmap).generate().getDarkVibrantColor(context.getResources().getColor(R.color.image_background));
        }else {
            return Color.TRANSPARENT;
        }
    }

    public static float toPercentage(int percentage){
        return ((float)percentage)/100;
    }

    public static int[] generatePallete(int color){
        return Colour.colorSchemeOfType(color, Colour.ColorScheme.ColorSchemeAnalagous);
    }
}
