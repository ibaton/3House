package treehou.se.habit.ui.util;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.util.Settings;

public class IconPickerActivity extends AppCompatActivity {

    public static final String RESULT_ICON = IconPickerFragment.RESULT_ICON;

    @Inject Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((HabitApplication) getApplication()).component().inject(this);
        setTheme(settings.getThemeResourse());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_picker);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.findFragmentById(R.id.content) == null){
            fragmentManager.beginTransaction()
                    .add(R.id.content, CategoryPickerFragment.newInstance())
                    .commit();
        }
    }
}
