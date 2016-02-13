package treehou.se.habit.ui.widgets.factories;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import se.treehou.ng.ohcommunicator.Openhab;
import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class SliderWidgetFactory implements IWidgetFactory {

    private static final String TAG = "SliderWidgetFactory";

    @Override
    public WidgetFactory.IWidgetHolder build(final WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {
        return new SliderWidgetHolder(widget, parent, widgetFactory);
    }

    public static class SliderWidgetHolder implements WidgetFactory.IWidgetHolder {

        private View itemView;
        private SeekBar skbDim;
        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private WidgetFactory factory;

        public SliderWidgetHolder(Widget widget, Widget parent, WidgetFactory factory) {

            this.factory = factory;
            WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(factory.getContext());
            boolean flat = settings.isCompressedSlider();

            itemView = factory.getInflater().inflate(R.layout.item_widget_slider, null);
            skbDim = (SeekBar) itemView.findViewById(R.id.skb_dim);

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
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
                    float progress = Float.valueOf(widget.getItem().getState());
                    skbDim.setProgress((int) progress);
                }
            }catch (Exception e){
                Log.e(TAG, "Failed to update progress", e);
            }

            skbDim.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if(widget.getItem() != null) {
                        try {
                            Openhab.sendCommand(ServerDB.toGeneric(factory.getServer()), widget.getItem().getName(), String.valueOf(skbDim.getProgress()));
                        } catch (Exception e) {}
                    }
                }
            });

            baseHolder.update(widget);
        }

        /**
         * Returns the holders slider view.
         *
         * @return sliders.
         */
        public SeekBar getSeekbarView() {
            return skbDim;
        }
    }
}
