package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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

import org.greenrobot.eventbus.EventBus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class BaseWidgetFactory {

    private static final String TAG = "BaseWidgetFactory";

    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory widgetFactory, OHServer server, OHLinkedPage page, final OHWidget widget, final OHWidget parent) {
        return build(context, widgetFactory, server, page, widget, parent, false);
    }

    public WidgetFactory.IWidgetHolder build(
            Context context, WidgetFactory widgetFactory,
            OHServer server, OHLinkedPage page,
            final OHWidget widget, final OHWidget parent, boolean flat) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = flat ? inflater.inflate(R.layout.item_widget_base_flat, null) : inflater.inflate(R.layout.item_widget_base, null);


        // Put in dummy frame if not in frame
        if (parent == null) {
            View holderView = inflater.inflate(R.layout.widget_container, null);
            LinearLayout holder = (LinearLayout) holderView.findViewById(R.id.lou_widget_frame_holder);
            holder.addView(rootView);

            rootView = holderView;
        }

        BaseWidgetHolder holder = new BaseWidgetHolder(context, rootView, server, page, widget, widgetFactory);
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
        private WidgetFactory factory;

        private List<WidgetFactory.IWidgetHolder> widgetHolders = new ArrayList<>();
        private List<OHWidget> widgets = new ArrayList<>();
        private OHWidget widget;
        private OHLinkedPage page;
        private OHServer server;

        public static BaseWidgetHolder create(Context context, WidgetFactory factory, Builder builder){

            final LayoutInflater inflater = LayoutInflater.from(context);

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

            BaseWidgetHolder holder = new BaseWidgetHolder(context, rootView, builder.getServer(), builder.getPage(), builder.getWidget(), factory);

            Realm realm = Realm.getDefaultInstance();
            final WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
            float percentage = Util.toPercentage(settings.getTextSize());
            // Set size of icon
            float imageSizePercentage = Util.toPercentage(settings.getIconSize());
            realm.close();

            holder.lblName.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.lblName.getTextSize() * percentage);

            ViewGroup.LayoutParams layoutParams = holder.imgIcon.getLayoutParams();
            layoutParams.width = (int) (((float) layoutParams.width) * imageSizePercentage);
            holder.imgIcon.setLayoutParams(layoutParams);

            holder.update(builder.getWidget());

            return holder;
        }

        BaseWidgetHolder(Context context, View view, OHServer server, OHLinkedPage page, OHWidget widget, WidgetFactory factory) {
            super(view);

            this.context = context;
            this.page = page;
            this.widget = widget;
            this.server = server;
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
        public void update(final OHWidget widget) {

            if(widget == null){
                return;
            }

            Log.d(TAG, "update " + (widget.getItem() != null ? widget.getItem().getName() : "") + " " + widget.getLabel());

            setName(widget.getLabel());

            /*TODO implement*/
            if (widget.getLinkedPage() != null) {
                btnNextPage.setVisibility(View.VISIBLE);
                rootView.setOnClickListener(v -> EventBus.getDefault().post(widget.getLinkedPage()));
            } else {
                btnNextPage.setVisibility(View.GONE);
            }

            loadIcon(widget);

            this.widget = widget;
        }

        private synchronized void updateWidgets(List<OHWidget> pageWidgets){

            Log.d(TAG, "frame widgets update " + pageWidgets.size() + " : " + widgets.size());
            boolean invalidate = pageWidgets.size() != widgets.size();
            if(!invalidate){
                for(int i=0; i < widgets.size(); i++) {
                    OHWidget currentWidget = widgets.get(i);
                    OHWidget newWidget = pageWidgets.get(i);

                    // TODO handle update
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
                    WidgetFactory.IWidgetHolder result = widgetFactory.createWidget(context, server, page, widget, null);
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
                    OHWidget newWidget = pageWidgets.get(i);

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

            lblName.setText(Util.createLabel(context, name));

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

        /**
         * Load icon and populate image view with it.
         * @param widget the widget to load icon for.
         */
        private void loadIcon(OHWidget widget) {

            // TODO text is default value. Remove when fixed on server
            boolean ignoreDefault = "text".equalsIgnoreCase(widget.getIcon());
            if (widget.getIconPath() != null && !ignoreDefault) {
                iconHolder.setVisibility(View.VISIBLE);
                imgIcon.setVisibility(View.INVISIBLE);
                try {
                    Log.d(TAG, "widget.getIconPath " + widget.getIconPath() + " : " + page.getBaseUrl());
                    URL imageUrl = new URL(page.getBaseUrl() + widget.getIconPath());
                    Communicator communicator = Communicator.instance(context);
                    communicator.loadImage(server, imageUrl, imgIcon);
                    Log.d(TAG, "Loading image url " + imageUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                iconHolder.setVisibility(View.INVISIBLE);
                imgIcon.setVisibility(View.INVISIBLE);
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
        protected OHWidget getWidget(){
            return widget;
        }

        public LinearLayout getSubView() {
            return subView;
        }

        public static class Builder {

            private OHWidget widget;
            private OHWidget parent;
            private View view;
            private boolean flat = false;
            private boolean showLabel = true;
            private Context context;
            private OHServer server;
            private OHLinkedPage page;
            private WidgetFactory factory;

            public Builder(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page) {
                this.context = context;
                this.page = page;
                this.server = server;
                this.factory = factory;
            }

            /**
             * Set the widget to populate view with.
             *
             * @param widget the widget to display.
             * @return this builder.
             */
            public Builder setWidget(OHWidget widget) {
                this.widget = widget;
                return this;
            }

            /**
             * Set if the widget should be displayed compressed.
             *
             * @param flat true to show it compressed, else false.
             * @return this builder.
             */
            public Builder setFlat(boolean flat) {
                this.flat = flat;
                return this;
            }

            /**
             * Set parent widget
             * @param parent the parent of widget.
             * @return this builder.
             */
            public Builder setParent(OHWidget parent) {
                this.parent = parent;
                return this;
            }

            /**
             * The view to display widget in.
             *
             * @param view the view to display.
             * @return this builder.
             */
            public Builder setView(View view) {
                this.view = view;
                return this;
            }

            public Builder setShowLabel(boolean showLabel) {
                this.showLabel = showLabel;
                return this;
            }

            public OHWidget getWidget() {
                return widget;
            }

            public OHWidget getParent() {
                return parent;
            }

            public OHServer getServer() {
                return server;
            }

            public OHLinkedPage getPage() {
                return page;
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

                BaseWidgetHolder baseBuilderHolder = BaseWidgetHolder.create(context, factory, this);
                baseBuilderHolder.setShowLabel(showLabel);

                return baseBuilderHolder;
            }
        }
    }
}
