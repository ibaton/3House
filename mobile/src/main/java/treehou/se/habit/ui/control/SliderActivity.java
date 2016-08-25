package treehou.se.habit.ui.control;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.trello.rxlifecycle.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHItem;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.Connector;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.SliderCellDB;
import treehou.se.habit.util.ConnectionFactory;

public class SliderActivity extends AppCompatActivity {
    public static final String TAG = "SliderActivity";

    public static final String ACTION_NUMBER = "active";
    public static final String ARG_CELL = "arg_cell";

    public static final String SLIDER_TAG = "sliderDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        ButterKnife.bind(this);
        long id = getIntent().getExtras().getLong(ARG_CELL);
        SliderFragment sliderFragment = SliderFragment.newInstance(id);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, sliderFragment, SLIDER_TAG)
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @OnClick(R.id.container)
    void closeClick() {
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SliderFragment extends RxFragment {

        private SliderCellDB numberCell;
        private Realm realm;
        private SeekBar sbrNumber;

        @Inject ConnectionFactory connectionFactory;

        public SliderFragment() {}

        public static SliderFragment newInstance(long id) {
            SliderFragment fragment = new SliderFragment();
            Bundle args = new Bundle();
            args.putLong(ARG_CELL, id);
            fragment.setArguments(args);
            return fragment;
        }

        SeekBar.OnSeekBarChangeListener sliderListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (numberCell != null) {
                    OHServer server = numberCell.getItem().getServer().toGeneric();
                    IServerHandler serverHandler = connectionFactory.createServerHandler(server, getContext());
                    serverHandler.sendCommand(numberCell.getItem().getName(), "" + seekBar.getProgress());
                }
            }
        };

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ((HabitApplication) getActivity().getApplication()).component().inject(this);

            realm = Realm.getDefaultInstance();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            try {
                View rootView = inflater.inflate(R.layout.fragment_slider, null, false);

                if (getArguments() != null) {
                    long id = getArguments().getLong(ARG_CELL);
                    CellDB cell = CellDB.load(realm, id);
                    numberCell = SliderCellDB.getCell(realm, cell);
                }

                sbrNumber = (SeekBar) rootView.findViewById(R.id.sbrNumber);
                sbrNumber.setMax(numberCell.getMax());
                sbrNumber.setOnSeekBarChangeListener(sliderListener);
                return rootView;
            }catch (Exception e){
                return inflater.inflate(R.layout.item_widget_null, null, false);
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            OHServer server = numberCell.getItem().getServer().toGeneric();
            IServerHandler serverHandler = new Connector.ServerHandler(server, getContext());
            serverHandler.requestItemRx(numberCell.getItem().getName())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindToLifecycle())
                    .subscribe(ohItem -> {
                        try {
                            if (ohItem != null && ohItem.getState() != null) {
                                sbrNumber.setOnSeekBarChangeListener(null);
                                float progress = Float.valueOf(ohItem.getState());
                                sbrNumber.setProgress((int) progress);
                                sbrNumber.setOnSeekBarChangeListener(sliderListener);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to update progress", e);
                        }
                    }, e -> {
                        Log.e(TAG, "Error getting slider data", e);
                    });
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            realm.close();
        }
    }
}
