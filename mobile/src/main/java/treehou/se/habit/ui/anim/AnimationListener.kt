package treehou.se.habit.ui.anim

interface AnimationFinishListener {
    /**
     * Returns true if animation run to an end. Else false
     */
    fun animationFinished(properFinish: Boolean)
}