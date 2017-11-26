package treehou.se.habit.ui.control;


import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import treehou.se.habit.core.db.model.controller.ControllerDB;

public class ControllerHandler {

    private Realm realm;
    private ControllerUtil controllerUtil;
    private Set<Long> notificationIds = new HashSet<>();

    private RealmChangeListener<RealmResults<ControllerDB>>
            controllerDBListener = this::handleControllerUpdates;

    @SuppressWarnings("FieldCanBeLocal") // Needed for listener
    private RealmResults<ControllerDB> controllers;

    public ControllerHandler(Realm realm, ControllerUtil controllerUtil) {
        this.realm = realm;
        this.realm = Realm.getDefaultInstance();
        this.controllerUtil = controllerUtil;
    }

    public void init(){
        controllers = realm.where(ControllerDB.class).findAll();
        updateNotifications(controllers);
        controllers.addChangeListener(controllerDBListener);
    }

    private void handleControllerUpdates(RealmResults<ControllerDB> controllerDBS){
        Set<Long> updateNotificationIds = new HashSet<>();
        Observable.fromIterable(controllerDBS).map(ControllerDB::getId).subscribe(updateNotificationIds::add);

        Set<Long> notificationsDeleted = new HashSet<>(notificationIds);
        notificationsDeleted.removeAll(updateNotificationIds);

        deleteNotifications(notificationsDeleted);
        updateNotifications(controllerDBS);

        notificationIds = updateNotificationIds;
    }

    private void deleteNotifications(Collection<Long> controllerIds){
        for(long controllerDbId : controllerIds){
            controllerUtil.hideNotification((int) controllerDbId);
        }
    }

    private void updateNotifications(List<ControllerDB> controllerDBs){
        for(ControllerDB controllerDB : controllerDBs){
            if(controllerDB.isShowNotification()) {
                controllerUtil.showNotification(controllerDB);
            } else {
                controllerUtil.hideNotification((int) controllerDB.getId());
            }
        }
    }
}
