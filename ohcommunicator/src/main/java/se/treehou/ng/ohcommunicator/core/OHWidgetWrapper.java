package se.treehou.ng.ohcommunicator.core;

import android.util.Log;

import java.net.URL;
import java.util.List;

import io.realm.RealmList;
import se.treehou.ng.ohcommunicator.connector.ConnectorUtil;
import se.treehou.ng.ohcommunicator.core.db.OHMapping;
import se.treehou.ng.ohcommunicator.core.db.OHLinkedPage;
import se.treehou.ng.ohcommunicator.core.db.OHWidget;

public class OHWidgetWrapper {

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

    private OHWidget widgetDB;

    public OHWidgetWrapper() {
    }

    public OHWidgetWrapper(OHWidget widgetDB) {
        this.widgetDB = widgetDB;
    }

    public OHWidget getDB() {
        return widgetDB;
    }

    public void setDB(OHWidget widgetDB) {
        this.widgetDB = widgetDB;
    }

    public String getWidgetId() {
        return getDB().getWidgetId();
    }

    public void setWidgetId(String widgetId) {
        getDB().setWidgetId(widgetId);
    }

    public String getType() {
        return getDB().getType();
    }

    public void setType(String type) {
        getDB().setType(type);
    }

    public String getIcon() {
        String icon = getDB().getIcon();
        return (icon == null || icon.equals("none") || icon.equals("image") || icon.equals("")) ? null : icon;
    }

    public String getIconPath() {
        String icon = getIcon();
        return (icon != null) ? "/images/"+icon+".png" : null;
    }

    public void setIcon(String icon) {
        getDB().setIcon(icon);
    }

    public String getLabel() {
        return getDB().getLabel() != null ? getDB().getLabel() : "";
    }

    public void setLabel(String label) {
        getDB().setLabel(label);
    }

    public OHItemWrapper getItem() {
        return new OHItemWrapper(getDB().getItem());
    }

    public void setItem(OHItemWrapper item) {
        getDB().setItem(item.getDB());
    }

    public String getPeriod() {
        return getDB().getPeriod();
    }

    public void setPeriod(String period) {
        getDB().getPeriod();
    }

    public String getService() {
        return getDB().getService();
    }

    public void setService(String service) {
        getDB().setService(service);
    }

    public List<OHWidgetWrapper> getWidget() {
        /*if(getDB().getWidget() == null) return new ArrayList<>();

        List<OHWidgetWrapper> widgets = new ArrayList<>();
        for(OHWidget widgetDB : getDB().getWidget()){
            widgets.add(new OHWidgetWrapper(widgetDB));
        }

        return widgets;*/
        return null;
    }

    public String getUrl() {
        return getDB().getUrl();
    }

    public String getBaseUrl(){
        try {
            return new URL(ConnectorUtil.getBaseUrl(getDB().getItem().getLink())).toString();
        } catch (Exception e) {
            Log.e(TAG, "Failed to generate base url", e);
        }
        return "";
    }

    public void setUrl(String url) {
        getDB().setUrl(url);
    }

    public void setWidget(List<OHWidgetWrapper> widgets) {
        /*RealmList<OHWidget> widgetDBs = new RealmList<>();
        for(OHWidgetWrapper widget : widgets){
            widgetDBs.add(widget.getDB());
        }
        getDB().setWidget(widgetDBs);*/
    }

    public OHLinkedPage getLinkedPage() {
        return getDB().getLinkedPage();
    }

    public void setLinkedPage(OHLinkedPage linkedPage) {
        getDB().setLinkedPage(linkedPage);
    }

    public List<OHMapping> getMapping() {
        //return getDB().getMapping();
        return null;
    }

    public void setMapping(List<OHMapping> mapping) {

        /*RealmList<OHMapping> mappingsDb = new RealmList<>();
        for(OHMapping mappingDb : mapping){
            mappingsDb.add(mappingDb);
        }

        getDB().setMapping(mappingsDb);*/
    }

    public float getStep() {
        return getDB().getStep();
    }

    public void setStep(float step) {
        getDB().setStep(step);
    }

    public int getMaxValue() {
        return getDB().getMaxValue();
    }

    public void setMaxValue(int maxValue) {
        getDB().setMaxValue(maxValue);
    }

    public int getMinValue() {
        return getDB().getMinValue();
    }

    public void setMinValue(int minValue) {
        getDB().setMinValue(minValue);
    }

    public boolean needUpdate(OHWidgetWrapper widget){

        if(getWidget().size() != widget.getWidget().size()){
            Log.d(TAG, "needUpdate1 " + getWidget().size() + " : " + widget.getWidget().size());
            return true;
        }
        for(int i=0; i < getWidget().size(); i++){
            if (getWidget().get(i).needUpdate(widget.getWidget().get(i))){
                Log.d(TAG, "needUpdate2 " + getWidget().size() + " : " + widget.getWidget().size());
                return true;
            }
        }

        boolean needUpdate = (!getType().equals(widget.getType()));

        if(needUpdate){
            Log.d(TAG, "needUpdate3 " + getType() + ":" + widget.getType() + ":" + getType().equals(widget.getType()) + " id " + getWidgetId() + ":" + widget.getWidgetId() + ":" + getWidgetId().equals(widget.getWidgetId()));
        }

        return needUpdate;
    }
}
