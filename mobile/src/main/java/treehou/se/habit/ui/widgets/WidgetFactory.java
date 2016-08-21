package treehou.se.habit.ui.widgets;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.util.OpenhabConstants;
import treehou.se.habit.ui.widgets.factories.ChartWidgetFactory;
import treehou.se.habit.ui.widgets.factories.FrameWidgetFactory;
import treehou.se.habit.ui.widgets.factories.GroupWidgetFactory;
import treehou.se.habit.ui.widgets.factories.IWidgetFactory;
import treehou.se.habit.ui.widgets.factories.ImageWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SelectionWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SetpointWidgetFactory;
import treehou.se.habit.ui.widgets.factories.switches.SwitchWidgetFactory;
import treehou.se.habit.ui.widgets.factories.TextWidgetFactory;
import treehou.se.habit.ui.widgets.factories.VideoWidgetFactory;
import treehou.se.habit.ui.widgets.factories.WebWidgetFactory;

public class WidgetFactory {

    private static final String TAG = "WidgetFactory";

    private IWidgetFactory defaultBuilder = new TextWidgetFactory();

    private Map<String, IWidgetFactory> builders = new HashMap<>();

    public WidgetFactory(){

        // Populate factory
        builders.put(OpenhabConstants.TYPE_FRAME, new FrameWidgetFactory());
        builders.put(OpenhabConstants.TYPE_CHART, new ChartWidgetFactory());
        builders.put(OpenhabConstants.TYPE_IMAGE, new ImageWidgetFactory());
        builders.put(OpenhabConstants.TYPE_VIDEO, new VideoWidgetFactory());
        builders.put(OpenhabConstants.TYPE_WEB, new WebWidgetFactory());
        builders.put(OpenhabConstants.TYPE_SELECTION, new SelectionWidgetFactory());
        builders.put(OpenhabConstants.TYPE_SETPOINT, new SetpointWidgetFactory());
        builders.put(OpenhabConstants.TYPE_TEXT, new TextWidgetFactory());
        builders.put(OpenhabConstants.TYPE_GROUP, new GroupWidgetFactory());
    }

    public IWidgetHolder createWidget(Context context, OHServer server, OHLinkedPage page, OHWidget widget , final OHWidget parent){

        IWidgetHolder itemHolder;
        try {
            if (builders.containsKey(widget.getType())) {
                Log.w(TAG, "Building widget with type " + widget.getType());
                IWidgetFactory builder = builders.get(widget.getType());
                itemHolder = builder.build(context, this, server, page, widget, parent);
            } else {
                Log.w(TAG, "Error: No builder with type " + widget.getType());
                return defaultBuilder.build(context, this, server, page, widget, parent);
            }
        }catch (Exception e){
            e.printStackTrace();
            itemHolder = defaultBuilder.build(context, this, server, page, widget, parent);
        }

        return itemHolder;
    }

    public void addWidgetFactory(String type, IWidgetFactory sliderWidgetFactory){
        builders.put(type, sliderWidgetFactory);
    }

    public interface IWidgetHolder {
        View getView();
        void update(OHWidget widget);
    }

    public static class WidgetHolder implements IWidgetHolder {
        private View view;

        public WidgetHolder(View view) {
            this.view = view;
        }

        public View getView() {
            return view;
        }

        public void update(OHWidget widget) {}
    }
}
