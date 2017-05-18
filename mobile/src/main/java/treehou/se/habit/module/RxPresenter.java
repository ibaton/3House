package treehou.se.habit.module;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import treehou.se.habit.module.RxPresenterEvent.PresenterEvent;
import treehou.se.habit.mvp.BasePresenter;

public class RxPresenter implements BasePresenter {

    private final BehaviorSubject<PresenterEvent> lifecycleSubject = BehaviorSubject.create();

    @NonNull
    @CheckResult
    public final Observable<PresenterEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @NonNull
    @CheckResult
    public final <K> LifecycleTransformer<K> bindUntilEvent(@NonNull PresenterEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @NonNull
    @CheckResult
    public final <K> LifecycleTransformer<K> bindToLifecycle() {
        return RxLifecyclePresenter.bindPresenter(lifecycleSubject);
    }

    @Override
    @CallSuper
    public void load(Bundle savedData) {
        lifecycleSubject.onNext(PresenterEvent.LOAD);
    }

    @Override
    @CallSuper
    public void subscribe() {
        lifecycleSubject.onNext(PresenterEvent.SUBSCRIBE);
    }

    @Override
    @CallSuper
    public void unsubscribe() {
        lifecycleSubject.onNext(PresenterEvent.UNSUBSCRIBE);
    }

    @Override
    @CallSuper
    public void unload() {
        lifecycleSubject.onNext(PresenterEvent.UNLOAD);
    }

    @Override
    public void save(Bundle savedData) {
    }
}
