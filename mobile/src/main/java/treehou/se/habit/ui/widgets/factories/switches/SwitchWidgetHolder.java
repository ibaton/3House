package treehou.se.habit.ui.widgets.factories.switches;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.BaseWidgetFactory;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Util;

/**
 * WidgetFactory with switch
 */
public class SwitchWidgetHolder implements WidgetFactory.IWidgetHolder {

    private static final String TAG = "SwitchWidgetHolder";

    private BaseWidgetFactory.BaseWidgetHolder baseHolder;
    private Switch swtSwitch;
    private ConnectionFactory connectionFactory;

    public static SwitchWidgetHolder create(Context context, WidgetFactory factory, ConnectionFactory connectionFactory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent){
        return new SwitchWidgetHolder(context, factory, connectionFactory, server, page, widget, parent);
    }

    private SwitchWidgetHolder(Context context, final WidgetFactory factory, ConnectionFactory connectionFactory, OHServer server, OHLinkedPage page, final OHWidget widget, OHWidget parent) {

        this.connectionFactory = connectionFactory;
        Realm realm = Realm.getDefaultInstance();
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                .setWidget(widget)
                .setFlat(true)
                .setShowLabel(true)
                .setParent(parent)
                .build();

        float percentage = Util.INSTANCE.toPercentage(settings.getTextSize());
        realm.close();

        Log.d(TAG, "Switch state " + widget.getItem().getState() + " : " + widget.getItem().getName());

        View itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_switch, null);

        swtSwitch = (Switch) itemView.findViewById(R.id.swt_switch);
        swtSwitch.setTextSize(TypedValue.COMPLEX_UNIT_PX, percentage * swtSwitch.getTextSize());

        IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
        getView().setOnClickListener(v -> {
            boolean newState = !(swtSwitch.isChecked());
            Log.d(TAG, widget.getLabel() + " " + newState);
            if (widget.getItem() != null && !widget.getItem().getStateDescription().isReadOnly()) {
                swtSwitch.setChecked(newState);
                serverHandler.sendCommand(widget.getItem().getName(), newState ? Constants.INSTANCE.getCOMMAND_ON() : Constants.INSTANCE.getCOMMAND_OFF());
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

        boolean isOn = widget.getItem().getState().equals(Constants.INSTANCE.getCOMMAND_ON());
        swtSwitch.setEnabled(widget.getItem().getStateDescription() == null || !widget.getItem().getStateDescription().isReadOnly());
        swtSwitch.setChecked(isOn);
        baseHolder.update(widget);
    }

    @Override
    public View getView() {
        return baseHolder.getView();
    }
}
