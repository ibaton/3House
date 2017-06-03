package treehou.se.habit.ui.control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import se.treehou.ng.ohcommunicator.services.IServerHandler;
import treehou.se.habit.BaseActivity;
import treehou.se.habit.HabitApplication;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.SliderCellDB;
import treehou.se.habit.ui.BaseFragment;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.Util;

public class SliderActivity extends BaseActivity {
    public static final String TAG = "SliderActivity";

    public static final String ACTION_NUMBER = "active";
    public static final String ARG_CELL = "arg_cell";

    public static final String SLIDER_TAG = "sliderDialog";

    @Inject ConnectionFactory connectionFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.getApplicationComponent(this).inject(this);
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
    public static class SliderFragment extends BaseFragment {

        private SliderCellDB sliederCell;
        private SeekBar sbrNumber;
        private TextView itemName;

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
                if (sliederCell != null) {
                    OHServer server = sliederCell.getItem().getServer().toGeneric();
                    IServerHandler serverHandler = connectionFactory.createServerHandler(server, getContext());
                    serverHandler.sendCommand(sliederCell.getItem().getName(), "" + seekBar.getProgress());
                }
            }
        };

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ((HabitApplication) getActivity().getApplication()).component().inject(this);
            if (getArguments() != null) {
                long id = getArguments().getLong(ARG_CELL);
                logger.d(TAG, "Loading cell " + id);
                CellDB cell = CellDB.load(realm, id);
                sliederCell = cell.getCellSlider();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            try {
                View rootView = inflater.inflate(R.layout.fragment_slider, null, false);

                itemName = (TextView) rootView.findViewById(R.id.item_name);
                sbrNumber = (SeekBar) rootView.findViewById(R.id.sbrNumber);
                sbrNumber.setMax(sliederCell.getMax());
                sbrNumber.setOnSeekBarChangeListener(sliderListener);
                return rootView;
            }catch (Exception e){
                logger.e(TAG, "Slider adapter inflater fail", e);
                return inflater.inflate(R.layout.item_widget_null, null, false);
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            OHServer server = sliederCell.getItem().getServer().toGeneric();
            IServerHandler serverHandler = connectionFactory.createServerHandler(server, getContext());
            serverHandler.requestItemRx(sliederCell.getItem().getName())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindToLifecycle())
                    .subscribe(ohItem -> {
                        try {
                            if (ohItem != null && ohItem.getState() != null) {
                                if(ohItem.getLabel() != null){
                                    itemName.setVisibility(View.VISIBLE);
                                    itemName.setText(Util.createLabel(getContext(), ohItem.getLabel()));
                                } else {
                                    itemName.setVisibility(View.GONE);
                                }

                                sbrNumber.setOnSeekBarChangeListener(null);
                                float progress = Float.valueOf(ohItem.getState());
                                sbrNumber.setProgress((int) progress);
                                sbrNumber.setOnSeekBarChangeListener(sliderListener);
                            }
                        } catch (Exception e) {
                            logger.e(TAG, "Failed to update progress", e);
                        }
                    }, e -> {
                        logger.e(TAG, "Error getting slider data", e);
                    });
        }
    }
}
