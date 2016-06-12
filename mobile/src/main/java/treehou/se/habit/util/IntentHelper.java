package treehou.se.habit.util;

import android.content.Intent;
import android.net.Uri;

public class IntentHelper {

    private IntentHelper() {
    }

    public static Intent helpTranslateIntent(){
        String url = "https://oswmdvr.oneskyapp.com/collaboration/project?id=71199";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return intent;
    }
}
