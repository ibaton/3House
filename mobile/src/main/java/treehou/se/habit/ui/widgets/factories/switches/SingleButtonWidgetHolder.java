package treehou.se.habit.ui.widgets.factories.switches;

import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHMapping;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.BaseWidgetFactory;

/**
 * Widget with single button
 */
public class SingleButtonWidgetHolder implements WidgetFactory.IWidgetHolder {

    private static final String TAG = "SingleButtonBuilder";

    private BaseWidgetFactory.BaseWidgetHolder baseHolder;
    private WidgetFactory factory;

    private Button btnSingle;

    public static SingleButtonWidgetHolder create(WidgetFactory factory, OHWidget widget, OHWidget parent){
        return new SingleButtonWidgetHolder(widget, parent, factory);
    }

    private SingleButtonWidgetHolder(final OHWidget widget, OHWidget parent, final WidgetFactory factory) {
        this.factory = factory;
        Realm realm = Realm.getDefaultInstance();
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(factory)
                .setWidget(widget)
                .setFlat(settings.isCompressedSingleButton())
                .setShowLabel(true)
                .setParent(parent)
                .build();
        realm.close();


        View itemView = factory.getInflater().inflate(R.layout.item_widget_switch_mapping_single, null);
        btnSingle = (Button) itemView.findViewById(R.id.btnSingle);
        if(widget.getMapping().size() == 1){

            OHMapping mapping = widget.getMapping().get(0);
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
    public void update(final OHWidget widget) {
        Log.d(TAG, "update " + widget);

        if (widget == null) {
            return;
        }

        final OHMapping mapSingle = widget.getMapping().get(0);
        btnSingle.setText(mapSingle.getLabel());
        IServerHandler serverHandler = new Connector.ServerHandler(factory.getServer(), factory.getContext());
        btnSingle.setOnClickListener(v -> serverHandler.sendCommand(widget.getItem().getName(), mapSingle.getCommand()));

        baseHolder.update(widget);
    }

    @Override
    public View getView() {
        return baseHolder.getView();
    }
}
