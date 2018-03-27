package treehou.se.habit.dagger


import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult

import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle

import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import treehou.se.habit.dagger.RxPresenterEvent.PresenterEvent
import treehou.se.habit.mvp.BasePresenter

open class RxPresenter : BasePresenter {

    private val lifecycleSubject = BehaviorSubject.create<PresenterEvent>()

    @CheckResult
    fun lifecycle(): Observable<PresenterEvent> {
        return lifecycleSubject.toFlowable(BackpressureStrategy.BUFFER).toObservable()
    }

    @CheckResult
    fun <K> bindUntilEvent(event: PresenterEvent): LifecycleTransformer<K> {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

    @CheckResult
    fun <K> bindToLifecycle(): LifecycleTransformer<K> {
        return RxLifecyclePresenter.bindPresenter(lifecycleSubject)
    }

    @CallSuper
    override fun load(launchData: Bundle?, savedData: Bundle?) {
        lifecycleSubject.onNext(PresenterEvent.LOAD)
    }

    @CallSuper
    override fun subscribe() {
        lifecycleSubject.onNext(PresenterEvent.SUBSCRIBE)
    }

    @CallSuper
    override fun unsubscribe() {
        lifecycleSubject.onNext(PresenterEvent.UNSUBSCRIBE)
    }

    @CallSuper
    override fun unload() {
        lifecycleSubject.onNext(PresenterEvent.UNLOAD)
    }

    override fun save(savedData: Bundle?) {}
}
