package treehou.se.habit.ui.widgets.factories.switches;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHMapping;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.BaseWidgetFactory;
import treehou.se.habit.util.ConnectionFactory;

/**
 * Widget with single button
 */
public class SingleButtonWidgetHolder implements WidgetFactory.IWidgetHolder {

    private static final String TAG = "SingleButtonBuilder";

    private BaseWidgetFactory.BaseWidgetHolder baseHolder;
    private ConnectionFactory connectionFactory;
    private WidgetFactory factory;
    private Context context;
    private OHServer server;

    private Button btnSingle;

    public static SingleButtonWidgetHolder create(Context context, WidgetFactory factory, ConnectionFactory connectionFactory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent){
        return new SingleButtonWidgetHolder(context, connectionFactory, server, page, widget, parent, factory);
    }

    private SingleButtonWidgetHolder(Context context, ConnectionFactory connectionFactory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent, final WidgetFactory factory) {
        this.server = server;
        this.factory = factory;
        this.context = context;
        this.connectionFactory = connectionFactory;
        Realm realm = Realm.getDefaultInstance();
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                .setWidget(widget)
                .setFlat(settings.isCompressedSingleButton())
                .setShowLabel(true)
                .setParent(parent)
                .build();
        realm.close();


        View itemView = LayoutInflater.from(context).inflate(R.layout.item_widget_switch_mapping_single, null);
        btnSingle = (Button) itemView.findViewById(R.id.btnSingle);
        if(widget.getMapping().size() == 1){

            OHMapping mapping = widget.getMapping().get(0);
            if(widget.getItem() != null && mapping.getCommand().equals(widget.getItem().getState())) {
                btnSingle.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            }
            else {
                btnSingle.getBackground().clearColorFilter();
            }
        }

        baseHolder.getSubView().addView(itemView);
        update(widget);
    }

    @Override
    public void update(final OHWidget widget) {
        Log.d(TAG, "update " + widget);

        if (widget == null) {
            return;
        }

        final OHMapping mapSingle = widget.getMapping().get(0);
        btnSingle.setText(mapSingle.getLabel());
        IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
        btnSingle.setOnClickListener(v -> serverHandler.sendCommand(widget.getItem().getName(), mapSingle.getCommand()));

        baseHolder.update(widget);
    }

    @Override
    public View getView() {
        return baseHolder.getView();
    }
}
