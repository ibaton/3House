package treehou.se.habit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import treehou.se.habit.core.db.ItemDB;
import treehou.se.habit.core.db.StateDescription;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "treehou.se.habit", sdk = 21)
public class ItemTest {

    @Test
    public void check_name_is_correct() throws Exception {

        ItemDB item = new ItemDB();
        item.setName("Device");

        if(BuildConfig.DEBUG && !item.getName().equals("Device")) {
            throw new AssertionError();
        }
    }

    @Test
    public void check_formated_name_is_correct() throws Exception {

        ItemDB item = new ItemDB();
        item.setName("Device [%.2f]");

        StateDescription stateDescription = new StateDescription();
        stateDescription.setPattern("%.2f");
        item.setStateDescription(stateDescription);

        item.setState("12.2");

        String printableName = item.printableName();
        if(BuildConfig.DEBUG && !item.printableName().equals("Device 12.20")) {
            throw new AssertionError();
        }
    }
}
