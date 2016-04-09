package treehou.se.habit.ui.widgets.factories;

import android.view.View;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class NullWidgetFactory implements IWidgetFactory {

    public WidgetFactory.WidgetHolder build(WidgetFactory widgetFactory, OHLinkedPage page, final OHWidget widget, final OHWidget parent) {
        View itemView = widgetFactory.getInflater().inflate(R.layout.item_widget_null, null);

        return new WidgetFactory.WidgetHolder(itemView);
    }
}
