package treehou.se.habit.module;


import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.OutsideLifecycleException;
import com.trello.rxlifecycle2.RxLifecycle;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import treehou.se.habit.module.RxPresenterEvent.PresenterEvent;

public class RxLifecyclePresenter {
    private RxLifecyclePresenter() {
        throw new AssertionError("No instances");
    }

    @NonNull
    @CheckResult
    public static <T> LifecycleTransformer<T> bindPresenter(@NonNull final Observable<PresenterEvent> lifecycle) {
        return RxLifecycle.bind(lifecycle, PRESENTER_LIFECYCLE);
    }

    private static final Function<PresenterEvent, PresenterEvent> PRESENTER_LIFECYCLE = lastEvent -> {
        switch (lastEvent) {
            case LOAD:
                return PresenterEvent.UNLOAD;
            case SUBSCRIBE:
                return PresenterEvent.UNSUBSCRIBE;
            case UNSUBSCRIBE:
            case UNLOAD:
                throw new OutsideLifecycleException("Cannot bind to Presenter lifecycle when outside of it.");
            default:
                throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
        }
    };

}
