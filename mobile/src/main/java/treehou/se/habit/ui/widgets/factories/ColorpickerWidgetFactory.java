package treehou.se.habit.ui.widgets.factories;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.connector.GsonHelper;
import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.ui.ColorpickerActivity;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class ColorpickerWidgetFactory implements IWidgetFactory {

    private static final String TAG = "ColorpickerWidget";

    @Override
    public WidgetFactory.IWidgetHolder build(final WidgetFactory widgetFactory, LinkedPage page, final Widget widget, final Widget parent) {
        return ColorWidgetHolder.create(widgetFactory, widget, parent);
    }

    static class HoldListener implements View.OnTouchListener {

        private static final int DEFAULT_TICK_TIME=200;

        private int tickTime;
        private int tick=0;
        private Timer timer;

        private OnHoldListener listener;

        public HoldListener(@NotNull OnHoldListener listener) {
            this(listener, DEFAULT_TICK_TIME);
        }

        public HoldListener(@NotNull OnHoldListener listener, int tickTime) {
            this.tickTime = tickTime;
            this.listener = listener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                tick = 0;
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        updateTick();
                    }
                }, tickTime, tickTime);
            } else if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL ||
                    event.getAction() == MotionEvent.ACTION_OUTSIDE) {

                timer.cancel();
                listener.onRelease(tick);
            }
            return true;
        }

        private void updateTick(){
            tick++;
            listener.onTick(tick);
        }

        interface OnHoldListener {
            void onTick(int tick);
            void onRelease(int tick);
        }
    }

    /**
     * Color widget
     */
    static class ColorWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "PickerWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private int color;
        private View clrView1;

        public static ColorWidgetHolder create(WidgetFactory widgetFactory, Widget widget, Widget parent){
            return new ColorWidgetHolder(widget, parent, widgetFactory);
        }

        private ColorWidgetHolder(final Widget widget, Widget parent, final WidgetFactory widgetFactory) {

            LayoutInflater inflater = widgetFactory.getInflater();
            final Context context = widgetFactory.getContext();
            View itemView = inflater.inflate(R.layout.item_widget_color, null);
            clrView1 = itemView.findViewById(R.id.clr_color);

            View btnIncrement = itemView.findViewById(R.id.btn_increment);
            btnIncrement.setOnTouchListener(new HoldListener(new HoldListener.OnHoldListener() {
                @Override
                public void onTick(int tick) {
                    if (tick > 0){
                        Communicator.instance(context).command(widgetFactory.getServer(), widget.getItem(), Constants.COMMAND_INCREMENT);
                    }
                }

                @Override
                public void onRelease(int tick) {
                    if (tick <= 0){
                        Communicator.instance(context).command(widgetFactory.getServer(), widget.getItem(), Constants.COMMAND_ON);
                    }
                }
            }));

            View btnDecrement = itemView.findViewById(R.id.btn_decrement);
            btnDecrement.setOnTouchListener(new HoldListener(new HoldListener.OnHoldListener() {
                @Override
                public void onTick(int tick) {
                    if (tick > 0) {
                        Communicator.instance(context).command(widgetFactory.getServer(), widget.getItem(), Constants.COMMAND_DECREMENT);
                    }
                }

                @Override
                public void onRelease(int tick) {
                    if (tick <= 0) {
                        Communicator.instance(context).command(widgetFactory.getServer(), widget.getItem(), Constants.COMMAND_OFF);
                    }
                }
            }));

            baseHolder = new BaseWidgetFactory.BaseWidgetHolder.Builder(widgetFactory)
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
                        intent.putExtra(ColorpickerActivity.EXTRA_SERVER, widgetFactory.getServer().getId());
                        intent.putExtra(ColorpickerActivity.EXTRA_WIDGET, gson.toJson(widget));
                        intent.putExtra(ColorpickerActivity.EXTRA_COLOR, color);

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
            clrView1.setBackgroundColor(color);
        }

        @Override
        public void update(final Widget widget) {
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
}
