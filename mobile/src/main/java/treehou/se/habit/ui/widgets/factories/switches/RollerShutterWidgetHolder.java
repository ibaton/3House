package treehou.se.habit.ui.widgets.factories.switches;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.BaseWidgetFactory;

/**
 * Widget rollershutters
 */
public class RollerShutterWidgetHolder implements WidgetFactory.IWidgetHolder {

    private static final String TAG = "RollerShutterWidgetHold";

    private BaseWidgetFactory.BaseWidgetHolder baseHolder;

    public static RollerShutterWidgetHolder create(WidgetFactory factory, OHWidget widget, OHWidget parent){
        return new RollerShutterWidgetHolder(widget, parent, factory);
    }

    private RollerShutterWidgetHolder(final OHWidget widget, OHWidget parent, final WidgetFactory factory) {

        baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
                .setWidget(widget)
                .setShowLabel(true)
                .setParent(parent)
                .build();

        final OHItem item = widget.getItem();
        View itemView = factory.getInflater().inflate(R.layout.item_widget_rollershutters, null);

        ImageButton btnUp = (ImageButton) itemView.findViewById(R.id.btn_up);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (widget.getItem() != null) {
                    Openhab.instance(factory.getServer()).sendCommand(widget.getItem().getName(), Constants.COMMAND_UP);
                }
            }
        });

        ImageButton btnCancel = (ImageButton) itemView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (widget.getItem() != null) {
                    Openhab.instance(factory.getServer()).sendCommand(item.getName(), Constants.COMMAND_STOP);
                }
            }
        });

        ImageButton btnDown = (ImageButton) itemView.findViewById(R.id.btn_down);
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (widget.getItem() != null) {
                    Openhab.instance(factory.getServer()).sendCommand(item.getName(), Constants.COMMAND_DOWN);
                }
            }
        });

        baseHolder.getSubView().addView(itemView);
        update(widget);
    }

    @Override
    public void update(final OHWidget widget) {
        Log.d(TAG, "update " + widget);

        if (widget == null) {
            return;
        }

        baseHolder.update(widget);
    }

    @Override
    public View getView() {
        return baseHolder.getView();
    }
}
