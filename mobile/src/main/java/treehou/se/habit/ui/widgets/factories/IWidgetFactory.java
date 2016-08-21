package treehou.se.habit.ui.widgets.factories;

import android.content.Context;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.ui.widgets.WidgetFactory;

/**
 * Builds widgets used to represent an item in the openhab sitemap.
 */
public interface IWidgetFactory {

    WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent);
}
