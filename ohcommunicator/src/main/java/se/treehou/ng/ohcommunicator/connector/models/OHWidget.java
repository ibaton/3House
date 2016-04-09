package se.treehou.ng.ohcommunicator.connector.models;

import java.util.List;

public class OHWidget {

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
    private OHItem item;
    private List<OHWidget> widget;
    private List<OHMapping> mapping;

    private OHLinkedPage linkedPage;

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public String getBaseUrl(){
        return url;
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

    public OHItem getItem() {
        return item;
    }

    public void setItem(OHItem item) {
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

    public List<OHWidget> getWidget() {
        return widget;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setWidget(List<OHWidget> widget) {
        this.widget = widget;
    }

    public OHLinkedPage getLinkedPage() {
        return linkedPage;
    }

    public void setLinkedPage(OHLinkedPage linkedPage) {
        this.linkedPage = linkedPage;
    }

    public List<OHMapping> getMapping() {
        return mapping;
    }

    public void setMapping(List<OHMapping> mapping) {
        this.mapping = mapping;
    }

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

    public String getIconPath(){
        String icon = getIcon();
        return (icon != null) ? "/images/"+icon+".png" : null;
    }

    public boolean needUpdate(OHWidget widget){
        return true;
    }
}
