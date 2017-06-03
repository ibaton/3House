package treehou.se.habit.ui.control;


import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import treehou.se.habit.core.db.model.controller.ControllerDB;

public class ControllerHandler {

    private Realm realm;
    private ControllerUtil controllerUtil;

    private OrderedRealmCollectionChangeListener<RealmResults<ControllerDB>>
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

    private void handleControllerUpdates(RealmResults<ControllerDB> controllerDBS, OrderedCollectionChangeSet orderedCollectionChangeSet){
        if(orderedCollectionChangeSet == null || controllerDBS.size() == 0) return;

        List<ControllerDB> deletions = getControllers(controllerDBS, orderedCollectionChangeSet.getDeletions());
        List<ControllerDB> changes = getControllers(controllerDBS, orderedCollectionChangeSet.getChanges());
        List<ControllerDB> insertions = getControllers(controllerDBS, orderedCollectionChangeSet.getInsertions());

        deleteNotifications(deletions);
        updateNotifications(changes);
        updateNotifications(insertions);
    }

    private List<ControllerDB> getControllers(RealmResults<ControllerDB> controllerDBs, int[] locations){
        List<ControllerDB> controllerList = new ArrayList<>();
        for(int insertionIndex : locations){
            controllerList.add(controllerDBs.get(insertionIndex));
        }
        return controllerList;
    }

    private void deleteNotifications(List<ControllerDB> controllerDBs){
        for(ControllerDB controllerDB : controllerDBs){
            controllerUtil.hideNotification(controllerDB);
        }
    }

    private void updateNotifications(List<ControllerDB> controllerDBs){
        for(ControllerDB controllerDB : controllerDBs){
            if(controllerDB.isShowNotification()) {
                controllerUtil.showNotification(controllerDB);
            } else {
                controllerUtil.hideNotification(controllerDB);
            }
        }
    }
}
