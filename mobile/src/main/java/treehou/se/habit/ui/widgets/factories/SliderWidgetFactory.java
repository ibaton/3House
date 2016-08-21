package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import javax.inject.Inject;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.util.ConnectionFactory;

public class SliderWidgetFactory implements IWidgetFactory {

    private static final String TAG = "SliderWidgetFactory";

    private ConnectionFactory connectionFactory;

    public SliderWidgetFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        return new SliderWidgetHolder(context, factory, connectionFactory, server, page, widget, parent);
    }

    public static class SliderWidgetHolder implements WidgetFactory.IWidgetHolder {

        private View itemView;
        private SeekBar skbDim;
        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private Context context;
        private OHServer server;
        private ConnectionFactory connectionFactory;

        public SliderWidgetHolder(Context context, WidgetFactory factory, ConnectionFactory connectionFactory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {

            this.server = server;
            this.context = context;
            this.connectionFactory = connectionFactory;

            Realm realm = Realm.getDefaultInstance();
            WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
            boolean flat = settings.isCompressedSlider();
            realm.close();

            itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_slider, null);
            skbDim = (SeekBar) itemView.findViewById(R.id.skb_dim);

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
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
                            IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
                            serverHandler.sendCommand(widget.getItem().getName(), String.valueOf(skbDim.getProgress()));
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
