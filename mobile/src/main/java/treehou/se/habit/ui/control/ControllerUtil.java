package treehou.se.habit.ui.control;

import android.graphics.Color;

import com.mattyork.colours.Colour;

import treehou.se.habit.core.db.controller.CellDB;
import treehou.se.habit.core.db.controller.ControllerDB;
import treehou.se.habit.util.Util;

public class ControllerUtil {

    public static final int INDEX_BUTTON = 0;

    public static int[] generateColor(ControllerDB controller, CellDB cell) {
        return generateColor(controller, cell, true);
    }

    public static int[] generateColor(ControllerDB controller, CellDB cell, boolean preventInvis) {
        int[] pallete;
        if (Colour.alpha(cell.getColor()) < 150) {
            if (preventInvis && Colour.alpha(controller.getColor()) < 150) {
                pallete = Util.generatePallete(Color.LTGRAY);
            }else {
                pallete = Util.generatePallete(controller.getColor());
            }
        } else {
            pallete = Util.generatePallete(cell.getColor());
        }

        return pallete;
    }
}
