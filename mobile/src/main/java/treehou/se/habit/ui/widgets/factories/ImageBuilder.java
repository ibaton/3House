package treehou.se.habit.ui.widgets.factories;

import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.net.MalformedURLException;
import java.net.URL;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.WidgetFactory;

/**
 * Created by ibaton on 2014-10-19.
 */
public class ImageBuilder implements IWidgetBuilder {

    @Override
    public WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {

        return new ImageBuilderHolder(widget, parent, widgetFactory);
    }

    static class ImageBuilderHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "ImageBuilderHolder";

        private BaseBuilder.BaseBuilderHolder baseHolder;
        private ImageView imgImage;
        private WidgetFactory factory;

        private ImageBuilderHolder(Widget widget, Widget parent, WidgetFactory factory) {
            this.factory = factory;

            View itemView = factory.getInflater().inflate(R.layout.item_widget_image, null);
            imgImage = (ImageView) itemView.findViewById(R.id.img_image);

            baseHolder = new BaseBuilder.BaseBuilderHolder.Builder(factory)
                    .setWidget(widget)
                    .setParent(parent)
                    .setShowLabel(false)
                    .build();

            baseHolder.getSubView().addView(itemView);
            update(widget);
        }

        @Override
        public void update(final Widget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }

            try {
                Log.d(TAG, "Image url " + widget.getUrl());
                URL imageUrl = new URL(widget.getUrl());
                Communicator communicator = Communicator.instance(factory.getContext());
                communicator.loadImage(factory.getServer(), imageUrl, imgImage);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            baseHolder.update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }
    }
}
