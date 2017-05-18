package treehou.se.habit.ui.widgets.factories.colorpicker;

import android.content.Context;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.IWidgetFactory;
import treehou.se.habit.util.ConnectionFactory;

public class ColorpickerWidgetFactory implements IWidgetFactory {

    private static final String TAG = "ColorpickerWidget";

    private ConnectionFactory connectionFactory;

    public ColorpickerWidgetFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        return new ColorWidgetHolder(context, factory, connectionFactory, server, page, widget, parent);
    }

}
