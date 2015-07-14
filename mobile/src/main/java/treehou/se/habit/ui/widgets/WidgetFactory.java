package treehou.se.habit.ui.widgets;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.factories.ChartWidgetFactory;
import treehou.se.habit.ui.widgets.factories.ColorpickerWidgetFactory;
import treehou.se.habit.ui.widgets.factories.FrameWidgetFactory;
import treehou.se.habit.ui.widgets.factories.GroupWidgetFactory;
import treehou.se.habit.ui.widgets.factories.IWidgetFactory;
import treehou.se.habit.ui.widgets.factories.ImageWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SelectionWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SliderWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SwitchWidgetFactory;
import treehou.se.habit.ui.widgets.factories.TextWidgetFactory;
import treehou.se.habit.ui.widgets.factories.VideoWidgetFactory;
import treehou.se.habit.ui.widgets.factories.WebWidgetFactory;

public class WidgetFactory {

    private static final String TAG = "WidgetFactory";

    private FragmentActivity context;
    private ServerDB server;
    private LinkedPage page;
    private LayoutInflater inflater;

    private IWidgetFactory defaultBuilder = new TextWidgetFactory();

    private Map<String, IWidgetFactory> builders = new HashMap<>();

    public WidgetFactory(FragmentActivity context, ServerDB server, LinkedPage page){
        this.context = context;
        this.server = server;
        this.page = page;

        builders.put(Widget.TYPE_FRAME, new FrameWidgetFactory());
        builders.put(Widget.TYPE_CHART, new ChartWidgetFactory());
        builders.put(Widget.TYPE_COLORPICKER, new ColorpickerWidgetFactory());
        builders.put(Widget.TYPE_IMAGE, new ImageWidgetFactory());
        builders.put(Widget.TYPE_VIDEO, new VideoWidgetFactory());
        builders.put(Widget.TYPE_WEB, new WebWidgetFactory());
        builders.put(Widget.TYPE_SLIDER, new SliderWidgetFactory());
        builders.put(Widget.TYPE_SWITCH, new SwitchWidgetFactory());
        builders.put(Widget.TYPE_SELECTION, new SelectionWidgetFactory());
        builders.put(Widget.TYPE_TEXT, new TextWidgetFactory());
        builders.put(Widget.TYPE_GROUP, new GroupWidgetFactory());
    }

    public IWidgetHolder createWidget(final Widget widget , final Widget parent){
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

    public ServerDB getServer() {
        return server;
    }

    public interface IWidgetHolder {
        View getView();
        void update(Widget widget);
    }

    public static class WidgetHolder implements IWidgetHolder {
        private View view;

        public WidgetHolder(View view) {
            this.view = view;
        }

        public View getView() {
            return view;
        }

        public void update(Widget widget) {}
    }

    public LinkedPage getPage() {
        return page;
    }
}
