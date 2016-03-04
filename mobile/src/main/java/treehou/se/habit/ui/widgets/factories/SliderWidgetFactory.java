package treehou.se.habit.ui.widgets.factories;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class SliderWidgetFactory implements IWidgetFactory {

    private static final String TAG = "SliderWidgetFactory";

    @Override
    public WidgetFactory.IWidgetHolder build(final WidgetFactory widgetFactory, OHLinkedPage page, final OHWidget widget, final OHWidget parent) {
        return new SliderWidgetHolder(widget, parent, widgetFactory);
    }

    public static class SliderWidgetHolder implements WidgetFactory.IWidgetHolder {

        private View itemView;
        private SeekBar skbDim;
        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private WidgetFactory factory;

        public SliderWidgetHolder(OHWidget widget, OHWidget parent, WidgetFactory factory) {

            this.factory = factory;
            Realm realm = Realm.getDefaultInstance();
            WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
            boolean flat = settings.isCompressedSlider();
            realm.close();

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
        public void update(final OHWidget widget) {
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
                            Openhab.instance(factory.getServer()).sendCommand(widget.getItem().getName(), String.valueOf(skbDim.getProgress()));
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
