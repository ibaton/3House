package treehou.se.habit.ui.widgets.factories;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;

import se.treehou.ng.ohcommunicator.connector.ConnectorUtil;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.Connector;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class ChartWidgetFactory implements IWidgetFactory {

    private static final String TAG = ChartWidgetFactory.class.getSimpleName();

    @Override
    public WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, OHLinkedPage page, final OHWidget widget, final OHWidget parent) {
        return ChartWidgetHolder.create(widgetFactory, widget, parent);
    }

    public static class ChartWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "ChartWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;

        private ImageView imgImage;
        private WidgetFactory factory;

        public static ChartWidgetHolder create(WidgetFactory factory, OHWidget widget, OHWidget parent){
            return new ChartWidgetHolder(widget, parent, factory);
        }

        private ChartWidgetHolder(OHWidget widget, OHWidget parent, WidgetFactory factory) {
            this.factory = factory;

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
                    .setWidget(widget)
                    .setParent(parent)
                    .setFlat(false)
                    .setShowLabel(false)
                    .build();

            View itemView = factory.getInflater().inflate(R.layout.item_widget_chart, null);

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
                String url = Connector.ServerHandler.getUrl(factory.getContext(), factory.getServer());
                URL imageUrl = new URL(ConnectorUtil.buildChartRequestString(url, widget));
                Communicator communicator = Communicator.instance(factory.getContext());
                communicator.loadImage(factory.getServer(), imageUrl, imgImage, false);
            } catch (MalformedURLException e) {
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
