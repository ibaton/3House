package treehou.se.habit.ui.control;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import treehou.se.habit.R;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.Server;
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

        overridePendingTransition(R.animator.dialog_in,R.animator.dialog_out);
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.animator.dialog_in,R.animator.dialog_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    Long id = getArguments().getLong(ARG_CELL);
                    Cell cell = Cell.load(Cell.class, id);
                    numberCell = cell.sliderCell();
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
                            Server server = numberCell.getItem().getServer();
                            Communicator communicator = Communicator.instance(getActivity());
                            communicator.command(server, numberCell.getItem(), "" + seekBar.getProgress());
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
