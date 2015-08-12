package treehou.se.habit.core;

import android.util.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import treehou.se.habit.connector.ConnectorUtil;
import treehou.se.habit.core.db.ItemDB;

public class Widget {

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

    private String widgetId;
    private String type;
    private String icon;
    private String label;
    private String period;
    private String url;
    private ItemDB item;
    private List<Widget> widget;
    private List<Mapping> mapping;
    private LinkedPage linkedPage;

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

    public String getIconPath() {
        String icon = getIcon();
        return (icon != null) ? "/images/"+icon+".png" : null;
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

    public ItemDB getItem() {
        return item;
    }

    public void setItem(ItemDB item) {
        this.item = item;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<Widget> getWidget() {
        if(widget == null) return new ArrayList<>();
        return widget;
    }

    public String getUrl() {
        return url;
    }

    public String getBaseUrl(){
        try {
            return new URL(ConnectorUtil.getBaseUrl(item.getLink())).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setWidget(List<Widget> widget) {
        this.widget = widget;
    }

    public LinkedPage getLinkedPage() {
        return linkedPage;
    }

    public void setLinkedPage(LinkedPage linkedPage) {
        this.linkedPage = linkedPage;
    }

    public List<Mapping> getMapping() {
        return mapping;
    }

    public void setMapping(List<Mapping> mapping) {
        this.mapping = mapping;
    }

    public static class Mapping {

        private String command;
        private String label;

        public Mapping(String command, String label) {
            this.command = command;
            this.label = label;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public boolean needUpdate(Widget widget){

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
