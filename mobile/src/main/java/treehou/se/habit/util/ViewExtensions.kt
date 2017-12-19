package treehou.se.habit.util

import android.view.View
import android.view.animation.Animation
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

fun MutableList<Int>.swap(index1: Int, index2: Int) {
    val tmp = this[index1] // 'this' corresponds to the list
    this[index1] = this[index2]
    this[index2] = tmp
}

fun View.startAnimationFlowable(animation : Animation) : Flowable<Boolean> {
    return Flowable.create<Boolean>({ s ->
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(p0: Animation?) {
                s.onNext(false)
                s.onComplete()
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationStart(p0: Animation?) {
                s.onNext(true)
                s.onComplete()
            }
        })
        this.startAnimation(animation)
    }, BackpressureStrategy.DROP)
}