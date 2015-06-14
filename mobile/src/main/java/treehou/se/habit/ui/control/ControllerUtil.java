package treehou.se.habit.ui.control;

import android.graphics.Color;

import com.mattyork.colours.Colour;

import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.Controller;
import treehou.se.habit.util.Util;

/**
 * Created by ibaton on 2015-03-02.
 */
public class ControllerUtil {

    public static final int INDEX_BUTTON = 0;

    public static int[] generateColor(Controller controller, Cell cell) {
        return generateColor(controller, cell, true);
    }

    public static int[] generateColor(Controller controller, Cell cell, boolean preventInvis) {
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
