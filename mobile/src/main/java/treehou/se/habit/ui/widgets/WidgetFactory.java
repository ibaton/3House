package treehou.se.habit.ui.widgets;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.factories.ChartBuilder;
import treehou.se.habit.ui.widgets.factories.ColorpickerBuilder;
import treehou.se.habit.ui.widgets.factories.FrameBuilder;
import treehou.se.habit.ui.widgets.factories.GroupBuilder;
import treehou.se.habit.ui.widgets.factories.ICustomWidgetBuilder;
import treehou.se.habit.ui.widgets.factories.IWidgetBuilder;
import treehou.se.habit.ui.widgets.factories.ImageBuilder;
import treehou.se.habit.ui.widgets.factories.NullBuilder;
import treehou.se.habit.ui.widgets.factories.SelectionBuilder;
import treehou.se.habit.ui.widgets.factories.SliderBuilder;
import treehou.se.habit.ui.widgets.factories.SwitchBuilder;
import treehou.se.habit.ui.widgets.factories.TextBuilder;
import treehou.se.habit.ui.widgets.factories.VideoBuilder;
import treehou.se.habit.ui.widgets.factories.WebBuilder;

public class WidgetFactory {

    private static final String TAG = "WidgetFactory";

    private FragmentActivity context;
    private ServerDB server;
    private LinkedPage page;
    private LayoutInflater inflater;

    private IWidgetBuilder defaultBuilder = new NullBuilder();

    private Map<String, IWidgetBuilder> builders = new HashMap<>();

    public WidgetFactory(FragmentActivity context, ServerDB server, LinkedPage page){
        this.context = context;
        this.server = server;
        this.page = page;

        builders.put(Widget.TYPE_FRAME, new FrameBuilder());
        builders.put(Widget.TYPE_CHART, new ChartBuilder());
        builders.put(Widget.TYPE_COLORPICKER, new ColorpickerBuilder());
        builders.put(Widget.TYPE_IMAGE, new ImageBuilder());
        builders.put(Widget.TYPE_VIDEO, new VideoBuilder());
        builders.put(Widget.TYPE_WEB, new WebBuilder());
        builders.put(Widget.TYPE_SLIDER, new SliderBuilder());
        builders.put(Widget.TYPE_SWITCH, new SwitchBuilder());
        builders.put(Widget.TYPE_SELECTION, new SelectionBuilder());
        builders.put(Widget.TYPE_TEXT, new TextBuilder());
        builders.put(Widget.TYPE_GROUP, new GroupBuilder());
    }

    private ICustomWidgetBuilder getCustomWidget(List<Widget> widgets ,int position , Widget parent) {
        return null;
    }

    public IWidgetHolder createWidget(final Widget widget , final Widget parent){
        inflater = LayoutInflater.from(context);

        IWidgetHolder itemHolder = null;
        try {
            ViewGroup louWidgetHolder = null;

            if (builders.containsKey(widget.getType())) {
                IWidgetBuilder builder = builders.get(widget.getType());
                itemHolder = builder.build(this, page, widget, parent);
            } else {
                Log.w(TAG, "Error: No builder with type " + widget.getType());
                return defaultBuilder.build(this, page, widget, parent);
            }

            /*if(louWidgetHolder != null) {
                List<Widget> subWidgets = widget.getWidget();
                for (final Widget w : subWidgets) {
                    IWidgetHolder subWidget = createWidget(w, widget);
                    louWidgetHolder.addView(subWidget.getView());
                }
            }*/
        }catch (Exception e){
            e.printStackTrace();
            itemHolder = defaultBuilder.build(this, page, widget, parent);
        }

        //itemView.setOnLongClickListener(dialogItemListener);
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

    public static class WidgetSettingsCallback {
        private List<Widget> widgets;
        private int position;
        private Widget parent;

        public WidgetSettingsCallback(List<Widget> widgets ,int position , Widget parent) {
            this.widgets = widgets;
            this.position = position;
            this.parent = parent;
        }

        public List<Widget> getWidgets() {
            return widgets;
        }

        public int getPosition() {
            return position;
        }

        public Widget getParent() {
            return parent;
        }
    }
}
