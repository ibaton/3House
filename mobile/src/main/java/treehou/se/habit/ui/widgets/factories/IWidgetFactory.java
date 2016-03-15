package treehou.se.habit.ui.widgets.factories;

import se.treehou.ng.ohcommunicator.core.OHLinkedPageWrapper;
import se.treehou.ng.ohcommunicator.core.OHWidgetWrapper;
import treehou.se.habit.ui.widgets.WidgetFactory;

/**
 * Builds widgets used to represent an item in the openhab sitemap.
 */
public interface IWidgetFactory {

    WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, OHLinkedPageWrapper page, OHWidgetWrapper widget, OHWidgetWrapper parent);
}
