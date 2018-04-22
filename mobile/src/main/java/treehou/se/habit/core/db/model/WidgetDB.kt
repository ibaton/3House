package treehou.se.habit.core.db.model

import io.realm.annotations.Ignore

class WidgetDB /*extends RealmObject*/ {

    var widgetId: String? = null
    var type: String? = null
    var icon: String? = null
        get() = if (field == null || field == "none" || field == "image" || field == "") null else field
    var label: String? = null
        get() = if (field != null) field else ""

    // Used for charts
    var period: String? = null
    var service: String? = null

    var minValue = 0
    var maxValue = 100
    /*public RealmList<MappingDB> getMapping() {
        return mapping;
    }

    public void setMapping(RealmList<MappingDB> mapping) {
        this.mapping = mapping;
    }*/

    var step = 1f

    /*public RealmList<WidgetDB> getWidget() {
        return widget;
    }*/

    var url: String? = null
    var item: ItemDB? = null
    /*private RealmList<WidgetDB> widget;*/
    //private RealmList<MappingDB> mapping;

    /*public void setWidget(RealmList<WidgetDB> widget) {
        this.widget = widget;
    }*/

    @Ignore
    var linkedPage: LinkedPageDB? = null

    companion object {

        val TAG = "WidgetFactory"

        // TODO convert to enum
        val TYPE_DUMMY = "Dummy"
        val TYPE_FRAME = "Frame"
        val TYPE_SWITCH = "Switch"
        val TYPE_COLORPICKER = "Colorpicker"
        val TYPE_SELECTION = "Selection"
        val TYPE_CHART = "Chart"
        val TYPE_IMAGE = "Image"
        val TYPE_VIDEO = "Video"
        val TYPE_WEB = "Webview"
        val TYPE_TEXT = "Text"
        val TYPE_SLIDER = "Slider"
        val TYPE_GROUP = "Group"
        val TYPE_SETPOINT = "Setpoint"
    }
}
