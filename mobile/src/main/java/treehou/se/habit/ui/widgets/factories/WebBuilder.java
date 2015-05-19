package treehou.se.habit.ui.widgets.factories;

import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import treehou.se.habit.R;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.WidgetFactory;

/**
 * Created by ibaton on 2014-10-19.
 */
public class WebBuilder implements IWidgetBuilder {

    private static final String TAG = "WebBuilder";

    @Override
    public WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {

        WidgetFactory.IWidgetHolder rootView = WebBuilderHolder.create(widgetFactory, widget, parent);

        return rootView;
    }

    static class WebBuilderHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "WebBuilderHolder";

        private BaseBuilder.BaseBuilderHolder baseHolder;

        private WebView webView;
        private Widget widget;

        public static WebBuilderHolder create(WidgetFactory factory, Widget widget, Widget parent){

            return new WebBuilderHolder(widget, parent, factory);
        }

        private WebBuilderHolder(Widget widget, Widget parent, WidgetFactory factory) {

            baseHolder = BaseBuilder.BaseBuilderHolder.create(factory, false, widget, parent);

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
        public void update(final Widget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }
            Widget oldWidget = this.widget;

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
