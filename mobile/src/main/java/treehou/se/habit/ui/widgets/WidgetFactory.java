package treehou.se.habit.ui.widgets;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.core.db.model.ServerDB;
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
    private ServerDB server;
    private OHLinkedPage page;
    private LayoutInflater inflater;

    private IWidgetFactory defaultBuilder = new TextWidgetFactory();

    private Map<String, IWidgetFactory> builders = new HashMap<>();

    public WidgetFactory(FragmentActivity context, ServerDB server, OHLinkedPage page){
        this.context = context;
        this.server = server;
        this.page = page;

        // Populate factory
        builders.put(OHWidget.TYPE_FRAME, new FrameWidgetFactory());
        builders.put(OHWidget.TYPE_CHART, new ChartWidgetFactory());
        builders.put(OHWidget.TYPE_COLORPICKER, new ColorpickerWidgetFactory());
        builders.put(OHWidget.TYPE_IMAGE, new ImageWidgetFactory());
        builders.put(OHWidget.TYPE_VIDEO, new VideoWidgetFactory());
        builders.put(OHWidget.TYPE_WEB, new WebWidgetFactory());
        builders.put(OHWidget.TYPE_SLIDER, new SliderWidgetFactory());
        builders.put(OHWidget.TYPE_SWITCH, new SwitchWidgetFactory());
        builders.put(OHWidget.TYPE_SELECTION, new SelectionWidgetFactory());
        builders.put(OHWidget.TYPE_SETPOINT, new SetpointWidgetFactory());
        builders.put(OHWidget.TYPE_TEXT, new TextWidgetFactory());
        builders.put(OHWidget.TYPE_GROUP, new GroupWidgetFactory());
    }

    public IWidgetHolder createWidget(final OHWidget widget , final OHWidget parent){
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

    public OHServer getServer() {
        return server.toGeneric();
    }

    public ServerDB getServerDB() {
        return server;
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

    public OHLinkedPage getPage() {
        return page;
    }
}
