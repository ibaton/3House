package treehou.se.habit.mvp;

import android.os.Bundle;

public interface BasePresenter {
    void load(Bundle savedData);
    void subscribe();
    void unsubscribe();
    void unload();
    void save(Bundle savedData);
}
