package treehou.se.habit.ui.widgets.factories;

import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.WidgetFactory;

/**
 * Builds widgets used to represent an item in the openhab sitemap.
 */
public interface IWidgetFactory {

    WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, LinkedPage page, Widget widget, Widget parent);
}
