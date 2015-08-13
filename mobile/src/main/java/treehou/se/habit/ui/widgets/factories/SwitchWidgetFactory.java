package treehou.se.habit.ui.widgets.factories;

import android.graphics.PorterDuff;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.ItemDB;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.util.Util;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class SwitchWidgetFactory implements IWidgetFactory {

    private static final String TAG = "SwitchWidgetFactory";

    @Override
    public WidgetFactory.IWidgetHolder build(
            WidgetFactory widgetFactory, LinkedPage page,
            final Widget widget, final Widget parent) {

        if(widget.getMapping() == null) {
            final ItemDB item = widget.getItem();
            if (item == null){
                return new NullWidgetFactory().build(widgetFactory, page, widget, parent);
            }

            Log.d(TAG, "Type " + item.getType());
            if(item.getType().equals(ItemDB.TYPE_ROLLERSHUTTER)){
                return RollerShutterWidgetHolder.create(widgetFactory, widget, parent);
            }else{
                Log.d(TAG, "Switch state " + widget.getItem().getState() + " : " + widget.getItem().getName());
                return SwitchWidgetHolder.create(widgetFactory, widget, parent);
            }
        } else {
            if(widget.getMapping().size() == 1) {
                return SingleButtonWidgetHolder.create(widgetFactory, widget, parent);
            }else {
                return PickerWidgetHolder.create(widgetFactory, widget, parent);
            }
        }
    }


    /**
     * Widget rollershutters
     */
    static class RollerShutterWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "RollerShutterWidgetHold";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;

        public static RollerShutterWidgetHolder create(WidgetFactory factory, Widget widget, Widget parent){
            return new RollerShutterWidgetHolder(widget, parent, factory);
        }

        private RollerShutterWidgetHolder(final Widget widget, Widget parent, final WidgetFactory factory) {

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
                    .setWidget(widget)
                    .setShowLabel(true)
                    .setParent(parent)
                    .build();

            final ItemDB item = widget.getItem();
            View itemView = factory.getInflater().inflate(R.layout.item_widget_rollershutters, null);

            ImageButton btnUp = (ImageButton) itemView.findViewById(R.id.btn_up);
            btnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (widget.getItem() != null) {
                        Communicator communicator = Communicator.instance(factory.getContext());
                        communicator.command(factory.getServer(), widget.getItem(), Constants.COMMAND_UP);
                    }
                }
            });

            ImageButton btnCancel = (ImageButton) itemView.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (widget.getItem() != null) {
                        Communicator communicator = Communicator.instance(factory.getContext());
                        communicator.command(factory.getServer(), item, Constants.COMMAND_STOP);
                    }
                }
            });

            ImageButton btnDown = (ImageButton) itemView.findViewById(R.id.btn_down);
            btnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (widget.getItem() != null) {
                        Communicator communicator = Communicator.instance(factory.getContext());
                        communicator.command(factory.getServer(), item, Constants.COMMAND_DOWN);
                    }
                }
            });

            baseHolder.getSubView().addView(itemView);
            update(widget);
        }

        @Override
        public void update(final Widget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }

            baseHolder.update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }
    }


    /**
     * Widget with single button
     */
    static class PickerWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "PickerWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private WidgetFactory factory;
        private RadioGroup rgpMapping;

        public static PickerWidgetHolder create(WidgetFactory factory, Widget widget, Widget parent){
            return new PickerWidgetHolder(widget, parent, factory);
        }

        private PickerWidgetHolder(final Widget widget, Widget parent, final WidgetFactory factory) {

            this.factory = factory;
            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
                    .setWidget(widget)
                    .setShowLabel(true)
                    .setParent(parent)
                    .build();

            View itemView = factory.getInflater().inflate(R.layout.item_widget_switch_mapping, null);
            rgpMapping = (RadioGroup) itemView.findViewById(R.id.rgp_mapping);

            baseHolder.getSubView().addView(itemView);
            update(widget);
        }

        @Override
        public void update(final Widget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }

            WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(factory.getContext());

            //TODO do this smother
            rgpMapping.removeAllViews();
            for (final Widget.Mapping mapping : widget.getMapping()) {
                RadioButton rbtMap = (RadioButton) factory.getInflater().inflate(R.layout.radio_button, null);
                float percentage = Util.toPercentage(settings.getTextSize());
                rbtMap.setTextSize(TypedValue.COMPLEX_UNIT_PX, percentage*rbtMap.getTextSize());
                rbtMap.setText(mapping.getLabel());
                rbtMap.setId(rbtMap.hashCode());
                if (widget.getItem().getState().equals(mapping.getCommand())) {
                    rbtMap.setChecked(true);
                }
                rbtMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            Communicator communicator = Communicator.instance(factory.getContext());
                            communicator.command(factory.getServer(), widget.getItem(), mapping.getCommand());
                        }
                    }
                });
                rgpMapping.addView(rbtMap);
            }

            baseHolder.update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }
    }


    /**
     * Widget with single button
     */
    static class SingleButtonWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "SingleButtonBuilder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private WidgetFactory factory;

        private Button btnSingle;

        public static SingleButtonWidgetHolder create(WidgetFactory factory, Widget widget, Widget parent){
            return new SingleButtonWidgetHolder(widget, parent, factory);
        }

        private SingleButtonWidgetHolder(final Widget widget, Widget parent, final WidgetFactory factory) {
            this.factory = factory;
            WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(factory.getContext());
            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
                    .setWidget(widget)
                    .setFlat(settings.isCompressedSingleButton())
                    .setShowLabel(true)
                    .setParent(parent)
                    .build();


            View itemView = factory.getInflater().inflate(R.layout.item_widget_switch_mapping_single, null);
            btnSingle = (Button) itemView.findViewById(R.id.btnSingle);
            if(widget.getMapping().size() == 1){

                Widget.Mapping mapping = widget.getMapping().get(0);

                Communicator communicator = Communicator.instance(factory.getContext());
                communicator.command(factory.getServer(), widget.getItem(), mapping.getCommand());

                if(widget.getItem() != null && mapping.getCommand().equals(widget.getItem().getState())) {
                    btnSingle.getBackground().setColorFilter(factory.getContext().getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                }
                else {
                    btnSingle.getBackground().clearColorFilter();
                }
            }

            baseHolder.getSubView().addView(itemView);
            update(widget);
        }

        @Override
        public void update(final Widget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }

            final Widget.Mapping mapSingle = widget.getMapping().get(0);
            btnSingle.setText(mapSingle.getLabel());
            btnSingle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Communicator communicator = Communicator.instance(factory.getContext());
                    communicator.command(factory.getServer(), widget.getItem(), mapSingle.getCommand());
                }
            });

            baseHolder.update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }
    }


    /**
     * Widget with switch
     */
    static class SwitchWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "SwitchWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private SwitchCompat swtSwitch;

        public static SwitchWidgetHolder create(WidgetFactory factory, Widget widget, Widget parent){
            return new SwitchWidgetHolder(widget, parent, factory);
        }

        private SwitchWidgetHolder(final Widget widget, Widget parent, final WidgetFactory factory) {

            WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(factory.getContext());
            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
                    .setWidget(widget)
                    .setFlat(true)
                    .setShowLabel(true)
                    .setParent(parent)
                    .build();

            Log.d(TAG, "Switch state " + widget.getItem().getState() + " : " + widget.getItem().getName());

            View itemView = factory.getInflater().inflate(R.layout.item_widget_switch, null);

            swtSwitch = (SwitchCompat) itemView.findViewById(R.id.swt_switch);
            float percentage = Util.toPercentage(settings.getTextSize());
            swtSwitch.setTextSize(TypedValue.COMPLEX_UNIT_PX, percentage * swtSwitch.getTextSize());

            getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean newState = !(swtSwitch.isChecked());
                    Log.d(TAG, widget.getLabel() + " " + newState);
                    if (widget.getItem() != null) {
                        swtSwitch.setChecked(newState);
                        Communicator communicator = Communicator.instance(factory.getContext());
                        communicator.command(factory.getServer(), widget.getItem(), newState ? Constants.COMMAND_ON : Constants.COMMAND_OFF);
                    }
                }
            });

            baseHolder.getSubView().addView(itemView);
            update(widget);
        }

        @Override
        public void update(final Widget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }

            swtSwitch.setChecked(widget.getItem().getState().equals(Constants.COMMAND_ON));
            baseHolder.update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }
    }
}
