package treehou.se.habit.ui.widgets.factories.switches;

import android.util.Log;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.ui.widgets.factories.IWidgetFactory;
import treehou.se.habit.ui.widgets.factories.NullWidgetFactory;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class SwitchWidgetFactory implements IWidgetFactory {

    private static final String TAG = "SwitchWidgetFactory";

    @Override
    public WidgetFactory.IWidgetHolder build(
            WidgetFactory widgetFactory, OHLinkedPage page,
            final OHWidget widget, final OHWidget parent) {

        if(widget.getMapping() == null || widget.getMapping().size() <= 0) {
            final OHItem item = widget.getItem();
            if (item == null || item.getType() == null) {
                Log.w(TAG, "Null switch created");
                return new NullWidgetFactory().build(widgetFactory, page, widget, parent);
            }

            if(item.getType().equals(OHItem.TYPE_ROLLERSHUTTER)){
                return RollerShutterWidgetHolder.create(widgetFactory, widget, parent);
            }else{
                return SwitchWidgetHolder.create(widgetFactory, widget, parent);
            }
        } else {
            if(widget.getMapping().size() == 1) {
                return SingleButtonWidgetHolder.create(widgetFactory, widget, parent);
            }else {
                return PickerWidgetHolder.create(widgetFactory, widget, parent);
            }
        }
    }


}
