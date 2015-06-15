package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.R;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class FrameWidgetFactory implements IWidgetFactory {

    private static final String TAG = "FrameWidgetFactory";

    @Override
    public WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, LinkedPage page, Widget widget, Widget parent) {
        return FrameHolder.create(widgetFactory, widget);
    }

    public static class FrameHolder extends WidgetFactory.WidgetHolder {

        private TextView lblName;
        private View titleHolder;
        private WidgetFactory widgetFactory;
        private LinearLayout subView;
        private boolean showLabel = true;

        private List<WidgetFactory.IWidgetHolder> widgetHolders = new ArrayList<>();
        private Widget widget;
        private List<Widget> widgets = new ArrayList<>();

        public static FrameHolder create(WidgetFactory factory, Widget widget){

            View rootView = factory.getInflater().inflate(R.layout.widget_frame, null);
            TextView lblTitle = (TextView) rootView.findViewById(R.id.lbl_widget_name);
            View lblTitleHolder = rootView.findViewById(R.id.lbl_widget_name_holder);
            LinearLayout louWidgetHolder = (LinearLayout) rootView.findViewById(R.id.lou_widget_frame_holder);

            FrameHolder holder = new FrameHolder(factory.getContext(), rootView, louWidgetHolder, lblTitleHolder, lblTitle, widget, factory);

            Log.d(TAG, "update " + widget.getLabel());
            final WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(factory.getContext());
            float percentage = Util.toPercentage(settings.getTextSize());
            holder.lblName.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.lblName.getTextSize() * percentage);

            holder.update(widget);

            return holder;
        }

        private FrameHolder(Context context, View view, LinearLayout louWidgetHolder, View titleHolder, TextView lblName, Widget widget, WidgetFactory factory) {
            super(view);

            Log.d(TAG, "Crating frame " + widget.getLabel());

            widgetFactory = factory;
            this.lblName = lblName;

            lblName.setText(widget.getLabel());

            this.titleHolder = titleHolder;
            if (widget.getLabel() == null || "".equals(widget.getLabel().trim())) {
                titleHolder.setVisibility(View.GONE);
            }

            subView = louWidgetHolder;
        }

        @Override
        public void update(final Widget widget) {

            if(widget == null){
                return;
            }

            if(widget.getLabel() != null) {
                setName(widget.getLabel());
            }

            setName(widget.getLabel());

            this.widget = widget;
            updateWidgets(widget.getWidget());
        }

        private synchronized void updateWidgets(List<Widget> pageWidgets){

            Log.d(TAG, "frame widgets update " + pageWidgets.size() + " : " + widgets.size());
            boolean invalidate = pageWidgets.size() != widgets.size();
            if(!invalidate){
                for(int i=0; i < widgets.size(); i++) {
                    Widget currentWidget = widgets.get(i);
                    Widget newWidget = pageWidgets.get(i);

                    if(currentWidget.needUpdate(newWidget)){
                        invalidate = true;
                        break;
                    }
                }
            }

            if(invalidate) {
                Log.d(TAG, "Invalidating frame widgets " + pageWidgets.size() + " : " + widgets.size());

                widgetHolders.clear();
                subView.removeAllViews();

                for (Widget widget : pageWidgets) {
                    try {
                        WidgetFactory.IWidgetHolder result = widgetFactory.createWidget(widget, this.widget);
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
                        Widget newWidget = pageWidgets.get(i);
                        holder.update(newWidget);
                    }catch (Exception e){
                        Log.w(TAG, "Update widget failed " + e);
                    }
                }
            }
        }

        /**
         * Set name of widget
         *
         * @param showLabel
         */
        private void setShowLabel(boolean showLabel){

            if(this.showLabel != showLabel) {
                this.showLabel = showLabel;

                titleHolder.setVisibility(showLabel ? View.VISIBLE : View.GONE);
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
        protected Widget getWidget(){
            return widget;
        }

        public LinearLayout getSubView() {
            return subView;
        }
    }
}
