package treehou.se.habit.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.mattyork.colours.Colour;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import treehou.se.habit.R;
import treehou.se.habit.core.settings.WidgetSettings;
import treehou.se.habit.ui.control.IconAdapter;

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
        return getBackground(context, bitmap, WidgetSettings.MUTED_COLOR);
    }

    public static int getBackground(Context context, Bitmap bitmap, int type){
        if(type == WidgetSettings.MUTED_COLOR) {
            return Palette.generate(bitmap).getMutedColor(context.getResources().getColor(R.color.image_background));
        }else if(type == WidgetSettings.LIGHT_MUTED_COLOR) {
            return Palette.generate(bitmap).getLightMutedColor(context.getResources().getColor(R.color.image_background));
        }else if(type == WidgetSettings.DARK_MUTED_COLOR) {
            return Palette.generate(bitmap).getDarkMutedColor(context.getResources().getColor(R.color.image_background));
        }else if(type == WidgetSettings.VIBRANT_COLOR) {
            return Palette.generate(bitmap).getVibrantColor(context.getResources().getColor(R.color.image_background));
        }else if(type == WidgetSettings.LIGHT_VIBRANT_COLOR) {
            return Palette.generate(bitmap).getLightVibrantColor(context.getResources().getColor(R.color.image_background));
        }else if(type == WidgetSettings.DARK_VIBRANT_COLOR) {
            return Palette.generate(bitmap).getDarkVibrantColor(context.getResources().getColor(R.color.image_background));
        }else {
            return Palette.generate(bitmap).getMutedColor(context.getResources().getColor(R.color.image_background));
        }
    }

    public static float toPercentage(int percentage){
        return ((float)percentage)/100;
    }

    public static int[] generatePallete(int color){
        return Colour.colorSchemeOfType(color, Colour.ColorScheme.ColorSchemeAnalagous);
    }
}
