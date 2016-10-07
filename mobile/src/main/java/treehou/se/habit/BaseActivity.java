package treehou.se.habit;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import treehou.se.habit.util.Settings;
import treehou.se.habit.util.logging.Logger;

public class BaseActivity extends RxAppCompatActivity {


    private static final String TAG = BaseActivity.class.getSimpleName();

    protected @Inject Settings settings;
    protected @Inject Logger logger;
    protected Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((HabitApplication) getApplication()).component().inject(this);
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupFullscreenHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    /**
     * Set up full screen handler.
     * Will automatically switch to fullscreen when set in settings
     */
    public void setupFullscreenHandler(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.getFullscreenPref().asObservable()
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(fullscreen -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            showFullscreen(fullscreen);
                        }
                    }, throwable -> {
                        logger.e(TAG, "Failed to set fullscreen mode", throwable);
                    });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        showFullscreen(settings.getFullscreenPref().get());
    }

    /**
     * Set if view should be in fullscreen.
     * Requires {@link Build.VERSION_CODES#KITKAT}
     *
     * @param fullscreen true to set into fullscreen, else false.
     */
    private void showFullscreen(boolean fullscreen){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;

        if(fullscreen){
            showFullscreen();
        } else {
            showNormal();
        }
    }

    /**
     * Hides system ui using immersive layout.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * Put system ui into normal mode.
     * This is done by default
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showNormal() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}
