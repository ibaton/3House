package treehou.se.habit.ui.control.config.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.ColorCellDB;
import treehou.se.habit.core.db.model.controller.ControllerDB;
import treehou.se.habit.core.db.model.controller.IncDecCellDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.control.CellFactory;

public class ColorConfigCellBuilder implements CellFactory.CellBuilder {

    private static final String TAG = "ColorConfigCellBuilder";

    @Bind(R.id.img_icon) ImageView imgIcon;

    public View build(Context context, ControllerDB controller, CellDB cell){
        LayoutInflater inflater = LayoutInflater.from(context);
        View cellView = inflater.inflate(R.layout.cell_conf_button, null);
        ButterKnife.bind(this, cellView);

        Realm realm = Realm.getDefaultInstance();
        ColorCellDB colorCell = ColorCellDB.getCell(realm, cell);

        Drawable icon = Util.getIconDrawable(context, colorCell.getIcon());
        if(icon != null) {
            imgIcon.setImageDrawable(icon);
        }
        imgIcon.getBackground().setColorFilter(cell.getColor(), PorterDuff.Mode.MULTIPLY);
        realm.close();

        ButterKnife.unbind(this);

        return cellView;
    }

    @Override
    public RemoteViews buildRemote(Context context, ControllerDB controller, CellDB cell) {
        return null;
    }
}
