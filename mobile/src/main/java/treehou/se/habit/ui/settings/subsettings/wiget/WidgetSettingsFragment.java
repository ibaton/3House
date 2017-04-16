package treehou.se.habit.ui.settings.subsettings.wiget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.core.db.settings.WidgetSettingsDB;
import treehou.se.habit.module.HasActivitySubcomponentBuilders;
import treehou.se.habit.mvp.BaseDaggerFragment;
import treehou.se.habit.ui.widgets.DummyWidgetFactory;
import treehou.se.habit.util.Constants;

public class WidgetSettingsFragment extends BaseDaggerFragment<WidgetSettingsContract.Presenter> implements WidgetSettingsContract.View {

    private static final String TAG = "WidgetSettingsFragment";

    private static final int BASE_IMAGE_SIZE = 50;

    private OHWidget displayWidget;

    @BindView(R.id.widget_holder) FrameLayout widgetHolder;
    @BindView(R.id.img_widget_icon1) ImageView backgroundColorMuted;
    @BindView(R.id.img_widget_icon2) ImageView backgroundColorLightMuted;
    @BindView(R.id.img_widget_icon3) ImageView backgroundColorDark;
    @BindView(R.id.img_widget_icon4) ImageView backgroundColorVibrant;
    @BindView(R.id.img_widget_icon5) ImageView backgroundColorLightVibrant;
    @BindView(R.id.img_widget_icon6) ImageView backgroundColorDarkVibrant;
    @BindView(R.id.cbx_enable_image_background) CheckBox cbxEnableImageBackground;
    @BindView(R.id.lou_icon_backgrounds) View louIconBackground;
    @BindView(R.id.bar_image_size) SeekBar widgetImageSize;
    @BindView(R.id.bar_text_size) SeekBar barTextSize;
    @BindView(R.id.swt_compressed_button) Switch swtCompressButton;
    @BindView(R.id.swt_compressed_slider) Switch swtCompressSlider;

    @Inject WidgetSettingsContract.Presenter presenter;

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
                presenter.setWidgetTextSize(Constants.MIN_TEXT_ADDON+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        setupBackroundColorSelector();

        widgetImageSize.setProgress(settings.getIconSize()-BASE_IMAGE_SIZE);

        widgetImageSize.setProgress(settings.getIconSize()-Constants.MIN_TEXT_ADDON);
        widgetImageSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                presenter.setWidgetImageSize(BASE_IMAGE_SIZE + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        swtCompressButton.setChecked(settings.isCompressedSingleButton());
        swtCompressButton.setOnCheckedChangeListener((buttonView, isChecked) -> presenter.setCompressedWidgetButton(isChecked));

        swtCompressSlider.setChecked(settings.isCompressedSlider());
        swtCompressSlider.setOnCheckedChangeListener((buttonView, isChecked) -> presenter.setCompressedWidgetSlider(isChecked));

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setupBackroundColorSelector() {
        DummyWidgetFactory factory = new DummyWidgetFactory(getActivity());
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_item_settings_widget);

        factory.setBackgroundColor(backgroundColorMuted, bitmap, WidgetSettingsDB.MUTED_COLOR);
        backgroundColorMuted.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.MUTED_COLOR));

        factory.setBackgroundColor(backgroundColorLightMuted, bitmap, WidgetSettingsDB.LIGHT_MUTED_COLOR);
        backgroundColorLightMuted.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.LIGHT_MUTED_COLOR));

        factory.setBackgroundColor(backgroundColorDark, bitmap, WidgetSettingsDB.DARK_MUTED_COLOR);
        backgroundColorDark.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.DARK_MUTED_COLOR));

        factory.setBackgroundColor(backgroundColorVibrant, bitmap, WidgetSettingsDB.VIBRANT_COLOR);
        backgroundColorVibrant.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.VIBRANT_COLOR));

        factory.setBackgroundColor(backgroundColorLightVibrant, bitmap, WidgetSettingsDB.LIGHT_VIBRANT_COLOR);
        backgroundColorLightVibrant.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.LIGHT_VIBRANT_COLOR));

        factory.setBackgroundColor(backgroundColorDarkVibrant, bitmap, WidgetSettingsDB.DARK_VIBRANT_COLOR);
        backgroundColorDarkVibrant.setOnClickListener(new BackgroundSelectListener(WidgetSettingsDB.DARK_VIBRANT_COLOR));
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
                    cbxEnableImageBackground.setOnCheckedChangeListener((compoundButton, checked) -> setWidgetBackground(checked ?  WidgetSettingsDB.MUTED_COLOR : WidgetSettingsDB.NO_COLOR));

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

    @Override
    public void setCompressedWidgetButton(boolean isChecked) {
        redrawWidget();
    }

    @Override
    public void setCompressedWidgetSlider(boolean isChecked) {
        redrawWidget();
    }

    private void redrawWidget(){
        DummyWidgetFactory factory = new DummyWidgetFactory(getActivity());
        View widget = factory.createWidget(displayWidget);

        widgetHolder.removeAllViews();
        widgetHolder.addView(widget);
    }

    @Override
    public void setWidgetBackground(int backgroundType){
        redrawWidget();
    }

    @Override
    public void setWidgetTextSize(int size) {
        redrawWidget();
    }

    @Override
    public void setWidgetImageSize(int size) {
        redrawWidget();
    }

    @Override
    public WidgetSettingsContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    protected void injectMembers(HasActivitySubcomponentBuilders hasActivitySubcomponentBuilders) {
        ((WidgetSettingsComponent.Builder) hasActivitySubcomponentBuilders.getFragmentComponentBuilder(WidgetSettingsFragment.class))
                .fragmentModule(new WidgetSettingsModule(this))
                .build().injectMembers(this);
    }

    private class BackgroundSelectListener implements View.OnClickListener{

        private int backgroundType;

        private BackgroundSelectListener(int backgroundType) {
            this.backgroundType = backgroundType;
        }

        @Override
        public void onClick(View v) {
            presenter.setWidgetBackground(backgroundType);
        }
    }
}
