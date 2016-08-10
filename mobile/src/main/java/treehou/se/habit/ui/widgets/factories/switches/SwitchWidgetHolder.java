package treehou.se.habit.ui.widgets.factories.switches;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Switch;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.BaseWidgetFactory;
import treehou.se.habit.util.Util;

/**
 * Widget with switch
 */
public class SwitchWidgetHolder implements WidgetFactory.IWidgetHolder {

    private static final String TAG = "SwitchWidgetHolder";

    private BaseWidgetFactory.BaseWidgetHolder baseHolder;
    private Switch swtSwitch;

    public static SwitchWidgetHolder create(WidgetFactory factory, OHWidget widget, OHWidget parent){
        return new SwitchWidgetHolder(widget, parent, factory);
    }

    private SwitchWidgetHolder(final OHWidget widget, OHWidget parent, final WidgetFactory factory) {

        Realm realm = Realm.getDefaultInstance();
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
                .setWidget(widget)
                .setFlat(true)
                .setShowLabel(true)
                .setParent(parent)
                .build();

        float percentage = Util.toPercentage(settings.getTextSize());
        realm.close();

        Log.d(TAG, "Switch state " + widget.getItem().getState() + " : " + widget.getItem().getName());

        View itemView = factory.getInflater().inflate(R.layout.item_widget_switch, null);

        swtSwitch = (Switch) itemView.findViewById(R.id.swt_switch);
        swtSwitch.setTextSize(TypedValue.COMPLEX_UNIT_PX, percentage * swtSwitch.getTextSize());

        IServerHandler serverHandler = new Connector.ServerHandler(factory.getServer(), factory.getContext());
        getView().setOnClickListener(v -> {
            boolean newState = !(swtSwitch.isChecked());
            Log.d(TAG, widget.getLabel() + " " + newState);
            if (widget.getItem() != null && !widget.getItem().getStateDescription().isReadOnly()) {
                swtSwitch.setChecked(newState);
                serverHandler.sendCommand(widget.getItem().getName(), newState ? Constants.COMMAND_ON : Constants.COMMAND_OFF);
            }
        });

        baseHolder.getSubView().addView(itemView);
        update(widget);
    }

    @Override
    public void update(final OHWidget widget) {
        Log.d(TAG, "update " + widget);

        if (widget == null || widget.getItem() == null) {
            return;
        }

        boolean isOn = widget.getItem().getState().equals(Constants.COMMAND_ON);
        swtSwitch.setEnabled(widget.getItem().getStateDescription() == null || !widget.getItem().getStateDescription().isReadOnly());
        swtSwitch.setChecked(isOn);
        baseHolder.update(widget);
    }

    @Override
    public View getView() {
        return baseHolder.getView();
    }
}
