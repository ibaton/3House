package treehou.se.habit.ui.colorpicker;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.R;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.model.ServerDB;

public class LightFragment extends Fragment {

    private static final String TAG = "LightFragment";

    private static final String ARG_SERVER = "ARG_SERVER";
    private static final String ARG_WIDGET = "ARG_SITEMAP";
    private static final String ARG_COLOR = "ARG_COLOR";

    @BindView(R.id.lbl_name) TextView lblName;
    @BindView(R.id.pcr_color_h) ColorPicker pcrColor;

    private Realm realm;

    private OHServer server;
    private OHWidget widget;
    private int color;

    private Timer timer = new Timer();
    private Unbinder unbinder;

    public static LightFragment newInstance(long serverId, OHWidget widget, int color) {
        LightFragment fragment = new LightFragment();

        Bundle args = new Bundle();
        Gson gson = GsonHelper.createGsonBuilder();
        args.putLong(ARG_SERVER, serverId);
        args.putString(ARG_WIDGET, gson.toJson(widget));
        args.putInt(ARG_COLOR, color);
        fragment.setArguments(args);

        return fragment;
    }

    public LightFragment() {
    }

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
        unbinder = ButterKnife.bind(this, rootView);

        lblName.setText(widget.getLabel());
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
    unbinder.unbind();
    }

    private ColorPicker.ColorChangeListener colorChangeListener = new ColorPicker.ColorChangeListener() {
        @Override
        public void onColorChange(final float[] hsv) {
            timer.cancel();
            timer.purge();
            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    IServerHandler serverHandler = new Connector.ServerHandler(server, getActivity());

                    hsv[1] *= 100;
                    hsv[2] *= 100;
                    Log.d(TAG, "Color changed to " + String.format("%d,%d,%d", (int) hsv[0], (int) (hsv[1]), (int) (hsv[2])));
                    if (hsv[2] > 5) {
                        serverHandler.sendCommand(widget.getItem().getName(), String.format(Locale.getDefault(), Constants.COMMAND_COLOR, (int) hsv[0], (int) (hsv[1]), (int) (hsv[2])));
                    } else {
                        serverHandler.sendCommand(widget.getItem().getName(), Constants.COMMAND_OFF);
                    }
                }
            }, 300);
        }
    };
}
