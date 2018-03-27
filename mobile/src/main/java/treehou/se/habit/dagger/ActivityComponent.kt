package treehou.se.habit.dagger


import android.app.Activity

import dagger.MembersInjector

interface ActivityComponent<A : Activity> : MembersInjector<A>
