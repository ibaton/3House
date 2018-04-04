package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;

import se.treehou.ng.ohcommunicator.util.ConnectorUtil;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.Connector;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class ChartWidgetFactory implements IWidgetFactory {

    private static final String TAG = ChartWidgetFactory.class.getSimpleName();

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        return ChartWidgetHolder.create(context, factory, server, page, widget, parent);
    }

    public static class ChartWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "ChartWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;

        private ImageView imgImage;
        private Context context;
        private OHServer server;
        private WidgetFactory factory;

        public static ChartWidgetHolder create(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent){
            return new ChartWidgetHolder(context, factory, server, page, widget, parent);
        }

        private ChartWidgetHolder(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
            this.factory = factory;
            this.server = server;
            this.context = context;

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                    .setWidget(widget)
                    .setParent(parent)
                    .setFlat(false)
                    .setShowLabel(false)
                    .build();

            View itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_chart, null);

            imgImage = (ImageView) itemView.findViewById(R.id.img_chart);
            baseHolder.getSubView().addView(itemView);

            update(widget);
        }

        @Override
        public void update(final OHWidget widget) {
            if (widget == null) {
                return;
            }

            try {
                String url = Connector.ServerHandler.getUrl(context, server);
                Uri imageUrl = Uri.parse(ConnectorUtil.buildChartRequestString(url, widget));
                Communicator communicator = Communicator.instance(context);
                communicator.loadImage(server, imageUrl, imgImage, false);
            } catch (Exception e) {
                Log.e(TAG, "Failed to update chart", e);
            }

            baseHolder.update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }
    }
}
