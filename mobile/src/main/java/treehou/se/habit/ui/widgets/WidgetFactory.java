package treehou.se.habit.ui.widgets;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import se.treehou.ng.ohcommunicator.core.OHLinkedPageWrapper;
import se.treehou.ng.ohcommunicator.core.OHServerWrapper;
import se.treehou.ng.ohcommunicator.core.OHWidgetWrapper;
import treehou.se.habit.ui.widgets.factories.ChartWidgetFactory;
import treehou.se.habit.ui.widgets.factories.ColorpickerWidgetFactory;
import treehou.se.habit.ui.widgets.factories.FrameWidgetFactory;
import treehou.se.habit.ui.widgets.factories.GroupWidgetFactory;
import treehou.se.habit.ui.widgets.factories.IWidgetFactory;
import treehou.se.habit.ui.widgets.factories.ImageWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SelectionWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SetpointWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SliderWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SwitchWidgetFactory;
import treehou.se.habit.ui.widgets.factories.TextWidgetFactory;
import treehou.se.habit.ui.widgets.factories.VideoWidgetFactory;
import treehou.se.habit.ui.widgets.factories.WebWidgetFactory;

public class WidgetFactory {

    private static final String TAG = "WidgetFactory";

    private FragmentActivity context;
    private OHServerWrapper server;
    private OHLinkedPageWrapper page;
    private LayoutInflater inflater;

    private IWidgetFactory defaultBuilder = new TextWidgetFactory();

    private Map<String, IWidgetFactory> builders = new HashMap<>();

    public WidgetFactory(FragmentActivity context, OHServerWrapper server, OHLinkedPageWrapper page){
        this.context = context;
        this.server = server;
        this.page = page;

        builders.put(OHWidgetWrapper.TYPE_FRAME, new FrameWidgetFactory());
        builders.put(OHWidgetWrapper.TYPE_CHART, new ChartWidgetFactory());
        builders.put(OHWidgetWrapper.TYPE_COLORPICKER, new ColorpickerWidgetFactory());
        builders.put(OHWidgetWrapper.TYPE_IMAGE, new ImageWidgetFactory());
        builders.put(OHWidgetWrapper.TYPE_VIDEO, new VideoWidgetFactory());
        builders.put(OHWidgetWrapper.TYPE_WEB, new WebWidgetFactory());
        builders.put(OHWidgetWrapper.TYPE_SLIDER, new SliderWidgetFactory());
        builders.put(OHWidgetWrapper.TYPE_SWITCH, new SwitchWidgetFactory());
        builders.put(OHWidgetWrapper.TYPE_SELECTION, new SelectionWidgetFactory());
        builders.put(OHWidgetWrapper.TYPE_SETPOINT, new SetpointWidgetFactory());
        builders.put(OHWidgetWrapper.TYPE_TEXT, new TextWidgetFactory());
        builders.put(OHWidgetWrapper.TYPE_GROUP, new GroupWidgetFactory());
    }

    public IWidgetHolder createWidget(final OHWidgetWrapper widget , final OHWidgetWrapper parent){
        inflater = LayoutInflater.from(context);

        IWidgetHolder itemHolder;
        try {
            if (builders.containsKey(widget.getType())) {
                Log.w(TAG, "Building widget with type " + widget.getType());
                IWidgetFactory builder = builders.get(widget.getType());
                itemHolder = builder.build(this, page, widget, parent);
            } else {
                Log.w(TAG, "Error: No builder with type " + widget.getType());
                return defaultBuilder.build(this, page, widget, parent);
            }
        }catch (Exception e){
            e.printStackTrace();
            itemHolder = defaultBuilder.build(this, page, widget, parent);
        }

        return itemHolder;
    }

    public Context getContext() {
        return context;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public OHServerWrapper getServer() {
        return server;
    }

    public interface IWidgetHolder {
        View getView();
        void update(OHWidgetWrapper widget);
    }

    public static class WidgetHolder implements IWidgetHolder {
        private View view;

        public WidgetHolder(View view) {
            this.view = view;
        }

        public View getView() {
            return view;
        }

        public void update(OHWidgetWrapper widget) {}
    }

    public OHLinkedPageWrapper getPage() {
        return page;
    }
}
