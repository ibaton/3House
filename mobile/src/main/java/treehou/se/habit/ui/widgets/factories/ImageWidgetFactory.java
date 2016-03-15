package treehou.se.habit.ui.widgets.factories;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;

import se.treehou.ng.ohcommunicator.core.OHLinkedPageWrapper;
import se.treehou.ng.ohcommunicator.core.OHWidgetWrapper;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class ImageWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, OHLinkedPageWrapper page, final OHWidgetWrapper widget, final OHWidgetWrapper parent) {

        return new ImageWidgetHolder(widget, parent, widgetFactory);
    }

    public static class ImageWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "ImageWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private ImageView imgImage;
        private WidgetFactory factory;

        ImageWidgetHolder(OHWidgetWrapper widget, OHWidgetWrapper parent, WidgetFactory factory) {
            this.factory = factory;

            View itemView = factory.getInflater().inflate(R.layout.item_widget_image, null);
            imgImage = (ImageView) itemView.findViewById(R.id.img_image);

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
                    .setWidget(widget)
                    .setParent(parent)
                    .setShowLabel(false)
                    .build();

            baseHolder.getSubView().addView(itemView);
            update(widget);
        }

        @Override
        public void update(final OHWidgetWrapper widget) {
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
