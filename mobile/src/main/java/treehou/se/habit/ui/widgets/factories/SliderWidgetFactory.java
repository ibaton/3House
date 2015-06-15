package treehou.se.habit.ui.widgets.factories;

import android.view.View;
import android.widget.SeekBar;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class SliderWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(final WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {

        return new SliderBuilderHolder(parent, widget, widgetFactory);
    }

    public static class SliderBuilderHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "SliderBuilderHolder";

        private View itemView;
        private SeekBar skbDim;
        private BaseWidgetFactory.BaseBuilderHolder baseHolder;
        private WidgetFactory factory;

        public SliderBuilderHolder(Widget widget, Widget parent, WidgetFactory factory) {

            this.factory = factory;
            WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(factory.getContext());
            boolean flat = settings.isCompressedSlider();

            itemView = factory.getInflater().inflate(R.layout.item_widget_slider, null);
            skbDim = (SeekBar) itemView.findViewById(R.id.skb_dim);

            baseHolder = new BaseWidgetFactory.BaseBuilderHolder.Builder(factory)
                    .setWidget(widget)
                    .setParent(parent)
                    .setFlat(flat)
                    .build();

            baseHolder.getSubView().addView(itemView);
            update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }

        @Override
        public void update(final Widget widget) {
            if (widget == null) {
                return;
            }

            skbDim.setOnSeekBarChangeListener(null);
            try {
                if(widget.getItem() != null) {
                    int progress = Integer.valueOf(widget.getItem().getState());
                    skbDim.setProgress(progress);
                }
            }catch (NumberFormatException e){}

            skbDim.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if(widget.getItem() != null) {
                        try {
                            Communicator communicator = Communicator.instance(factory.getContext());
                            communicator.command(factory.getServer(), widget.getItem(), String.valueOf(skbDim.getProgress()));
                        } catch (Exception e) {}
                    }
                }
            });

            baseHolder.update(widget);
        }
    }
}
