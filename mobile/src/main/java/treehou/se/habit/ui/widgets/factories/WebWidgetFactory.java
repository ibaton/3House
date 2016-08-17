package treehou.se.habit.ui.widgets.factories;

import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class WebWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, OHLinkedPage page, final OHWidget widget, final OHWidget parent) {

        return WebWidgetHolder.create(widgetFactory, widget, parent);
    }

    public static class WebWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "WebWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;

        private WebView webView;
        private OHWidget widget;

        public static WebWidgetHolder create(WidgetFactory factory, OHWidget widget, OHWidget parent){

            return new WebWidgetHolder(widget, parent, factory);
        }

        private WebWidgetHolder(OHWidget widget, OHWidget parent, WidgetFactory factory) {

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
                    .setWidget(widget)
                    .setFlat(false)
                    .setShowLabel(true)
                    .setParent(parent)
                    .build();

            final View itemView = factory.getInflater().inflate(R.layout.item_widget_web, null);
            webView = (WebView) itemView.findViewById(R.id.webView);
            WebViewClient client = new WebViewClient();
            webView.setWebViewClient(client);

            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            webView.setFocusableInTouchMode(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
            settings.setAppCacheEnabled(true);

            baseHolder.getSubView().addView(itemView);

            update(widget);
        }

        @Override
        public void update(final OHWidget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }
            OHWidget oldWidget = this.widget;

            if(oldWidget == null || !oldWidget.getUrl().equals(widget.getUrl())){
                webView.loadUrl(widget.getUrl());
            }

            baseHolder.update(widget);
            this.widget = widget;
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }
    }
}
