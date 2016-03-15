package treehou.se.habit.ui.widgets.factories;

import android.view.View;

import se.treehou.ng.ohcommunicator.core.OHLinkedPageWrapper;
import se.treehou.ng.ohcommunicator.core.OHWidgetWrapper;
import treehou.se.habit.R;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class NullWidgetFactory implements IWidgetFactory {

    public WidgetFactory.WidgetHolder build(WidgetFactory widgetFactory, OHLinkedPageWrapper page, final OHWidgetWrapper widget, final OHWidgetWrapper parent) {
        View itemView = widgetFactory.getInflater().inflate(R.layout.item_widget_null, null);

        return new WidgetFactory.WidgetHolder(itemView);
    }
}
