package treehou.se.habit.ui.widgets.factories;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class GroupWidgetFactory implements IWidgetFactory {

    private static final String TAG = "GroupWidgetFactory";

    @Override
    public WidgetFactory.IWidgetHolder build(
            WidgetFactory widgetFactory, OHLinkedPage page, OHWidget widget, OHWidget parent) {

        WidgetFactory.IWidgetHolder itemView = new BaseWidgetFactory().build(widgetFactory, page, widget, parent);

        // TODO More work needed

        return itemView;
    }
}
