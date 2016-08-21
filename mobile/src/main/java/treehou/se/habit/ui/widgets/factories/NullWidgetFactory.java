package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class NullWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_null, null);

        return new WidgetFactory.WidgetHolder(itemView);
    }
}
