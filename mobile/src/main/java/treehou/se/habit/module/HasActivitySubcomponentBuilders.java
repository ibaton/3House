package treehou.se.habit.module;

import android.app.Activity;
import android.support.v4.app.Fragment;

public interface HasActivitySubcomponentBuilders {
    ActivityComponentBuilder getActivityComponentBuilder(Class<? extends Activity> activityClass);
    FragmentComponentBuilder getFragmentComponentBuilder(Class<? extends Fragment> fragmentClass);
}