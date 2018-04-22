package treehou.se.habit.ui.settings.subsettings.wiget

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.R
import treehou.se.habit.core.db.settings.WidgetSettingsDB
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.fragment.WidgetSettingsComponent
import treehou.se.habit.dagger.fragment.WidgetSettingsModule
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.widgets.DummyWidgetFactory
import treehou.se.habit.util.Constants
import javax.inject.Inject

class WidgetSettingsFragment : BaseDaggerFragment<WidgetSettingsContract.Presenter>(), WidgetSettingsContract.View {

    private var displayWidget: OHWidget? = null

    @BindView(R.id.widget_holder) lateinit var widgetHolder: FrameLayout
    @BindView(R.id.img_widget_icon1) lateinit var backgroundColorMuted: ImageView
    @BindView(R.id.img_widget_icon2) lateinit var backgroundColorLightMuted: ImageView
    @BindView(R.id.img_widget_icon3) lateinit var backgroundColorDark: ImageView
    @BindView(R.id.img_widget_icon4) lateinit var backgroundColorVibrant: ImageView
    @BindView(R.id.img_widget_icon5) lateinit var backgroundColorLightVibrant: ImageView
    @BindView(R.id.img_widget_icon6) lateinit var backgroundColorDarkVibrant: ImageView
    @BindView(R.id.cbx_enable_image_background) lateinit var cbxEnableImageBackground: CheckBox
    @BindView(R.id.lou_icon_backgrounds) lateinit var louIconBackground: View
    @BindView(R.id.bar_image_size) lateinit var widgetImageSize: SeekBar
    @BindView(R.id.bar_text_size) lateinit var barTextSize: SeekBar
    @BindView(R.id.swt_compressed_button) lateinit var swtCompressButton: Switch
    @BindView(R.id.swt_compressed_slider) lateinit var swtCompressSlider: Switch

    @Inject lateinit var settingsPresenter: WidgetSettingsContract.Presenter

    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = Realm.getDefaultInstance()

        displayWidget = OHWidget()
        displayWidget!!.type = "Dummy"
        displayWidget!!.label = activity!!.getString(R.string.label_widget_text)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_settings_widget, container, false)
        unbinder = ButterKnife.bind(this, rootView)

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.setTitle(R.string.settings_widget)

        val settings = WidgetSettingsDB.loadGlobal(realm)

        redrawWidget()

        barTextSize.progress = settings.textSize - Constants.MIN_TEXT_ADDON
        barTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                getPresenter().setWidgetTextSize(Constants.MIN_TEXT_ADDON + progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        setupBackgroundColorSelector()

        widgetImageSize.progress = settings.iconSize - BASE_IMAGE_SIZE

        widgetImageSize.progress = settings.iconSize - Constants.MIN_TEXT_ADDON
        widgetImageSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                getPresenter().setWidgetImageSize(BASE_IMAGE_SIZE + progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        swtCompressButton.isChecked = settings.isCompressedSingleButton
        swtCompressButton.setOnCheckedChangeListener { _, isChecked -> getPresenter().setCompressedWidgetButton(isChecked) }

        swtCompressSlider.isChecked = settings.isCompressedSlider
        swtCompressSlider.setOnCheckedChangeListener { _, isChecked -> getPresenter().setCompressedWidgetSlider(isChecked) }

        // Inflate the layout for this fragment
        return rootView
    }

    private fun setupBackgroundColorSelector() {
        val factory = DummyWidgetFactory(activity)
        val bitmap = BitmapFactory.decodeResource(activity!!.resources, R.drawable.ic_item_settings_widget)

        factory.setBackgroundColor(backgroundColorMuted, bitmap, WidgetSettingsDB.MUTED_COLOR)
        backgroundColorMuted.setOnClickListener(BackgroundSelectListener(WidgetSettingsDB.MUTED_COLOR))

        factory.setBackgroundColor(backgroundColorLightMuted, bitmap, WidgetSettingsDB.LIGHT_MUTED_COLOR)
        backgroundColorLightMuted.setOnClickListener(BackgroundSelectListener(WidgetSettingsDB.LIGHT_MUTED_COLOR))

        factory.setBackgroundColor(backgroundColorDark, bitmap, WidgetSettingsDB.DARK_MUTED_COLOR)
        backgroundColorDark.setOnClickListener(BackgroundSelectListener(WidgetSettingsDB.DARK_MUTED_COLOR))

        factory.setBackgroundColor(backgroundColorVibrant, bitmap, WidgetSettingsDB.VIBRANT_COLOR)
        backgroundColorVibrant.setOnClickListener(BackgroundSelectListener(WidgetSettingsDB.VIBRANT_COLOR))

        factory.setBackgroundColor(backgroundColorLightVibrant, bitmap, WidgetSettingsDB.LIGHT_VIBRANT_COLOR)
        backgroundColorLightVibrant.setOnClickListener(BackgroundSelectListener(WidgetSettingsDB.LIGHT_VIBRANT_COLOR))

        factory.setBackgroundColor(backgroundColorDarkVibrant, bitmap, WidgetSettingsDB.DARK_VIBRANT_COLOR)
        backgroundColorDarkVibrant.setOnClickListener(BackgroundSelectListener(WidgetSettingsDB.DARK_VIBRANT_COLOR))
    }

    override fun onResume() {
        super.onResume()

        WidgetSettingsDB.loadGlobalRx(realm)
                .map { widgetSettingsDBs -> widgetSettingsDBs.first()!!.imageBackground >= 0 }
                .compose(bindToLifecycle())
                .subscribe ({ useBackground ->
                    cbxEnableImageBackground.setOnCheckedChangeListener(null)
                    cbxEnableImageBackground.isChecked = useBackground!!
                    cbxEnableImageBackground.setOnCheckedChangeListener { _, checked ->
                        val backgroundType = if (checked) WidgetSettingsDB.MUTED_COLOR else WidgetSettingsDB.NO_COLOR
                        getPresenter().setWidgetBackground(backgroundType)
                        setWidgetBackground(backgroundType)
                    }

                    louIconBackground.visibility = if (useBackground) View.VISIBLE else View.GONE
                }, {logger.e(TAG, "Widget settings load failed", it)})
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    override fun setCompressedWidgetButton(isChecked: Boolean) {
        redrawWidget()
    }

    override fun setCompressedWidgetSlider(isChecked: Boolean) {
        redrawWidget()
    }

    private fun redrawWidget() {
        val factory = DummyWidgetFactory(activity)
        val widget = factory.createWidget(displayWidget)

        widgetHolder.removeAllViews()
        widgetHolder.addView(widget)
    }

    override fun setWidgetBackground(backgroundType: Int) {
        redrawWidget()
    }

    override fun setWidgetTextSize(size: Int) {
        redrawWidget()
    }

    override fun setWidgetImageSize(size: Int) {
        redrawWidget()
    }

    override fun getPresenter(): WidgetSettingsContract.Presenter {
        return settingsPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(WidgetSettingsFragment::class.java) as WidgetSettingsComponent.Builder)
                .fragmentModule(WidgetSettingsModule(this))
                .build().injectMembers(this)
    }

    private inner class BackgroundSelectListener constructor(private val backgroundType: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            getPresenter().setWidgetBackground(backgroundType)
        }
    }

    companion object {

        private val TAG = "WidgetSettingsFragment"

        private val BASE_IMAGE_SIZE = 50

        fun newInstance(): WidgetSettingsFragment {
            val fragment = WidgetSettingsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
