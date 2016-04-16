package treehou.se.habit.ui.settings.subsettings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.Constants;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.DummyWidgetFactory;

public class WidgetSettingsFragment extends Fragment {

    private static final String TAG = "WidgetSettingsFragment";

    private static final int BASE_IMAGE_SIZE = 50;

    private OHWidget displayWidget;
    @Bind(R.id.widget_holder) FrameLayout widgetHolder;
    private Realm realm;

    public static WidgetSettingsFragment newInstance() {
        WidgetSettingsFragment fragment = new WidgetSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public WidgetSettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        displayWidget = new OHWidget();
        displayWidget.setType(OHWidget.TYPE_DUMMY);
        displayWidget.setLabel(getActivity().getString(R.string.label_widget_text));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getActivity().getString(R.string.settings_widget));

        final WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);

        View rootView = inflater.inflate(R.layout.fragment_settings_widget, container, false);
        ButterKnife.bind(this, rootView);

        redrawWidget();

        SeekBar barTextSize = (SeekBar) rootView.findViewById(R.id.bar_text_size);
        barTextSize.setProgress(settings.getTextSize()-Constants.MIN_TEXT_ADDON);
        barTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                realm.beginTransaction();
                settings.setTextSize(Constants.MIN_TEXT_ADDON+progress);
                realm.close();
                redrawWidget();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        DummyWidgetFactory factory = new DummyWidgetFactory(getActivity());
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_item_settings_widget);

        ImageView img1 = (ImageView) rootView.findViewById(R.id.img_widget_icon1);
        factory.setBackgroundColor(img1,bitmap, WidgetSettingsDB.MUTED_COLOR);
        img1.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.MUTED_COLOR));

        ImageView img2 = (ImageView) rootView.findViewById(R.id.img_widget_icon2);
        factory.setBackgroundColor(img2,bitmap, WidgetSettingsDB.LIGHT_MUTED_COLOR);
        img2.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.LIGHT_MUTED_COLOR));

        ImageView img3 = (ImageView) rootView.findViewById(R.id.img_widget_icon3);
        factory.setBackgroundColor(img3,bitmap, WidgetSettingsDB.DARK_MUTED_COLOR);
        img3.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.DARK_MUTED_COLOR));

        ImageView img4 = (ImageView) rootView.findViewById(R.id.img_widget_icon4);
        factory.setBackgroundColor(img4,bitmap, WidgetSettingsDB.VIBRANT_COLOR);
        img4.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.VIBRANT_COLOR));

        ImageView img5 = (ImageView) rootView.findViewById(R.id.img_widget_icon5);
        factory.setBackgroundColor(img5,bitmap, WidgetSettingsDB.LIGHT_VIBRANT_COLOR);
        img5.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.LIGHT_VIBRANT_COLOR));

        ImageView img6 = (ImageView) rootView.findViewById(R.id.img_widget_icon6);
        factory.setBackgroundColor(img6,bitmap, WidgetSettingsDB.DARK_VIBRANT_COLOR);
        img6.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.DARK_VIBRANT_COLOR));

        SeekBar barImageSize = (SeekBar) rootView.findViewById(R.id.bar_image_size);
        barImageSize.setProgress(settings.getIconSize()-BASE_IMAGE_SIZE);

        barImageSize.setProgress(settings.getIconSize()-Constants.MIN_TEXT_ADDON);
        barImageSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                realm.beginTransaction();
                settings.setIconSize(BASE_IMAGE_SIZE + progress);
                realm.commitTransaction();
                redrawWidget();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // TODO Add image showing change

        SwitchCompat swtCompressButton = (SwitchCompat) rootView.findViewById(R.id.swt_compressed_button);
        swtCompressButton.setChecked(settings.isCompressedSingleButton());
        swtCompressButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCompressedButtonChanged(isChecked);
            }
        });

        SwitchCompat swtCompressSlider = (SwitchCompat) rootView.findViewById(R.id.swt_compressed_slider);
        swtCompressSlider.setChecked(settings.isCompressedSlider());
        swtCompressSlider.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCompressedSliderChanged(isChecked);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    private void setCompressedButtonChanged(boolean isChecked){
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        realm.beginTransaction();
        settings.setCompressedSingleButton(isChecked);
        realm.commitTransaction();
    }

    private void setCompressedSliderChanged(boolean isChecked){
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        realm.beginTransaction();
        settings.setCompressedSlider(isChecked);
        realm.commitTransaction();
    }

    private void redrawWidget(){
        DummyWidgetFactory factory = new DummyWidgetFactory(getActivity());
        View widget = factory.createWidget(displayWidget);

        widgetHolder.removeAllViews();
        Log.d(TAG, "Settings widget " + widget);
        widgetHolder.addView(widget);
    }

    private class BackgroundSelectListener implements View.OnClickListener{

        private int backgroundType;

        private BackgroundSelectListener(int backgroundType) {
            this.backgroundType = backgroundType;
        }

        @Override
        public void onClick(View v) {
            WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
            realm.beginTransaction();
            settings.setImageBackground(backgroundType);
            realm.commitTransaction();

            redrawWidget();
        }
    }
}
