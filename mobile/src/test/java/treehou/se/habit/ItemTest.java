package treehou.se.habit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import se.treehou.ng.ohcommunicator.connector.models.OHItem;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "treehou.se.habit", sdk = 21)
public class ItemTest {

    @Test
    public void check_name_is_correct() throws Exception {

        OHItem item = new OHItem();
        item.setName("Device");

        if(BuildConfig.DEBUG && !item.getName().equals("Device")) {
            throw new AssertionError();
        }
    }

    @Test
    public void check_formated_state_is_correct() throws Exception {

        OHItem item = new OHItem();

        /*StateDescriptionDB stateDescription = new StateDescriptionDB();
        stateDescription.setPattern("%.2f");
        item.setStateDescription(stateDescription);

        item.setState("12.2");

        String printableState = ItemDB.getFormatedValue(item);
        if(BuildConfig.DEBUG && !printableState.equals("12,20")) {
            throw new AssertionError();
        }*/
    }
}
