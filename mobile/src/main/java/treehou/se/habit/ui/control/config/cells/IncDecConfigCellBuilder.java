package treehou.se.habit.ui.control.config.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.core.db.model.controller.IncDecCellDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerUtil;

public class IncDecConfigCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "IncDecConfigCellBuilder";

    @Bind(R.id.img_icon_button) ImageButton imgIcon;

    public View build(Context context, ControllerDB controller, CellDB cell){
        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_conf_button, null);
        ButterKnife.bind(this, cellView);

        Realm realm = Realm.getDefaultInstance();
        IncDecCellDB numberCell = IncDecCellDB.getCell(realm, cell);

        int[] pallete = ControllerUtil.generateColor(controller, cell);
        cellView.setBackgroundColor(pallete[ControllerUtil.INDEX_BUTTON]);

        imgIcon.getBackground().setColorFilter(pallete[ControllerUtil.INDEX_BUTTON], PorterDuff.Mode.MULTIPLY);

        Log.d(TAG, "Build: Button icon " + numberCell.getIcon());

        Drawable icon = Util.getIconDrawable(context, numberCell.getIcon());
        if(icon != null) {
            imgIcon.setImageDrawable(icon);
        }
        realm.close();

        ButterKnife.unbind(this);

        return cellView;
    }



    @Override
    public RemoteViews buildRemote(Context context, ControllerDB controller, CellDB cell) {
        return null;
    }
}
