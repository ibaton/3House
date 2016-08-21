package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class WebWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        return new WebWidgetHolder(context, factory, server, page, widget, parent);
    }

    public static class WebWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "WebWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;

        private WebView webView;
        private OHWidget widget;

        private WebWidgetHolder(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                    .setWidget(widget)
                    .setFlat(false)
                    .setShowLabel(true)
                    .setParent(parent)
                    .build();

            final View itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_web, null);
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
