package treehou.se.habit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.core.LinkedPage;
import treehou.se.habit.core.Widget;
import treehou.se.habit.core.db.ItemDB;
import treehou.se.habit.core.db.ServerDB;
import treehou.se.habit.core.db.StateDescription;
import treehou.se.habit.ui.settings.SetupServerFragment;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.ChartWidgetFactory;
import treehou.se.habit.ui.widgets.factories.ColorpickerWidgetFactory;
import treehou.se.habit.ui.widgets.factories.FrameWidgetFactory;
import treehou.se.habit.ui.widgets.factories.GroupWidgetFactory;
import treehou.se.habit.ui.widgets.factories.ImageWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SelectionWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SetpointWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SliderWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SwitchWidgetFactory;
import treehou.se.habit.ui.widgets.factories.TextWidgetFactory;
import treehou.se.habit.ui.widgets.factories.VideoWidgetFactory;
import treehou.se.habit.ui.widgets.factories.WebWidgetFactory;

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

    /**
     * Check if widget chart factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_chart_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_CHART);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof ChartWidgetFactory.ChartWidgetHolder)) {
            throw new AssertionError();
        }
    }

    /**
     * Check if widget colorpicker factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_colorpicker_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_COLORPICKER);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof ColorpickerWidgetFactory.ColorWidgetHolder)) {
            throw new AssertionError();
        }
    }


    /**
     * Check if widget frame factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_frame_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_FRAME);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof FrameWidgetFactory.FrameWidget)) {
            throw new AssertionError();
        }
    }

    /**
     * Check if widget group factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_group_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_GROUP);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && widgetHolder == null) {
            throw new AssertionError();
        }
    }

    /**
     * Check if widget image factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_image_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_IMAGE);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof ImageWidgetFactory.ImageWidgetHolder)) {
            throw new AssertionError();
        }
    }


    /**
     * Check if widget selection factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_selection_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_SELECTION);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof SelectionWidgetFactory.SelectWidgetHolder)) {
            throw new AssertionError();
        }
    }

    /**
     * Check if widget setpoint factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_setpoint_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_SETPOINT);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof SetpointWidgetFactory.SetpointWidgetHolder)) {
            throw new AssertionError();
        }
    }


    /**
     * Check if widget slider factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_slider_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_SLIDER);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof SliderWidgetFactory.SliderWidgetHolder)) {
            throw new AssertionError();
        }
    }


    /**
     * Check if widget switch factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_switch_creation() throws Exception {

        // Test rollershutter
        Widget widget = new Widget();
        widget.setType(Widget.TYPE_SWITCH);

        ItemDB item = new ItemDB();
        item.setType(ItemDB.TYPE_ROLLERSHUTTER);
        widget.setItem(item);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof SwitchWidgetFactory.RollerShutterWidgetHolder)) {
            throw new AssertionError("Expected type - RollerShutterWidgetHolder" + widgetHolder.getClass().getSimpleName());
        }


        // Test Switch
        widget = new Widget();
        widget.setType(Widget.TYPE_SWITCH);

        item = new ItemDB();
        item.setType(ItemDB.TYPE_SWITCH);
        widget.setItem(item);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof SwitchWidgetFactory.SwitchWidgetHolder)) {
            throw new AssertionError("Expected type - SwitchWidgetHolder " + widgetHolder.getClass().getSimpleName());
        }


        // Test Button switch
        widget = new Widget();
        List<Widget.Mapping> mappings = new ArrayList<>();
        mappings.add(new Widget.Mapping("command", "label"));
        widget.setMapping(mappings);
        widget.setType(Widget.TYPE_SWITCH);

        item = new ItemDB();
        widget.setItem(item);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof SwitchWidgetFactory.SingleButtonWidgetHolder)) {
            throw new AssertionError("Expected type - SingleButtonWidgetHolder " + widgetHolder.getClass().getSimpleName());
        }


        // Test Picker switch
        widget = new Widget();
        mappings = new ArrayList<>();
        mappings.add(new Widget.Mapping("command1", "label1"));
        mappings.add(new Widget.Mapping("command2", "label2"));
        widget.setMapping(mappings);
        widget.setType(Widget.TYPE_SWITCH);

        item = new ItemDB();
        widget.setItem(item);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof SwitchWidgetFactory.PickerWidgetHolder)) {
            throw new AssertionError("Expected type - PickerWidgetHolder " + widgetHolder.getClass().getSimpleName());
        }
    }


    /**
     * Check if widget text factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_text_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_TEXT);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof TextWidgetFactory.TextWidgetHolder)) {
            throw new AssertionError();
        }
    }


    /**
     * Check if widget video factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_video_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_VIDEO);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof VideoWidgetFactory.VideoWidgetHolder)) {
            throw new AssertionError();
        }
    }


    /**
     * Check if widget web factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_web_creation() throws Exception {

        Widget widget = new Widget();
        widget.setType(Widget.TYPE_WEB);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof WebWidgetFactory.WebWidgetHolder)) {
            throw new AssertionError();
        }
    }
}
