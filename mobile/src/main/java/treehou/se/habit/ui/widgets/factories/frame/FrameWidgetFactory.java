package treehou.se.habit.ui.widgets.factories.frame;

import android.content.Context;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.ui.widgets.factories.IWidgetFactory;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class FrameWidgetFactory implements IWidgetFactory {

    private static final String TAG = "FrameWidgetFactory";

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        return FrameWidget.create(context, factory, server, page, widget);
    }

}
