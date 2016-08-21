package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class ImageWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        return new ImageWidgetHolder(context, factory, server, page, widget, parent);
    }

    public static class ImageWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "ImageWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private ImageView imgImage;
        private Context context;
        private OHServer server;

        ImageWidgetHolder(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
            this.server = server;
            this.context = context;

            View itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_image, null);
            imgImage = (ImageView) itemView.findViewById(R.id.img_image);

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                    .setWidget(widget)
                    .setParent(parent)
                    .setShowLabel(false)
                    .build();

            baseHolder.getSubView().addView(itemView);
            update(widget);
        }

        @Override
        public void update(final OHWidget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }

            try {
                Log.d(TAG, "Image url " + widget.getUrl());
                URL imageUrl = new URL(widget.getUrl());
                Communicator communicator = Communicator.instance(context);
                communicator.loadImage(server, imageUrl, imgImage);
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
