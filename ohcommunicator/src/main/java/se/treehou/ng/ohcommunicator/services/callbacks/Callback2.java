package se.treehou.ng.ohcommunicator.services.callbacks;

public interface Callback2<G,H> extends Callback {
    void onUpdate(G items1, H items2);
}
