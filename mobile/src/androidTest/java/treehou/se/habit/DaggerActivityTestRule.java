package treehou.se.habit;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import treehou.se.habit.module.ApplicationComponent;

/**
 * {@link ActivityTestRule} which provides hook for
 * {@link ActivityTestRule#beforeActivityLaunched()} method. Can be used for test dependency
 * injection especially in Espresso based tests.
 *
 * @author Tomasz Rozbicki
 */
public abstract class DaggerActivityTestRule<T extends Activity> extends ActivityTestRule<T> {

    public DaggerActivityTestRule(Class<T> activityClass) {
        this(activityClass, false);
    }

    public DaggerActivityTestRule(Class<T> activityClass, boolean initialTouchMode) {
        this(activityClass, initialTouchMode, true);
    }

    public DaggerActivityTestRule(Class<T> activityClass, boolean initialTouchMode,
                                  boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();

        HabitApplication application = (HabitApplication)InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();

        DatabaseUtil.init(application);

        ApplicationComponent component = setupComponent(application, getActivity());
        application.setComponent(component);
    }



    public abstract ApplicationComponent setupComponent(HabitApplication application, Activity activity);
}