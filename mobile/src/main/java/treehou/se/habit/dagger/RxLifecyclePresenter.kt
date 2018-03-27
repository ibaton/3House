package treehou.se.habit.dagger


import android.support.annotation.CheckResult

import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.OutsideLifecycleException
import com.trello.rxlifecycle2.RxLifecycle

import io.reactivex.Observable
import io.reactivex.functions.Function
import treehou.se.habit.dagger.RxPresenterEvent.PresenterEvent

class RxLifecyclePresenter private constructor() {
    init {
        throw AssertionError("No instances")
    }

    companion object {

        @CheckResult
        fun <T> bindPresenter(lifecycle: Observable<PresenterEvent>): LifecycleTransformer<T> {
            return RxLifecycle.bind(lifecycle, PRESENTER_LIFECYCLE)
        }

        private val PRESENTER_LIFECYCLE = Function<PresenterEvent, PresenterEvent>{ lastEvent ->
            when (lastEvent) {
                RxPresenterEvent.PresenterEvent.LOAD -> PresenterEvent.UNLOAD
                RxPresenterEvent.PresenterEvent.SUBSCRIBE -> PresenterEvent.UNSUBSCRIBE
                RxPresenterEvent.PresenterEvent.UNSUBSCRIBE, RxPresenterEvent.PresenterEvent.UNLOAD -> throw OutsideLifecycleException("Cannot bind to Presenter lifecycle when outside of it.")
                else -> throw UnsupportedOperationException("Binding to $lastEvent not yet implemented")
            }
        }
    }

}
