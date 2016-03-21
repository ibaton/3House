package treehou.se.habit.ui.widgets.factories;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.ui.widgets.WidgetFactory;

/**
 * Builds widgets used to represent an item in the openhab sitemap.
 */
public interface IWidgetFactory {

    WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, OHLinkedPage page, OHWidget widget, OHWidget parent);
}
