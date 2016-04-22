package treehou.se.habit.ui.colorpicker;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.model.ServerDB;

public class ColorpickerActivity extends AppCompatActivity {

    public static final String EXTRA_SERVER = "EXTRA_SERVER";
    public static final String EXTRA_WIDGET = "EXTRA_SITEMAP";
    public static final String EXTRA_COLOR  = "EXTRA_COLOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    public static class LightFragment extends Fragment {

        private static final String TAG = "LightFragment";

        private static final String ARG_SERVER  = "ARG_SERVER";
        private static final String ARG_WIDGET  = "ARG_SITEMAP";
        private static final String ARG_COLOR   = "ARG_COLOR";

        @Bind(R.id.lbl_name) TextView lblName;

        private Realm realm;

        private OHServer server;
        private OHWidget widget;
        private int color;
        private ColorPicker pcrColor;

        private Timer timer = new Timer();

        public static LightFragment newInstance(long serverId, OHWidget widget, int color){
            LightFragment fragment = new LightFragment();

            Bundle args = new Bundle();
            Gson gson = GsonHelper.createGsonBuilder();
            args.putLong(ARG_SERVER, serverId);
            args.putString(ARG_WIDGET ,gson.toJson(widget));
            args.putInt(ARG_COLOR ,color);
            fragment.setArguments(args);

            return fragment;
        }

        public LightFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            realm = Realm.getDefaultInstance();

            Bundle args = getArguments();
            long serverId = args.getLong(ARG_SERVER);
            String jWidget = args.getString(ARG_WIDGET);
            color = args.getInt(ARG_COLOR);

            Gson gson = GsonHelper.createGsonBuilder();
            server = ServerDB.load(realm, serverId).toGeneric();
            widget = gson.fromJson(jWidget, OHWidget.class);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_colorpicker, container, false);
            ButterKnife.bind(this, rootView);

            lblName.setText(widget.getLabel());

            pcrColor = (ColorPicker) rootView.findViewById(R.id.pcr_color_h);
            pcrColor.setColor(color);

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            pcrColor.setOnColorChangeListener(colorChangeListener);
        }

        @Override
        public void onPause() {
            super.onPause();
            pcrColor.setOnColorChangeListener(null);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            realm.close();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.unbind(this);
        }

        treehou.se.habit.ui.colorpicker.ColorPicker.ColorChangeListener colorChangeListener = new treehou.se.habit.ui.colorpicker.ColorPicker.ColorChangeListener() {
            @Override
            public void onColorChange(final float[] hsv) {
                timer.cancel();
                timer.purge();
                timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        hsv[1] *= 100;
                        hsv[2] *= 100;
                        Log.d(TAG, "Color changed to " + String.format("%d,%d,%d", (int) hsv[0], (int) (hsv[1]), (int) (hsv[2])));
                        if(hsv[2] > 5) {
                            Openhab.instance(server).sendCommand(widget.getItem().getName(), String.format(Constants.COMMAND_COLOR, (int) hsv[0], (int) (hsv[1]), (int) (hsv[2])));
                        }else {
                            Openhab.instance(server).sendCommand(widget.getItem().getName(), Constants.COMMAND_OFF);
                        }
                    }
                },300);
            }
        };
    }
}
