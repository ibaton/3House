package treehou.se.habit.ui.widget


import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

import java.util.concurrent.TimeUnit

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

class HoldListener @JvmOverloads constructor(private val tickListener: OnTickListener, private val releaseListener: OnReleaseListener, tickTime: Int = DEFAULT_TICK_TIME) : View.OnTouchListener {

    private var tick = 0
    private var timer: Observable<Long>? = null
    private var disposable: Disposable? = null
    private val touchSubject: Consumer<Long>

    init {

        timer = Observable.interval(tickTime.toLong(), TimeUnit.MILLISECONDS)
        touchSubject = Consumer { updateTick() }
        timer = Observable.interval(tickTime.toLong(), TimeUnit.MILLISECONDS).doOnDispose { releaseListener.onRelease(tick) }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            disposable = timer!!.subscribe(touchSubject)
        } else if (event.action == MotionEvent.ACTION_UP ||
                event.action == MotionEvent.ACTION_CANCEL ||
                event.action == MotionEvent.ACTION_OUTSIDE) {

            if (!disposable!!.isDisposed) {
                disposable!!.dispose()
            }
        }
        return true
    }

    private fun updateTick() {
        tick++
        tickListener.onTick(tick)
    }

    interface OnTickListener {
        fun onTick(tick: Int)
    }

    interface OnReleaseListener {
        fun onRelease(tick: Int)
    }

    companion object {

        private val DEFAULT_TICK_TIME = 200
    }
}
