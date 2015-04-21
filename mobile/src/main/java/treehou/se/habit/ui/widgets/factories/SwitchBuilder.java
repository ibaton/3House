package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.net.MalformedURLException;
import java.net.URL;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.ConnectorUtil;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.Item;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Server;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.settings.WidgetSettings;
import treehou.se.habit.ui.Util;
import treehou.se.habit.ui.widgets.WidgetFactory;

/**
 * Created by ibaton on 2014-10-19.
 */
public class SwitchBuilder implements IWidgetBuilder {

    private static final String TAG = "SwitchBuilder";

    @Override
    public WidgetFactory.IWidgetHolder build(
            WidgetFactory widgetFactory, LinkedPage page,
            final Widget widget, final Widget parent) {

        if(widget.getMapping() == null) {
            final Item item = widget.getItem();
            if (item == null){
                return new NullBuilder().build(widgetFactory, page, widget, parent);
            }

            Log.d(TAG, "Type " + item.getType());
            if(item.getType().equals(Item.TYPE_ROLLERSHUTTER)){
                return RollerShutterBuilderHolder.create(widgetFactory, widget, parent);
            }else{
                Log.d(TAG, "Switch state " + widget.getItem().getState() + " : " + widget.getItem().getName());
                return SwitchBuilderHolder.create(widgetFactory, widget, parent);
            }
        } else {
            if(widget.getMapping().size() == 1) {
                return SingleButtonBuilderHolder.create(widgetFactory, widget, parent);
            }else {
                return PickerBuilderHolder.create(widgetFactory, widget, parent);
            }
        }
    }

    /**
     * Widget rollershutters
     */
    static class RollerShutterBuilderHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "SliderBuilderHolder";

        private BaseBuilder.BaseBuilderHolder baseHolder;
        private WidgetFactory factory;

        public static RollerShutterBuilderHolder create(WidgetFactory factory, Widget widget, Widget parent){
            return new RollerShutterBuilderHolder(widget, parent, factory);
        }

        private RollerShutterBuilderHolder(final Widget widget, Widget parent, final WidgetFactory factory) {

            this.factory = factory;
            baseHolder = new BaseBuilder.BaseBuilderHolder.Builder(factory)
                    .setWidget(widget)
                    .setShowLabel(true)
                    .setParent(parent)
                    .build();

            final Item item = widget.getItem();
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
    static class PickerBuilderHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "SliderBuilderHolder";

        private BaseBuilder.BaseBuilderHolder baseHolder;
        private WidgetFactory factory;
        private RadioGroup rgpMapping;

        public static PickerBuilderHolder create(WidgetFactory factory, Widget widget, Widget parent){
            return new PickerBuilderHolder(widget, parent, factory);
        }

        private PickerBuilderHolder(final Widget widget, Widget parent, final WidgetFactory factory) {

            this.factory = factory;
            baseHolder = new BaseBuilder.BaseBuilderHolder.Builder(factory)
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

            WidgetSettings settings = WidgetSettings.loadGlobal(factory.getContext());

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
    static class SingleButtonBuilderHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "SliderBuilderHolder";

        private BaseBuilder.BaseBuilderHolder baseHolder;
        private WidgetFactory factory;

        private Button btnSingle;

        public static SingleButtonBuilderHolder create(WidgetFactory factory, Widget widget, Widget parent){
            return new SingleButtonBuilderHolder(widget, parent, factory);
        }

        private SingleButtonBuilderHolder(final Widget widget, Widget parent, final WidgetFactory factory) {
            this.factory = factory;
            WidgetSettings settings = WidgetSettings.loadGlobal(factory.getContext());
            baseHolder = new BaseBuilder.BaseBuilderHolder.Builder(factory)
                    .setWidget(widget)
                    .setFlat(settings.isCompressedSingleButton())
                    .setShowLabel(true)
                    .setParent(parent)
                    .build();


            View itemView = factory.getInflater().inflate(R.layout.item_widget_switch_mapping_single, null);

            btnSingle = (Button) itemView.findViewById(R.id.btnSingle);

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
    static class SwitchBuilderHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "SliderBuilderHolder";

        private BaseBuilder.BaseBuilderHolder baseHolder;
        private SwitchCompat swtSwitch;
        private CompoundButton.OnCheckedChangeListener switchListener;

        public static SwitchBuilderHolder create(WidgetFactory factory, Widget widget, Widget parent){
            return new SwitchBuilderHolder(widget, parent, factory);
        }

        private SwitchBuilderHolder(final Widget widget, Widget parent, final WidgetFactory factory) {

            WidgetSettings settings = WidgetSettings.loadGlobal(factory.getContext());
            baseHolder = new BaseBuilder.BaseBuilderHolder.Builder(factory)
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

            switchListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG, widget.getLabel() + " " + isChecked);
                    if (widget.getItem() != null) {
                        Communicator communicator = Communicator.instance(factory.getContext());
                        communicator.command(factory.getServer(), widget.getItem(), isChecked ? Constants.COMMAND_ON : Constants.COMMAND_OFF);
                    }
                }
            };

            baseHolder.getSubView().addView(itemView);
            update(widget);
        }

        @Override
        public void update(final Widget widget) {
            Log.d(TAG, "update " + widget);

            if (widget == null) {
                return;
            }

            swtSwitch.setOnCheckedChangeListener(null);
            swtSwitch.setChecked(widget.getItem().getState().equals(Constants.COMMAND_ON));
            swtSwitch.setOnCheckedChangeListener(switchListener);

            baseHolder.update(widget);
        }

        @Override
        public View getView() {
            return baseHolder.getView();
        }
    }
}
