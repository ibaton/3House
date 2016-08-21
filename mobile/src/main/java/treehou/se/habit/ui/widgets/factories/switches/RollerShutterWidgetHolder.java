package treehou.se.habit.ui.widgets.factories.switches;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.BaseWidgetFactory;
import treehou.se.habit.util.ConnectionFactory;

/**
 * Widget rollershutters
 */
public class RollerShutterWidgetHolder implements WidgetFactory.IWidgetHolder {

    private static final String TAG = "RollerShutterWidgetHold";

    private BaseWidgetFactory.BaseWidgetHolder baseHolder;

    public static RollerShutterWidgetHolder create(Context context, WidgetFactory factory, ConnectionFactory connectionFactory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent){
        return new RollerShutterWidgetHolder(context, factory, connectionFactory, server, page, widget, parent);
    }

    private RollerShutterWidgetHolder(Context context, WidgetFactory factory, ConnectionFactory connectionFactory, OHServer server, OHLinkedPage page, final OHWidget widget, OHWidget parent) {

        baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                .setWidget(widget)
                .setShowLabel(true)
                .setParent(parent)
                .build();

        final OHItem item = widget.getItem();
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_rollershutters, null);

        IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);

        ImageButton btnUp = (ImageButton) itemView.findViewById(R.id.btn_up);
        btnUp.setOnClickListener(v -> {
            if (widget.getItem() != null) {
                serverHandler.sendCommand(widget.getItem().getName(), Constants.COMMAND_UP);
            }
        });

        ImageButton btnCancel = (ImageButton) itemView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> {
            if (widget.getItem() != null) {
                serverHandler.sendCommand(item.getName(), Constants.COMMAND_STOP);
            }
        });

        ImageButton btnDown = (ImageButton) itemView.findViewById(R.id.btn_down);
        btnDown.setOnClickListener(v -> {
            if (widget.getItem() != null) {
                serverHandler.sendCommand(item.getName(), Constants.COMMAND_DOWN);
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
