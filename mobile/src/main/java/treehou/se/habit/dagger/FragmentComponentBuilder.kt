package treehou.se.habit.dagger

interface FragmentComponentBuilder<M : ViewModule<*>, C : FragmentComponent<*>> {
    fun fragmentModule(fragmentModule: M): FragmentComponentBuilder<M, C>
    fun build(): C
}