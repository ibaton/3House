package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class SetpointWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {
        return new SetpointWidgetHolder(context, factory, server, page, widget, parent);
    }

    public static class SetpointWidgetHolder implements WidgetFactory.IWidgetHolder {

        private View itemView;
        private Button btnIncrease;
        private Button btnDecrease;
        private TextView lblValue;

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private Context context;
        private OHServer server;

        public SetpointWidgetHolder(Context context, WidgetFactory factory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {

            this.context = context;
            this.server = server;

            itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_setpoint, null);
            btnDecrease = (Button) itemView.findViewById(R.id.btn_down);
            btnIncrease = (Button) itemView.findViewById(R.id.btn_up);
            lblValue = (TextView) itemView.findViewById(R.id.lbl_widget_point);

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                    .setWidget(widget)
                    .setFlat(true)
                    .setParent(parent)
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

            if(widget.getItem() != null) {
                lblValue.setText(widget.getItem().getFormatedValue());
            }

            btnIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setValueRelative(widget, widget.getStep());
                }
            });

            btnDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setValueRelative(widget, -widget.getStep());
                }
            });

            baseHolder.update(widget);
        }

        private void setValueRelative(OHWidget widget, float value) {
            if(widget.getItem() == null) {
                return;
            }

            lblValue.setText("" + value);

            float setpointValue = widget.getMinValue();
            try {
                if(widget.getItem() != null) {
                    setpointValue = Float.valueOf(widget.getItem().getState())+value;
                }
            }catch (NumberFormatException e){
                return;
            }

            setpointValue = Math.min(Math.max(setpointValue, widget.getMinValue()), widget.getMaxValue());

            setValue(widget, setpointValue);
        }

        private void setValue(OHWidget widget, float value) {

            String state = String.valueOf(value);
            if(widget.getItem() != null) {
                try {
                    widget.getItem().setState(state);
                    if(widget.getItem() != null) {
                        lblValue.setText(widget.getItem().getFormatedValue());
                    }

                    /*IServerHandler serverHandler = new Connector.ServerHandler(server, context);
                    serverHandler.sendCommand(widget.getItem().getName(), String.valueOf(value)); TODO fix*/
                } catch (Exception e) {}
            }
        }
    }
}
