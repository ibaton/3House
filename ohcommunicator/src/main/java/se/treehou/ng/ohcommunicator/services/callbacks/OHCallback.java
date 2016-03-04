package se.treehou.ng.ohcommunicator.services.callbacks;

public interface OHCallback<G> {
    void onUpdate(OHResponse<G> items);
    void onError();
}
