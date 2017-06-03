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

import butterknife.BindView;
import butterknife.ButterKnife;
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

    @BindView(R.id.viw_background) View viwBackground;
    @BindView(R.id.sbr_value) SeekBar sbrValue;
    @BindView(R.id.img_icon) ImageView imgIcon;

    public View build(Context context, ControllerDB controller, CellDB cell){
        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_conf_slider, null);
        ButterKnife.bind(this, cellView);

        Realm realm = Realm.getDefaultInstance();
        SliderCellDB numberCell = cell.getCellSlider();

        int[] pallete = ControllerUtil.generateColor(controller, cell);

        viwBackground.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);
        sbrValue.setOnTouchListener((v, event) -> true);

        Drawable icon = Util.getIconDrawable(context, numberCell.getIcon());
        if(icon != null) {
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
