package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHMapping;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.util.ConnectionFactory;

public class SelectionWidgetFactory implements IWidgetFactory {

    private ConnectionFactory connectionFactory;

    public SelectionWidgetFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        return new SelectWidgetHolder(context, connectionFactory, factory, server, page, widget, parent);
    }

    public static class SelectWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "SelectWidgetHolder";

        private View itemView;
        private Spinner sprSelect;
        private WidgetFactory factory;
        private OHServer server;
        private Context context;
        private ConnectionFactory connectionFactory;

        private int lastPosition = -1;

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;

        public SelectWidgetHolder(Context context, ConnectionFactory connectionFactory, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
            this.factory = factory;
            this.context = context;
            this.server = server;
            this.connectionFactory = connectionFactory;

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                    .setWidget(widget)
                    .setParent(parent)
                    .build();

            itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_selection, null);
            sprSelect = (Spinner) itemView.findViewById(R.id.spr_selector);
            baseHolder.getSubView().addView(itemView);

            update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }

        @Override
        public void update(final OHWidget widget) {
            Log.d(TAG, "update " + widget);
            if (widget == null) {
                return;
            }

            sprSelect.setOnItemSelectedListener(null);

            final List<OHMapping> mappings = widget.getMapping();
            final ArrayAdapter<OHMapping> mappingAdapter = new ArrayAdapter<>(context, R.layout.item_text, mappings);
            sprSelect.setAdapter(mappingAdapter);
            for(int i=0; i<mappings.size(); i++){
                if (mappings.get(i).getCommand().equals(widget.getItem().getState())){
                    sprSelect.setSelection(i);
                    lastPosition = i;
                    break;
                }
            }

            //TODO request value
            // Prevents rouge initial fire
            sprSelect.post(new Runnable() {
                @Override
                public void run() {
                    sprSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position != lastPosition) {
                                OHMapping mapping = mappings.get(position);
                                final IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
                                serverHandler.sendCommand(widget.getItem().getName(), mapping.getCommand());
                                lastPosition = position;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                    mappingAdapter.notifyDataSetChanged();
                }
            });

            baseHolder.update(widget);
        }
    }
}
