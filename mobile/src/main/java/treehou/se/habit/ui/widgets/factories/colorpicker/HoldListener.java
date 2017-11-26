package treehou.se.habit.ui.widgets.factories.colorpicker;


import android.view.MotionEvent;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

class HoldListener implements View.OnTouchListener {

    private static final int DEFAULT_TICK_TIME=200;

    private int tick=0;
    private Observable<Long> timer;
    private Disposable disposable = null;
    private OnHoldListener listener;
    private Consumer<Long> touchSubject;

    public HoldListener(@NotNull OnHoldListener listener) {
        this(listener, DEFAULT_TICK_TIME);
    }

    public HoldListener(@NotNull final OnHoldListener listener, int tickTime) {
        this.listener = listener;

        timer = Observable.interval(tickTime, TimeUnit.MILLISECONDS);

        touchSubject = time -> updateTick();
        timer = Observable.interval(tickTime, TimeUnit.MILLISECONDS).doOnDispose(() -> listener.onRelease(tick));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            disposable = timer.subscribe(touchSubject);
        } else if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL ||
                event.getAction() == MotionEvent.ACTION_OUTSIDE) {

            if (!disposable.isDisposed()) {
                disposable.dispose();
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
