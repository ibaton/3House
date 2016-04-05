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

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.ui.ColorpickerActivity;
import treehou.se.habit.ui.widgets.WidgetFactory;

public class ColorpickerWidgetFactory implements IWidgetFactory {

    private static final String TAG = "ColorpickerWidget";

    @Override
    public WidgetFactory.IWidgetHolder build(final WidgetFactory widgetFactory, OHLinkedPage page, final OHWidget widget, final OHWidget parent) {
        return ColorWidgetHolder.create(widgetFactory, widget, parent);
    }

    static class HoldListener implements View.OnTouchListener {

        private static final int DEFAULT_TICK_TIME=200;

        private int tick=0;
        private Observable<Long> timer;
        private Subscription subscribe = null;
        private OnHoldListener listener;
        private Action1<Long> touchSubject;

        public HoldListener(@NotNull OnHoldListener listener) {
            this(listener, DEFAULT_TICK_TIME);
        }

        public HoldListener(@NotNull final OnHoldListener listener, int tickTime) {
            this.listener = listener;

            timer = Observable.interval(tickTime, TimeUnit.MILLISECONDS);

            touchSubject = new Action1<Long>() {
                @Override
                public void call(Long time) {
                    updateTick();
                }
            };
            timer = Observable.interval(tickTime, TimeUnit.MILLISECONDS).doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    listener.onRelease(tick);
                }
            });
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                subscribe = timer.subscribe(touchSubject);
            } else if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL ||
                    event.getAction() == MotionEvent.ACTION_OUTSIDE) {

                if (!subscribe.isUnsubscribed()) {
                    subscribe.unsubscribe();
                }
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
    public static class ColorWidgetHolder implements WidgetFactory.IWidgetHolder {

        private static final String TAG = "PickerWidgetHolder";

        private BaseWidgetFactory.BaseWidgetHolder baseHolder;
        private int color;
        private View clrView;

        public static ColorWidgetHolder create(WidgetFactory widgetFactory, OHWidget widget, OHWidget parent){
            return new ColorWidgetHolder(widget, parent, widgetFactory);
        }

        private ColorWidgetHolder(final OHWidget widget, OHWidget parent, final WidgetFactory widgetFactory) {

            LayoutInflater inflater = widgetFactory.getInflater();
            final Context context = widgetFactory.getContext();
            View itemView = inflater.inflate(R.layout.item_widget_color, null);
            clrView = itemView.findViewById(R.id.clr_color);

            View btnIncrement = itemView.findViewById(R.id.btn_increment);
            final OHServer server = widgetFactory.getServer();
            final String itemName = widget.getItem().getName();
            btnIncrement.setOnTouchListener(new HoldListener(new HoldListener.OnHoldListener() {
                @Override
                public void onTick(int tick) {
                    if (tick > 0){
                        Openhab.instance(server).sendCommand(itemName, Constants.COMMAND_INCREMENT);
                    }
                }

                @Override
                public void onRelease(int tick) {
                    if (tick <= 0){
                        Openhab.instance(server).sendCommand(itemName, Constants.COMMAND_ON);
                    }
                }
            }));

            View btnDecrement = itemView.findViewById(R.id.btn_decrement);
            btnDecrement.setOnTouchListener(new HoldListener(new HoldListener.OnHoldListener() {
                @Override
                public void onTick(int tick) {
                    if (tick > 0) {
                        Openhab.instance(server).sendCommand(itemName, Constants.COMMAND_DECREMENT);
                    }
                }

                @Override
                public void onRelease(int tick) {
                    if (tick <= 0) {
                        Openhab.instance(server).sendCommand(itemName, Constants.COMMAND_OFF);
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
                        intent.putExtra(ColorpickerActivity.EXTRA_SERVER, widgetFactory.getServerDB().getId());
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
}
