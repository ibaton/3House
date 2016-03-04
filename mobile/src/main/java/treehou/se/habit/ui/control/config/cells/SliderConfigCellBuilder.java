package treehou.se.habit.ui.control.config.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;

import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.core.db.model.controller.SliderCellDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;

public class SliderConfigCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "SliderConfigCellBuilder";

    public View build(Context context, ControllerDB controller, CellDB cell){
        Realm realm = Realm.getDefaultInstance();
        SliderCellDB numberCell = SliderCellDB.getCell(realm, cell);

        int[] pallete = ControllerUtil.generateColor(controller, cell);

        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_conf_slider, null);
        View viwBackground = cellView.findViewById(R.id.viw_background);
        viwBackground.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);

        SeekBar sbrValue = (SeekBar) cellView.findViewById(R.id.sbr_value);
        sbrValue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        Drawable icon = Util.getIconDrawable(context, numberCell.getIcon());
        if(icon != null) {
            ImageView imgIcon = (ImageView) cellView.findViewById(R.id.img_icon);
            imgIcon.setImageDrawable(icon);
        }
        realm.close();

        return cellView;
    }



    @Override
    public RemoteViews buildRemote(Context context, ControllerDB controller, CellDB cell) {
        return null;
    }
}
