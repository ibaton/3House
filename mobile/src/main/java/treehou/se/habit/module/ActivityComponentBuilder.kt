package treehou.se.habit.module

interface ActivityComponentBuilder<M : ViewModule<*>, C : ActivityComponent<*>> {
    fun activityModule(activityModule: M): ActivityComponentBuilder<M, C>
    fun build(): C
}