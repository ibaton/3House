package treehou.se.habit.ui.colorpicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import javax.inject.Inject;

import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.util.Settings;

public class ColorpickerActivity extends AppCompatActivity {

    public static final String EXTRA_SERVER = "EXTRA_SERVER";
    public static final String EXTRA_WIDGET = "EXTRA_SITEMAP";
    public static final String EXTRA_COLOR  = "EXTRA_COLOR";

    @Inject Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((HabitApplication) getApplication()).component().inject(this);
        setTheme(settings.getThemeResourse());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_colorpicker);

        Bundle bundle = getIntent().getExtras();
        long serverId = bundle.getLong(EXTRA_SERVER);
        String jWidget = bundle.getString(EXTRA_WIDGET);
        int color = bundle.getInt(EXTRA_COLOR);

        Gson gson = GsonHelper.createGsonBuilder();
        OHWidget widget = gson.fromJson(jWidget, OHWidget.class);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, LightFragment.newInstance(serverId, widget, color))
                    .commit();
        }
    }
}
