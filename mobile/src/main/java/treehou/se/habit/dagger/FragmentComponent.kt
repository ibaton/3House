package treehou.se.habit.dagger


import android.support.v4.app.Fragment

import dagger.MembersInjector

interface FragmentComponent<A : Fragment> : MembersInjector<A>
