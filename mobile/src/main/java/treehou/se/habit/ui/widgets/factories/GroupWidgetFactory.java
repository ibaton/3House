package treehou.se.habit.ui.widgets.factories;

import se.treehou.ng.ohcommunicator.core.OHLinkedPageWrapper;
import se.treehou.ng.ohcommunicator.core.OHWidgetWrapper;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class GroupWidgetFactory implements IWidgetFactory {

    private static final String TAG = "GroupWidgetFactory";

    @Override
    public WidgetFactory.IWidgetHolder build(
            WidgetFactory widgetFactory, OHLinkedPageWrapper page, OHWidgetWrapper widget, OHWidgetWrapper parent) {

        WidgetFactory.IWidgetHolder itemView = new BaseWidgetFactory().build(widgetFactory, page, widget, parent);

        // TODO More work needed

        return itemView;
    }
}
