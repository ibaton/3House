package treehou.se.habit.ui.widgets.factories.switches;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHMapping;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.BaseWidgetFactory;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Util;

/**
 * Widget with single button
 */
public class PickerWidgetHolder implements WidgetFactory.IWidgetHolder {

    private static final String TAG = "PickerWidgetHolder";

    private BaseWidgetFactory.BaseWidgetHolder baseHolder;
    private OHServer server;
    private RadioGroup rgpMapping;
    private Context context;
    private ConnectionFactory connectionFactory;

    public static PickerWidgetHolder create(Context context, WidgetFactory factory, ConnectionFactory connectionFactory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent){
        return new PickerWidgetHolder(context, factory, connectionFactory, server, page, widget, parent);
    }

    private PickerWidgetHolder(Context context, final WidgetFactory factory, ConnectionFactory connectionFactory, OHServer server, OHLinkedPage page, final OHWidget widget, OHWidget parent) {

        this.context = context;
        this.server = server;
        this.connectionFactory = connectionFactory;
        baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                .setWidget(widget)
                .setShowLabel(true)
                .setParent(parent)
                .build();

        View itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_switch_mapping, null);
        rgpMapping = (RadioGroup) itemView.findViewById(R.id.rgp_mapping);

        baseHolder.getSubView().addView(itemView);
        update(widget);
    }

    @Override
    public void update(final OHWidget widget) {
        Log.d(TAG, "update " + widget);

        if (widget == null) {
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        float percentage = Util.toPercentage(settings.getTextSize());
        realm.close();

        //TODO do this smother
        rgpMapping.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        for (final OHMapping mapping : widget.getMapping()) {
            RadioButton rbtMap = (RadioButton) layoutInflater.inflate(R.layout.radio_button, null);
            rbtMap.setTextSize(TypedValue.COMPLEX_UNIT_PX, percentage*rbtMap.getTextSize());
            rbtMap.setText(mapping.getLabel());
            rbtMap.setId(rbtMap.hashCode());
            if (widget.getItem().getState().equals(mapping.getCommand())) {
                rbtMap.setChecked(true);
            }

            rbtMap.setOnClickListener(v -> {
                IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
                serverHandler.sendCommand(widget.getItem().getName(), mapping.getCommand());
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
