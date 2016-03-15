package treehou.se.habit.ui.control;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import se.treehou.ng.ohcommunicator.Openhab;
import se.treehou.ng.ohcommunicator.core.OHServerWrapper;
import treehou.se.habit.R;
import treehou.se.habit.core.controller.Cell;
import treehou.se.habit.core.controller.SliderCell;

public class SliderActivity extends AppCompatActivity {
    public static final String TAG = "SliderActivity";

    public static final String ACTION_NUMBER = "active";
    public static final String ARG_CELL = "arg_cell";

    public static final String SLIDER_TAG = "sliderDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        long id = getIntent().getExtras().getLong(ARG_CELL);
        SliderFragment sliderFragment = SliderFragment.newInstance(id);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, sliderFragment, SLIDER_TAG)
                    .commit();
        }
        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SliderFragment extends Fragment {

        private SliderCell numberCell;

        public SliderFragment() {}

        public static SliderFragment newInstance(long id) {
            SliderFragment fragment = new SliderFragment();
            Bundle args = new Bundle();
            args.putLong(ARG_CELL, id);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            try {
                View rootView = inflater.inflate(R.layout.fragment_slider, null, false);

                if (getArguments() != null) {
                    int id = getArguments().getInt(ARG_CELL);
                    Cell cell = Cell.load(id);
                    numberCell = SliderCell.getCell(cell);
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
                            OHServerWrapper server = numberCell.getItem().getServer();
                            Openhab.instance(server).sendCommand(numberCell.getItem().getName(), "" + seekBar.getProgress());
                        }
                    }
                });
                return rootView;
            }catch (Exception e){
                return inflater.inflate(R.layout.item_widget_null, null, false);
            }
        }
    }
}
