package treehou.se.habit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import se.treehou.ng.ohcommunicator.core.OHItemWrapper;
import se.treehou.ng.ohcommunicator.core.OHLinkedPageWrapper;
import se.treehou.ng.ohcommunicator.core.OHServerWrapper;
import se.treehou.ng.ohcommunicator.core.OHWidgetWrapper;
import treehou.se.habit.ui.widgets.WidgetFactory;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "treehou.se.habit", sdk = 21)
public class WidgetTest {

    WidgetFactory.IWidgetHolder widgetHolder;
    WidgetFactory factory;

    @Before
    public void setUp() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        OHServerWrapper server = new OHServerWrapper();
        server.setName("Home");

        OHLinkedPageWrapper page = new OHLinkedPageWrapper();
        page.setId("");
        page.setLink("");
        page.setTitle("");
        page.setWidgets(new ArrayList<OHWidgetWrapper>());

        factory = new WidgetFactory(activity, server, page);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void check_name_is_correct() throws Exception {

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_SWITCH);

        OHItemWrapper item = new OHItemWrapper();
        widget.setItem(item);

        widget.setLabel("Widget Name");

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG) {
            if (!"Widget Name".equals(widget.getLabel())) {
                throw new AssertionError("Wrong widget name - " + widget.getLabel());
            }
        }
    }
}
