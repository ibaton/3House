package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class BaseWidgetFactory {

    private static final String TAG = "BaseWidgetFactory";

    public WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {
        return build(widgetFactory, page, widget, parent, false);
    }

    public WidgetFactory.IWidgetHolder build(
            WidgetFactory widgetFactory, final LinkedPage page,
            final Widget widget, final Widget parent, boolean flat) {

        final LayoutInflater inflater = widgetFactory.getInflater();
        View rootView = flat ? inflater.inflate(R.layout.item_widget_base_flat, null) : inflater.inflate(R.layout.item_widget_base, null);


        // Put in dummy frame if not in frame
        if (parent == null) {
            View holderView = inflater.inflate(R.layout.widget_container, null);
            LinearLayout holder = (LinearLayout) holderView.findViewById(R.id.lou_widget_frame_holder);
            holder.addView(rootView);

            rootView = holderView;
        }

        BaseWidgetHolder holder = new BaseWidgetHolder(widgetFactory.getContext(), rootView, widget, widgetFactory);
        holder.update(widget);

        return holder;
    }

    public static class BaseWidgetHolder extends WidgetFactory.WidgetHolder {

        Context context;
        View rootView;
        View baseDataHolder;
        TextView lblName;
        View iconHolder;
        ImageView imgIcon;
        ImageButton btnNextPage;
        WidgetFactory widgetFactory;
        LinearLayout subView;
        boolean showLabel = true;

        private List<WidgetFactory.IWidgetHolder> widgetHolders = new ArrayList<>();
        private List<Widget> widgets = new ArrayList<>();
        private Widget widget;

        public static BaseWidgetHolder create(Builder builder){

            final LayoutInflater inflater = builder.getFactory().getInflater();

            View rootView = builder.getView();
            if(rootView == null) {
                rootView = builder.isFlat() ? inflater.inflate(R.layout.item_widget_base_flat, null) : inflater.inflate(R.layout.item_widget_base, null);
            }

            // Put in dummy frame if not in frame
            if (builder.getParent() == null) {
                View holderView = inflater.inflate(R.layout.widget_container, null);
                LinearLayout holder = (LinearLayout) holderView.findViewById(R.id.lou_widget_frame_holder);
                holder.addView(rootView);

                rootView = holderView;
            }

            BaseWidgetHolder holder = new BaseWidgetHolder(builder.getFactory().getContext(), rootView, builder.getWidget(), builder.getFactory());

            final WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(builder.getFactory().getContext());
            float percentage = Util.toPercentage(settings.getTextSize());

            holder.lblName.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.lblName.getTextSize() * percentage);

            // Set size of icon
            float imageSizePercentage = Util.toPercentage(settings.getIconSize());
            ViewGroup.LayoutParams layoutParams = holder.imgIcon.getLayoutParams();
            layoutParams.width = (int) (((float) layoutParams.width) * imageSizePercentage);
            holder.imgIcon.setLayoutParams(layoutParams);

            holder.update(builder.getWidget());

            return holder;
        }

        BaseWidgetHolder(Context context, View view, Widget widget, WidgetFactory factory) {
            super(view);

            this.context = context;
            widgetFactory = factory;

            rootView = view;
            baseDataHolder = rootView.findViewById(R.id.lou_base_data_holder);
            iconHolder = rootView.findViewById(R.id.img_widget_icon_holder);
            lblName = (TextView) rootView.findViewById(R.id.lbl_widget_name);
            imgIcon = (ImageView) rootView.findViewById(R.id.img_widget_icon);
            btnNextPage = (ImageButton) rootView.findViewById(R.id.btn_next_page);
            subView = (LinearLayout) rootView.findViewById(R.id.lou_widget_holder);
        }

        @Override
        public void update(final Widget widget) {

            if(widget == null){
                return;
            }

            Log.d(TAG, "update " + widget.getLabel());

            setName(widget.getLabel());

            /*TODO implement*/
            if (widget.getLinkedPage() != null) {
                btnNextPage.setVisibility(View.VISIBLE);
                rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(widget.getLinkedPage());
                    }
                });
            } else {
                btnNextPage.setVisibility(View.GONE);
            }

            loadIcon(widget);

            this.widget = widget;
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
                    WidgetFactory.IWidgetHolder result = widgetFactory.createWidget(widget, null);
                    widgetHolders.add(result);
                    subView.addView(result.getView());
                }
                widgets.clear();
                widgets.addAll(pageWidgets);
            }
            else {
                Log.d(TAG, "updating widgets");
                for (int i=0; i < widgetHolders.size(); i++) {
                    WidgetFactory.IWidgetHolder holder = widgetHolders.get(i);

                    Log.d(TAG, "updating widget " + holder.getClass().getSimpleName());
                    Widget newWidget = pageWidgets.get(i);

                    holder.update(newWidget);
                }
            }

            widgets.clear();
            widgets.addAll(pageWidgets);
        }

        /**
         * Set name of widget
         *
         * @param name
         */
        private void setName(String name){
            Log.d(TAG, "setName " + name);

            name = name.replaceAll("(\\[)(.*)(\\])", "<font color='"+ String.format("#%06X", 0xFFFFFF & context.getResources().getColor(R.color.colorAccent)) +"'>$2</font>");
            lblName.setText(Html.fromHtml(name));

            if(baseDataHolder != null && "".equals(name.trim())) {
                baseDataHolder.setVisibility(View.GONE);
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

                baseDataHolder.setVisibility(showLabel ? View.VISIBLE : View.GONE);
            }
        }

        private void loadIcon(Widget widget) {
            if (widget.getIconPath() != null) {
                iconHolder.setVisibility(View.VISIBLE);
                imgIcon.setVisibility(View.GONE);
                try {
                    Log.d(TAG, "widget.getIconPath " + widget.getIconPath() + " : " + widgetFactory.getPage().getBaseUrl());
                    URL imageUrl = new URL(widgetFactory.getPage().getBaseUrl() + widget.getIconPath());
                    Communicator communicator = Communicator.instance(widgetFactory.getContext());
                    communicator.loadImage(widgetFactory.getServer(), imageUrl, imgIcon);
                    Log.d(TAG, "Loading image url " + imageUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                imgIcon.setVisibility(View.GONE);
                if (baseDataHolder != null && (widget.getLabel() == null || "".equals(widget.getLabel().trim()))) {
                    baseDataHolder.setVisibility(View.GONE);
                }
            }
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

        public static class Builder {

            private WidgetFactory factory;
            private Widget widget;
            private Widget parent;
            private View view;
            private boolean flat = false;
            private boolean showLabel = true;

            public Builder(WidgetFactory factory) {
                this.factory = factory;
            }

            public Builder setWidget(Widget widget) {
                this.widget = widget;
                return this;
            }

            public Builder setFlat(boolean flat) {
                this.flat = flat;
                return this;
            }

            public Builder setParent(Widget parent) {
                this.parent = parent;
                return this;
            }

            public Builder setView(View view) {
                this.view = view;
                return this;
            }

            public Builder setShowLabel(boolean showLabel) {
                this.showLabel = showLabel;
                return this;
            }

            public WidgetFactory getFactory() {
                return factory;
            }

            public Widget getWidget() {
                return widget;
            }

            public Widget getParent() {
                return parent;
            }

            public View getView() {
                return view;
            }

            public boolean isFlat() {
                return flat;
            }

            public boolean isShowLabel() {
                return showLabel;
            }

            public BaseWidgetHolder build(){

                BaseWidgetHolder baseBuilderHolder = BaseWidgetHolder.create(this);
                baseBuilderHolder.setShowLabel(showLabel);

                return baseBuilderHolder;
            }
        }
    }
}
