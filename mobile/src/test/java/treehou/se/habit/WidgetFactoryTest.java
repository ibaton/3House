package treehou.se.habit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.db.ItemDB;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.core.db.StateDescription;
import treehou.se.habit.ui.settings.SetupServerFragment;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.ColorpickerWidgetFactory;
import treehou.se.habit.ui.widgets.factories.FrameWidgetFactory;
import treehou.se.habit.ui.widgets.factories.TextWidgetFactory;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "treehou.se.habit", sdk = 21)
public class WidgetFactoryTest {

    WidgetFactory.IWidgetHolder widgetHolder;
    WidgetFactory factory;

    @Before
    public void setUp() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        ServerDB server = new ServerDB();
        server.setName("Home");

        LinkedPage page = new LinkedPage();
        page.setId("");
        page.setLink("");
        page.setTitle("");
        page.setWidgets(new ArrayList<Widget>());

        factory = new WidgetFactory(activity, server, page);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void check_widget_label_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_TEXT);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof TextWidgetFactory.TextWidgetHolder)) {
            throw new AssertionError();
        }
    }

    @Test
    public void check_widget_frame_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_FRAME);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof FrameWidgetFactory.FrameWidget)) {
            throw new AssertionError();
        }
    }

    @Test
    public void check_widget_colorpicker_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_COLORPICKER);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof ColorpickerWidgetFactory.ColorWidgetHolder)) {
            throw new AssertionError();
        }
    }
}
