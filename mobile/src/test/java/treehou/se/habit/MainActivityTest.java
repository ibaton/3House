package treehou.se.habit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import treehou.se.habit.ui.settings.SetupServerFragment;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "treehou.se.habit", sdk = 21)
public class MainActivityTest {

    @Test
    public void instant_server_setup_isCorrect() throws Exception {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();

        if(BuildConfig.DEBUG && !(activity.getSupportFragmentManager().findFragmentById(R.id.page_container) instanceof SetupServerFragment)) {
            throw new AssertionError();
        }
    }
}
