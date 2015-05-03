package treehou.se.habit.ui.widgets.factories;

import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class GroupBuilder implements IWidgetBuilder {

    private static final String TAG = "GroupBuilder";

    @Override
    public WidgetFactory.IWidgetHolder build(
            WidgetFactory widgetFactory, LinkedPage page, Widget widget, Widget parent) {

        WidgetFactory.IWidgetHolder itemView = new BaseBuilder().build(widgetFactory, page, widget, parent);

        // TODO More work needed

        return itemView;
    }
}
