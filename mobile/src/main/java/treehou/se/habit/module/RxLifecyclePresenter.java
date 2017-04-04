package treehou.se.habit.module;


import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.OutsideLifecycleException;
import com.trello.rxlifecycle.RxLifecycle;

import rx.Observable;
import rx.functions.Func1;
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

    private static final Func1<PresenterEvent, PresenterEvent> PRESENTER_LIFECYCLE = lastEvent -> {
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
