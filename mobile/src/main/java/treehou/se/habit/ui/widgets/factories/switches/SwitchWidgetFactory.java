package treehou.se.habit.ui.widgets.factories.switches;

import android.content.Context;
import android.util.Log;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.util.OpenhabUtil;
import treehou.se.habit.ui.widgets.factories.IWidgetFactory;
import treehou.se.habit.ui.widgets.factories.NullWidgetFactory;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.util.ConnectionFactory;

public class SwitchWidgetFactory implements IWidgetFactory {

    private static final String TAG = "SwitchWidgetFactory";

    private ConnectionFactory connectionFactory;

    public SwitchWidgetFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        if(widget.getMapping() == null || widget.getMapping().size() <= 0) {
            final OHItem item = widget.getItem();
            if (item == null || item.getType() == null) {
                Log.w(TAG, "Null switch created");
                return new NullWidgetFactory().build(context, factory, server, page, widget, parent);
            }

            if(OpenhabUtil.isRollerShutter(item.getType())){
                return RollerShutterWidgetHolder.create(context, factory, connectionFactory, server, page, widget, parent);
            }else{
                return SwitchWidgetHolder.create(context, factory, connectionFactory, server, page, widget, parent);
            }
        } else {
            if(widget.getMapping().size() == 1) {
                return SingleButtonWidgetHolder.create(context, factory, connectionFactory, server, page, widget, parent);
            }else {
                return PickerWidgetHolder.create(context, factory, connectionFactory, server, page, widget, parent);
            }
        }
    }
}
