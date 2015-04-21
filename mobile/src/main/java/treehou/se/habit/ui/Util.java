package treehou.se.habit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.mattyork.colours.Colour;

import treehou.se.habit.R;
import treehou.se.habit.core.settings.WidgetSettings;
import treehou.se.habit.ui.control.Icon;

/**
 * Created by ibaton on 2014-11-09.
 */
public class Util {

    public static Icon getIcon(Context context, int value){

        String[] names          = context.getResources().getStringArray(R.array.cell_icons_name);
        int[] values            = context.getResources().getIntArray(R.array.cell_icons_values);
        TypedArray resources    = context.getResources().obtainTypedArray(R.array.cell_icons);

        for(int i=0; i<values.length; i++){
            if(values[i] == value) {
                return new Icon(names[i], values[i], resources.getResourceId(i, -1));
            }
        }
        return null;
    }

    public static int getBackground(Context context, Bitmap bitmap){
        return getBackground(context, bitmap, WidgetSettings.MUTED_COLOR);
    }

    public static int getBackground(Context context, Bitmap bitmap, int type){
        //return Palette.generate(bitmap).getLightMutedColor(context.getResources().getColor(R.color.image_background));
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
