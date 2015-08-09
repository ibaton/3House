package treehou.se.habit.ui.widgets.factories;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.ConnectorUtil;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class ChartWidgetFactory implements IWidgetFactory {

    private static final String TAG = "ChartWidgetFactory";

    @Override
    public WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {
        return ChartWidgetHolder.create(widgetFactory, widget, parent);
    }

    static class ChartWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "ChartWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;

        private ImageView imgImage;
        private WidgetFactory factory;

        public static ChartWidgetHolder create(WidgetFactory factory, Widget widget, Widget parent){
            return new ChartWidgetHolder(widget, parent, factory);
        }

        private ChartWidgetHolder(Widget widget, Widget parent, WidgetFactory factory) {
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
        public void update(final Widget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }

            try {
                URL imageUrl = new URL(ConnectorUtil.buildChartRequestString(factory.getServer().getUrl(), widget));
                Communicator communicator = Communicator.instance(factory.getContext());
                communicator.loadImage(factory.getServer(), imageUrl, imgImage, false);
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
