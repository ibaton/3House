package treehou.se.habit.ui.widgets.factories;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class SetpointWidgetFactory implements IWidgetFactory {

    @Override
    public WidgetFactory.IWidgetHolder build(final WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {
        return new SetpointWidgetHolder(widget, parent, widgetFactory);
    }

    public static class SetpointWidgetHolder implements WidgetFactory.IWidgetHolder {

        private View itemView;
        private Button btnIncrease;
        private Button btnDecrease;
        private TextView lblValue;

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private WidgetFactory factory;

        public SetpointWidgetHolder(Widget widget, Widget parent, WidgetFactory factory) {

            this.factory = factory;
            WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(factory.getContext());

            itemView = factory.getInflater().inflate(R.layout.item_widget_setpoint, null);
            btnDecrease = (Button) itemView.findViewById(R.id.btn_down);
            btnIncrease = (Button) itemView.findViewById(R.id.btn_up);
            lblValue = (TextView) itemView.findViewById(R.id.lbl_widget_point);

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
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
        public void update(final Widget widget) {
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

        private void setValueRelative(Widget widget, int value) {
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

        private void setValue(Widget widget, float value) {

            String state = String.valueOf(value);
            if(widget.getItem() != null) {
                try {
                    widget.getItem().setState(state);
                    if(widget.getItem() != null) {
                        lblValue.setText(widget.getItem().getFormatedValue());
                    }

                    Communicator communicator = Communicator.instance(factory.getContext());
                    communicator.command(factory.getServer(), widget.getItem(), String.valueOf(value));
                } catch (Exception e) {}
            }
        }
    }
}
