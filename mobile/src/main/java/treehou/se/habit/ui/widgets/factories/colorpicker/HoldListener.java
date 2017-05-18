package treehou.se.habit.ui.widgets.factories.colorpicker;


import android.view.MotionEvent;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

class HoldListener implements View.OnTouchListener {

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

        touchSubject = time -> updateTick();
        timer = Observable.interval(tickTime, TimeUnit.MILLISECONDS).doOnUnsubscribe(() -> listener.onRelease(tick));
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
