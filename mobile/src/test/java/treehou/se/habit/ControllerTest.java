package treehou.se.habit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "treehou.se.habit", sdk = 21)
public class ControllerTest {

    @Test
    public void create_new_controller() throws Exception {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        activity.onNavigationDrawerItemSelected(NavigationDrawerFragment.ITEM_CONTROLLERS);
    }
}
