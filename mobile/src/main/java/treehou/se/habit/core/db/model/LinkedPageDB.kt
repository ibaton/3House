package treehou.se.habit.core.db.model

import io.realm.RealmObject

open class LinkedPageDB : RealmObject() {

    //private RealmList<WidgetDB> widget;

    var id: String? = null
    var link: String? = null
    var title: String? = null
    var leaf: Boolean = false

    /*public RealmList<WidgetDB> getWidget() {
        //return widget;
        return null;
    }

    public void setWidget(RealmList<WidgetDB> widget) {
        //this.widget = widget;
    }*/
}
