package treehou.se.habit.ui.widgets.factories.frame;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.util.Util;

public class FrameWidget extends WidgetFactory.WidgetHolder {

    private static final String TAG = "FrameWidget";

    private TextView lblName;
    private View titleHolder;
    private WidgetFactory widgetFactory;
    private LinearLayout subView;
    private Context context;

    private List<WidgetFactory.IWidgetHolder> widgetHolders = new ArrayList<>();
    private OHServer server;
    private OHLinkedPage page;
    private OHWidget widget;
    private List<OHWidget> widgets = new ArrayList<>();

    public static FrameWidget create(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget){

        View rootView = LayoutInflater.from(context).inflate(R.layout.widget_frame, null);
        TextView lblTitle = (TextView) rootView.findViewById(R.id.widgetName);
        View lblTitleHolder = rootView.findViewById(R.id.lbl_widget_name_holder);
        LinearLayout louWidgetHolder = (LinearLayout) rootView.findViewById(R.id.lou_widget_frame_holder);

        FrameWidget holder = new FrameWidget(context, factory, server, page, rootView, louWidgetHolder, lblTitleHolder, lblTitle, widget);

        Log.d(TAG, "update " + widget.getLabel());
        Realm realm = Realm.getDefaultInstance();
        final WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        float percentage = Util.INSTANCE.toPercentage(settings.getTextSize());
        realm.close();
        holder.lblName.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.lblName.getTextSize() * percentage);

        holder.update(widget);

        return holder;
    }

    private FrameWidget(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, View view, LinearLayout louWidgetHolder, View titleHolder, TextView lblName, OHWidget widget) {
        super(view);

        Log.d(TAG, "Crating frame " + widget.getLabel());

        this.server = server;
        this.page = page;

        widgetFactory = factory;
        this.lblName = lblName;
        this.context = context;

        lblName.setText(widget.getLabel());

        this.titleHolder = titleHolder;
        if (widget.getLabel() == null || "".equals(widget.getLabel().trim())) {
            titleHolder.setVisibility(View.GONE);
        }

        subView = louWidgetHolder;
    }

    @Override
    public void update(final OHWidget widget) {
        this.widget = widget;
        if(TextUtils.isEmpty(widget.getLabel())) {
            setName(widget.getLabel());
        }
        updateWidgets(widget.getWidget());
    }

    private synchronized void updateWidgets(List<OHWidget> pageWidgets){

        Log.d(TAG, "frame widgets update " + pageWidgets.size() + " : " + widgets.size());
        boolean invalidate = pageWidgets.size() != widgets.size();
        if(!invalidate){
            for(int i=0; i < widgets.size(); i++) {
                OHWidget currentWidget = widgets.get(i);
                OHWidget newWidget = pageWidgets.get(i);

                // TODO handle updates
                //if(currentWidget.needUpdate(newWidget)){
                    invalidate = true;
                //    break;
                //}
            }
        }

        if(invalidate) {
            Log.d(TAG, "Invalidating frame widgets " + pageWidgets.size() + " : " + widgets.size());

            widgetHolders.clear();
            subView.removeAllViews();

            for (OHWidget widget : pageWidgets) {
                try {
                    WidgetFactory.IWidgetHolder result = widgetFactory.createWidget(context, server, page, widget, this.widget);
                    widgetHolders.add(result);
                    subView.addView(result.getView());
                }catch (Exception e){
                    Log.w(TAG, "Invalidate widget failed " + e);
                }
            }
            widgets.clear();
            widgets.addAll(pageWidgets);
        }
        else {
            Log.d(TAG, "updating widgets");
            for (int i=0; i < widgetHolders.size(); i++) {
                try {
                    WidgetFactory.IWidgetHolder holder = widgetHolders.get(i);
                    OHWidget newWidget = pageWidgets.get(i);
                    holder.update(newWidget);
                }catch (Exception e){
                    Log.w(TAG, "Update widget failed " + e);
                }
            }
        }
    }

    private void setName(String title){
        lblName.setText(title);
    }

    /**
     * Get previously stored widget.
     *
     * @return widget
     */
    protected OHWidget getWidget(){
        return widget;
    }

    public LinearLayout getSubView() {
        return subView;
    }
}
