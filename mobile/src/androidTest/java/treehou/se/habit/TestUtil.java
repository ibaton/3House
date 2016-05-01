package treehou.se.habit;

import android.app.Application;
import android.support.annotation.NonNull;

public class TestUtil {

    public static DaggerActivityTestRule<MainActivity> TestRule(){
        return new DaggerActivityTestRule<>(MainActivity.class, new DaggerActivityTestRule.OnBeforeActivityLaunchedListener<MainActivity>() {
            @Override
            public void beforeActivityLaunched(@NonNull Application application, @NonNull MainActivity activity) {
                DatabaseUtil.init(application);
            }
        });
    }
}
