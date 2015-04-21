package treehou.se.habit.ui.control.config;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SaturationBar;

import treehou.se.habit.R;
import treehou.se.habit.core.Util;
import treehou.se.habit.core.Widget;

public class ColorpickerActivity extends FragmentActivity {

    public static final String EXTRA_SERVER = "EXTRA_SERVER";
    public static final String EXTRA_WIDGET = "EXTRA_SITEMAP";
    public static final String EXTRA_COLOR  = "EXTRA_COLOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorpicker);

        Bundle bundle = getIntent().getExtras();
        String jWidget = bundle.getString(EXTRA_WIDGET);
        int color = bundle.getInt(EXTRA_COLOR);

        Gson gson = Util.createGsonBuilder();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, ColorFragment.newInstance(color))
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ColorFragment extends Fragment {

        private static final String TAG = "ColorFragment";

        private static final String ARG_COLOR   = "ARG_COLOR";

        private static final String EXTRA_COLOR   = "EXTRA_COLOR";

        private int color;

        public static ColorFragment newInstance(int color){
            ColorFragment fragment = new ColorFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_COLOR ,color);
            fragment.setArguments(args);

            return fragment;
        }

        public ColorFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle args = getArguments();
            color = args.getInt(ARG_COLOR);
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_colorpicker, container, false);

            SaturationBar barSaturation = (SaturationBar) rootView.findViewById(R.id.bar_saturation);
            final OpacityBar barOpacity = (OpacityBar) rootView.findViewById(R.id.bar_opacity);
            final ColorPicker pcrColor = (ColorPicker) rootView.findViewById(R.id.pcr_color);

            pcrColor.addOpacityBar(barOpacity);
            pcrColor.addSaturationBar(barSaturation);
            pcrColor.setShowOldCenterColor(false);

            pcrColor.setColor(color);

            Button btnSave = (Button) rootView.findViewById(R.id.btn_save);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_COLOR, pcrColor.getColor());
                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                    ((FragmentActivity)getActivity()).getSupportFragmentManager().popBackStack();
                }
            });

            return rootView;
        }
    }
}
