package treehou.se.habit.ui.control;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.connector.models.OHServer;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.core.db.model.controller.SliderCellDB;

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
    public static class SliderFragment extends Fragment {

        private SliderCellDB numberCell;
        private Realm realm;

        public SliderFragment() {}

        public static SliderFragment newInstance(long id) {
            SliderFragment fragment = new SliderFragment();
            Bundle args = new Bundle();
            args.putLong(ARG_CELL, id);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

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

                SeekBar sbrNumber = (SeekBar) rootView.findViewById(R.id.sbrNumber);
                sbrNumber.setMax(numberCell.getMax());
                sbrNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                            Openhab.instance(server).sendCommand(numberCell.getItem().getName(), "" + seekBar.getProgress());
                        }
                    }
                });
                return rootView;
            }catch (Exception e){
                return inflater.inflate(R.layout.item_widget_null, null, false);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            ButterKnife.unbind(this);
            realm.close();
        }
    }
}
