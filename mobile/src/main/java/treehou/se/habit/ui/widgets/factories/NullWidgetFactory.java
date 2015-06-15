package treehou.se.habit.ui.widgets.factories;

import android.view.View;

import treehou.se.habit.R;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class NullWidgetFactory implements IWidgetFactory {

    public WidgetFactory.WidgetHolder build(WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {
        View itemView = widgetFactory.getInflater().inflate(R.layout.item_widget_null, null);

        return new WidgetFactory.WidgetHolder(itemView);
    }
}
