package treehou.se.habit.module


import android.support.v4.app.Fragment

import dagger.MembersInjector

interface FragmentComponent<A : Fragment> : MembersInjector<A>
