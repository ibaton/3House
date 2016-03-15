package treehou.se.habit.core.db.model;

import io.realm.annotations.Ignore;

public class OHWidget /*extends RealmObject*/ {

    public static final String TAG                  = "Widget";

    // TODO convert to enum
    public static final String TYPE_DUMMY           = "Dummy";
    public static final String TYPE_FRAME           = "Frame";
    public static final String TYPE_SWITCH          = "Switch";
    public static final String TYPE_COLORPICKER     = "Colorpicker";
    public static final String TYPE_SELECTION       = "Selection";
    public static final String TYPE_CHART           = "Chart";
    public static final String TYPE_IMAGE           = "Image";
    public static final String TYPE_VIDEO           = "Video";
    public static final String TYPE_WEB             = "Webview";
    public static final String TYPE_TEXT            = "Text";
    public static final String TYPE_SLIDER          = "Slider";
    public static final String TYPE_GROUP           = "Group";
    public static final String TYPE_SETPOINT        = "Setpoint";

    private String widgetId;
    private String type;
    private String icon;
    private String label;

    // Used for charts
    private String period;
    private String service;

    private int minValue=0;
    private int maxValue=100;
    private float step=1;

    private String url;
    private OHItemDB item;
    /*private RealmList<OHWidget> widget;*/
    //private RealmList<OHMapping> mapping;

    @Ignore
    private OHLinkedPage linkedPage;

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return (icon == null || icon.equals("none") || icon.equals("image") || icon.equals("")) ? null : icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label != null ? label : "";
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public OHItemDB getItem() {
        return item;
    }

    public void setItem(OHItemDB item) {
        this.item = item;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    /*public RealmList<OHWidget> getWidget() {
        return widget;
    }*/

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /*public void setWidget(RealmList<OHWidget> widget) {
        this.widget = widget;
    }*/

    public OHLinkedPage getLinkedPage() {
        return linkedPage;
    }

    public void setLinkedPage(OHLinkedPage linkedPage) {
        this.linkedPage = linkedPage;
    }

    /*public RealmList<OHMapping> getMapping() {
        return mapping;
    }

    public void setMapping(RealmList<OHMapping> mapping) {
        this.mapping = mapping;
    }*/

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }
}
