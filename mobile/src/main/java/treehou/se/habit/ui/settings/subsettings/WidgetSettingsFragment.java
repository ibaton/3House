package treehou.se.habit.ui.settings.subsettings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;

import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;
import rx.functions.Func1;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.util.Constants;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.ui.widgets.DummyWidgetFactory;

public class WidgetSettingsFragment extends RxFragment {

    private static final String TAG = "WidgetSettingsFragment";

    private static final int BASE_IMAGE_SIZE = 50;

    private OHWidget displayWidget;

    @BindView(R.id.widget_holder) FrameLayout widgetHolder;
    @BindView(R.id.img_widget_icon1) ImageView img1;
    @BindView(R.id.img_widget_icon2) ImageView img2;
    @BindView(R.id.img_widget_icon3) ImageView img3;
    @BindView(R.id.img_widget_icon4) ImageView img4;
    @BindView(R.id.img_widget_icon5) ImageView img5;
    @BindView(R.id.img_widget_icon6) ImageView img6;
    @BindView(R.id.cbx_enable_image_background) CheckBox cbxEnableImageBackground;
    @BindView(R.id.lou_icon_backgrounds) View louIconBackground;
    @BindView(R.id.bar_image_size) SeekBar barImageSize;
    @BindView(R.id.bar_text_size) SeekBar barTextSize;
    @BindView(R.id.swt_compressed_button) Switch swtCompressButton;
    @BindView(R.id.swt_compressed_slider) Switch swtCompressSlider;

    private Realm realm;
    private Unbinder unbinder;

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
        displayWidget.setType("Dummy");
        displayWidget.setLabel(getActivity().getString(R.string.label_widget_text));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings_widget, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.settings_widget);

        final WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);

        redrawWidget();

        barTextSize.setProgress(settings.getTextSize()-Constants.MIN_TEXT_ADDON);
        barTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                realm.beginTransaction();
                settings.setTextSize(Constants.MIN_TEXT_ADDON+progress);
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

        DummyWidgetFactory factory = new DummyWidgetFactory(getActivity());
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_item_settings_widget);

        factory.setBackgroundColor(img1,bitmap, WidgetSettingsDB.MUTED_COLOR);
        img1.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.MUTED_COLOR));

        factory.setBackgroundColor(img2,bitmap, WidgetSettingsDB.LIGHT_MUTED_COLOR);
        img2.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.LIGHT_MUTED_COLOR));

        factory.setBackgroundColor(img3,bitmap, WidgetSettingsDB.DARK_MUTED_COLOR);
        img3.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.DARK_MUTED_COLOR));

        factory.setBackgroundColor(img4,bitmap, WidgetSettingsDB.VIBRANT_COLOR);
        img4.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.VIBRANT_COLOR));

        factory.setBackgroundColor(img5,bitmap, WidgetSettingsDB.LIGHT_VIBRANT_COLOR);
        img5.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.LIGHT_VIBRANT_COLOR));

        factory.setBackgroundColor(img6,bitmap, WidgetSettingsDB.DARK_VIBRANT_COLOR);
        img6.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.DARK_VIBRANT_COLOR));

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

        swtCompressButton.setChecked(settings.isCompressedSingleButton());
        swtCompressButton.setOnCheckedChangeListener((buttonView, isChecked) -> setCompressedButtonChanged(isChecked));

        swtCompressSlider.setChecked(settings.isCompressedSlider());
        swtCompressSlider.setOnCheckedChangeListener((buttonView, isChecked) -> setCompressedSliderChanged(isChecked));

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        WidgetSettingsDB.loadGlobalRx(realm)
                .map(widgetSettingsDBs -> widgetSettingsDBs.first().getImageBackground() >= 0)
                .compose(bindToLifecycle())
                .subscribe(useBackground -> {
                    cbxEnableImageBackground.setOnCheckedChangeListener(null);
                    cbxEnableImageBackground.setChecked(useBackground);
                    cbxEnableImageBackground.setOnCheckedChangeListener((compoundButton, checked) -> updateBackground(checked ?  WidgetSettingsDB.MUTED_COLOR : WidgetSettingsDB.NO_COLOR));

                    louIconBackground.setVisibility(useBackground ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

    private void updateBackground(int backgroundType){
        WidgetSettingsDB settings = WidgetSettingsDB.loadGlobal(realm);
        realm.beginTransaction();
        settings.setImageBackground(backgroundType);
        realm.commitTransaction();

        redrawWidget();
    }

    private class BackgroundSelectListener implements View.OnClickListener{

        private int backgroundType;

        private BackgroundSelectListener(int backgroundType) {
            this.backgroundType = backgroundType;
        }

        @Override
        public void onClick(View v) {
            updateBackground(backgroundType);
        }
    }
}
