package treehou.se.habit.ui.widgets.factories;

import android.content.Context;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class GroupWidgetFactory implements IWidgetFactory {

    private static final String TAG = "GroupWidgetFactory";

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        WidgetFactory.IWidgetHolder itemView = new BaseWidgetFactory().build(context, factory, server, page, widget, parent);

        // TODO More work needed

        return itemView;
    }
}
