package treehou.se.habit;

import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import se.treehou.ng.ohcommunicator.core.OHItemWrapper;
import se.treehou.ng.ohcommunicator.core.OHLinkedPageWrapper;
import se.treehou.ng.ohcommunicator.core.OHServerWrapper;
import se.treehou.ng.ohcommunicator.core.OHWidgetWrapper;
import se.treehou.ng.ohcommunicator.core.db.OHMapping;
import se.treehou.ng.ohcommunicator.core.db.OHItemDB;
import se.treehou.ng.ohcommunicator.core.db.OHserver;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.ChartWidgetFactory;
import treehou.se.habit.ui.widgets.factories.ColorpickerWidgetFactory;
import treehou.se.habit.ui.widgets.factories.FrameWidgetFactory;
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
        OHserver server = new OHserver();
        server.setName("Home");

        OHLinkedPageWrapper page = new OHLinkedPageWrapper();
        page.setId("");
        page.setLink("");
        page.setTitle("");
        page.setWidgets(new ArrayList<OHWidgetWrapper>());

        factory = new WidgetFactory(activity, new OHServerWrapper(server), page);
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

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_CHART);

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

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_COLORPICKER);

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

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_FRAME);

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

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_GROUP);

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

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_IMAGE);

        widget.setUrl("https://www.google.se/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");

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

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_SELECTION);

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

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_SETPOINT);

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

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_SLIDER);

        OHItemWrapper item = new OHItemWrapper();
        item.setType(OHItemDB.TYPE_DIMMER);
        item.setState("43.0");

        widget.setItem(item);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof SliderWidgetFactory.SliderWidgetHolder)) {
            throw new AssertionError();
        }
        SliderWidgetFactory.SliderWidgetHolder sliderWidgetHolder = (SliderWidgetFactory.SliderWidgetHolder) widgetHolder;

        widgetHolder = factory.createWidget(widget, null);

        sliderWidgetHolder.getSeekbarView();
        if(BuildConfig.DEBUG && sliderWidgetHolder.getSeekbarView().getProgress() != 43) {
            throw new AssertionError("Slider showing value " + sliderWidgetHolder.getSeekbarView().getProgress() + " should be 43");
        }

        item.setState("60.1");
        sliderWidgetHolder.update(widget);
        if(BuildConfig.DEBUG && sliderWidgetHolder.getSeekbarView().getProgress() != 60) {
            throw new AssertionError("Slider update showing value " + sliderWidgetHolder.getSeekbarView().getProgress() + " should be 60");
        }

        item.setState("65");
        sliderWidgetHolder.update(widget);
        if(BuildConfig.DEBUG && sliderWidgetHolder.getSeekbarView().getProgress() != 65) {
            throw new AssertionError("Slider update with int value " + sliderWidgetHolder.getSeekbarView().getProgress() + " should be 65");
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
        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_SWITCH);

        OHItemWrapper item = new OHItemWrapper();
        item.setType(OHItemWrapper.TYPE_ROLLERSHUTTER);
        widget.setItem(item);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof SwitchWidgetFactory.RollerShutterWidgetHolder)) {
            throw new AssertionError("Expected type - RollerShutterWidgetHolder" + widgetHolder.getClass().getSimpleName());
        }


        // Test Switch
        widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_SWITCH);

        item = new OHItemWrapper();
        item.setType(OHItemDB.TYPE_SWITCH);
        widget.setItem(item);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof SwitchWidgetFactory.SwitchWidgetHolder)) {
            throw new AssertionError("Expected type - SwitchWidgetHolder " + widgetHolder.getClass().getSimpleName());
        }


        // Test Button switch
        widget = new OHWidgetWrapper();
        List<OHMapping> mappings = new ArrayList<>();
        OHMapping mappingDB = new OHMapping();
        mappingDB.setCommand("command");
        mappingDB.setLabel("label");
        mappings.add(mappingDB);
        widget.setMapping(mappings);
        widget.setType(OHWidgetWrapper.TYPE_SWITCH);

        item = new OHItemWrapper();
        widget.setItem(item);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof SwitchWidgetFactory.SingleButtonWidgetHolder)) {
            throw new AssertionError("Expected type - SingleButtonWidgetHolder " + widgetHolder.getClass().getSimpleName());
        }


        // Test Picker switch
        widget = new OHWidgetWrapper();
        mappings = new ArrayList<>();
        OHMapping mappingDB1 = new OHMapping();
        mappingDB1.setCommand("command1");
        mappingDB1.setLabel("label1");
        OHMapping mappingDB2 = new OHMapping();
        mappingDB2.setCommand("command2");
        mappingDB2.setLabel("label2");
        mappings.add(mappingDB);
        mappings.add(mappingDB1);
        mappings.add(mappingDB2);
        widget.setMapping(mappings);
        widget.setType(OHWidgetWrapper.TYPE_SWITCH);

        item = new OHItemWrapper();
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

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_TEXT);
        widget.setLabel("WidgetText");

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG) {

            if(!(widgetHolder instanceof TextWidgetFactory.TextWidgetHolder)) {
                throw new AssertionError();
            }

            if(((TextView) widgetHolder.getView().findViewById(R.id.lbl_widget_name)).getText().equals("WidgetText") ) {
                throw new AssertionError();
            }
        }
    }


    /**
     * Check if widget video factory creation works.
     *
     * @throws Exception
     */
    @Test
    public void check_widget_video_creation() throws Exception {

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_VIDEO);

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

        OHWidgetWrapper widget = new OHWidgetWrapper();
        widget.setType(OHWidgetWrapper.TYPE_WEB);

        widgetHolder = factory.createWidget(widget, null);

        if(BuildConfig.DEBUG && !(widgetHolder instanceof WebWidgetFactory.WebWidgetHolder)) {
            throw new AssertionError();
        }
    }
}
