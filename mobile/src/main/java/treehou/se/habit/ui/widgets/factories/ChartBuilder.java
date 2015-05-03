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

public class ChartBuilder implements IWidgetBuilder {

    private static final String TAG = "ChartBuilder";

    @Override
    public WidgetFactory.IWidgetHolder build(WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {

        return ChartBuilderHolder.create(widgetFactory, widget);
    }


    static class ChartBuilderHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "SliderBuilderHolder";

        private BaseBuilder.BaseBuilderHolder baseHolder;

        private ImageView imgImage;
        private Widget widget;
        private WidgetFactory factory;

        public static ChartBuilderHolder create(WidgetFactory factory, Widget widget){
            return new ChartBuilderHolder(widget, factory);
        }

        private ChartBuilderHolder(Widget widget, WidgetFactory factory) {
            this.factory = factory;

            baseHolder = new BaseBuilder.BaseBuilderHolder.Builder(factory)
                    .setWidget(widget)
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
            this.widget = widget;
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }
    }
}
