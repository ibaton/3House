package treehou.se.habit.ui.colorpicker;

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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import treehou.se.habit.R;
import treehou.se.habit.connector.Constants;
import treehou.se.habit.core.db.model.ServerDB;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.mvp.BaseDaggerFragment;
import treehou.se.habit.ui.colorpicker.LightContract.Presenter;
import treehou.se.habit.util.ConnectionFactory;

public class LightFragment extends BaseDaggerFragment<Presenter> implements LightContract.View {

    private static final String TAG = "LightFragment";

    private static final String ARG_SERVER = "ARG_SERVER";
    private static final String ARG_WIDGET = "ARG_SITEMAP";
    private static final String ARG_COLOR = "ARG_COLOR";

    @BindView(R.id.lbl_name) TextView lblName;
    @BindView(R.id.pcr_color_h) ColorPicker pcrColor;

    @Inject ConnectionFactory connectionFactory;
    @Inject Presenter presenter;

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
    public Presenter getPresenter() {
        return presenter;
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
                    hsv[1] *= 100;
                    hsv[2] *= 100;

                    int hue = (int) hsv[0];
                    int saturation = (int) hsv[1];
                    int value = (int) hsv[2];

                    presenter.setHSV(widget.getItem(), hue, saturation, value);
                }
            }, 300);
        }
    };



    @Override
    protected void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders) {
        ((LightComponent.Builder) hasActivitySubcomponentBuilders.getFragmentComponentBuilder(LightFragment.class))
                .fragmentModule(new LightModule(this, getArguments()))
                .build().injectMembers(this);
    }
}
