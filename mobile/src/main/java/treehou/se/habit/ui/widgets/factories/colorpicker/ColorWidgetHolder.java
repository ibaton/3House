package treehou.se.habit.ui.widgets.factories.colorpicker;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import treehou.se.habit.R;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.ui.colorpicker.ColorpickerActivity;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.BaseWidgetFactory;
import treehou.se.habit.util.ConnectionFactory;

/**
 * Color widget
 */
public class ColorWidgetHolder implements WidgetFactory.IWidgetHolder {

    private static final String TAG = "PickerWidgetHolder";

    private BaseWidgetFactory.BaseWidgetHolder baseHolder;
    private int color;
    private View clrView;

    ColorWidgetHolder(Context context, WidgetFactory factory, ConnectionFactory connectionFactory, OHServer server, OHLinkedPage page, OHWidget widget, OHWidget parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_widget_color, null);
        clrView = itemView.findViewById(R.id.clr_color);

        View btnIncrement = itemView.findViewById(R.id.btn_increment);
        final String itemName = widget.getItem().getName();

        IServerHandler serverHandler = connectionFactory.createServerHandler(server, context);
        btnIncrement.setOnTouchListener(new HoldListener(new HoldListener.OnHoldListener() {
            @Override
            public void onTick(int tick) {
                if (tick > 0){
                    serverHandler.sendCommand(itemName, Constants.INSTANCE.getCOMMAND_INCREMENT());
                }
            }

            @Override
            public void onRelease(int tick) {
                if (tick <= 0){
                    serverHandler.sendCommand(itemName, Constants.INSTANCE.getCOMMAND_ON());
                }
            }
        }));

        View btnDecrement = itemView.findViewById(R.id.btn_decrement);
        btnDecrement.setOnTouchListener(new HoldListener(new HoldListener.OnHoldListener() {
            @Override
            public void onTick(int tick) {
                if (tick > 0) {
                    serverHandler.sendCommand(itemName, Constants.INSTANCE.getCOMMAND_DECREMENT());
                }
            }

            @Override
            public void onRelease(int tick) {
                if (tick <= 0) {
                    serverHandler.sendCommand(itemName, Constants.INSTANCE.getCOMMAND_OFF());
                }
            }
        }));

        baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(context, factory, server, page)
                .setWidget(widget)
                .setFlat(true)
                .setShowLabel(true)
                .setView(itemView)
                .setParent(parent)
                .build();

        baseHolder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (widget.getItem() != null) {
                    Intent intent = new Intent(context, ColorpickerActivity.class);
                    Gson gson = GsonHelper.createGsonBuilder();
                    intent.putExtra(ColorpickerActivity.Companion.getEXTRA_SERVER(), server.getId());
                    intent.putExtra(ColorpickerActivity.Companion.getEXTRA_WIDGET(), gson.toJson(widget));
                    intent.putExtra(ColorpickerActivity.Companion.getEXTRA_COLOR(), color);

                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, context.getString(R.string.item_missing), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Widget doesn't contain item");
                }
            }
        });

        update(widget);
    }

    private void setColor(int color){
        clrView.setBackgroundColor(color);
        clrView.setVisibility(View.GONE);
    }

    @Override
    public void update(final OHWidget widget) {
        Log.d(TAG, "update " + widget);

        if (widget == null) {
            return;
        }

        color = Color.TRANSPARENT;
        if(widget.getItem() != null && widget.getItem().getState() != null) {
            String[] sHSV = widget.getItem().getState().split(",");
            if (sHSV.length == 3) {
                float[] hSV = {
                        Float.valueOf(sHSV[0]),
                        Float.valueOf(sHSV[1]),
                        Float.valueOf(sHSV[2])};
                color = Color.HSVToColor(hSV);
            }
        }
        setColor(color);

        baseHolder.update(widget);
    }

    @Override
    public View getView() {
        return baseHolder.getView();
    }
}
