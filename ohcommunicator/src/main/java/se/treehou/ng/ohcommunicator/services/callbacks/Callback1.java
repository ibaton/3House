package se.treehou.ng.ohcommunicator.services.callbacks;

public interface Callback1<G> extends Callback {
    void onUpdate(G items);
}
