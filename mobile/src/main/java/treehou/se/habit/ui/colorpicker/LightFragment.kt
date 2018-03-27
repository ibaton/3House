package treehou.se.habit.ui.colorpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import io.realm.Realm
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import se.treehou.ng.ohcommunicator.util.GsonHelper
import treehou.se.habit.R
import treehou.se.habit.core.db.model.ServerDB
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.fragment.LightComponent
import treehou.se.habit.dagger.fragment.LightModule
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.colorpicker.LightContract.Presenter
import treehou.se.habit.util.ConnectionFactory
import java.util.*
import javax.inject.Inject

class LightFragment : BaseDaggerFragment<Presenter>(), LightContract.View {

    @BindView(R.id.lbl_name) lateinit var lblName: TextView
    @BindView(R.id.pcr_color_h) lateinit var pcrColor: ColorPicker

    @Inject lateinit var connectionFactory: ConnectionFactory
    @Inject lateinit var lightPresenter: Presenter

    private var server: OHServer? = null
    private var widget: OHWidget? = null
    private var color: Int = 0

    private var timer = Timer()
    private var unbinder: Unbinder? = null

    private val colorChangeListener = object: ColorPicker.ColorChangeListener {
        override fun onColorChange(hsv : FloatArray) {
            timer.cancel()
            timer.purge()
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    hsv[1] = hsv[1] * 100f
                    hsv[2] = hsv[2] * 100f

                    val hue = hsv[0].toInt()
                    val saturation = hsv[1].toInt()
                    val value = hsv[2].toInt()

                    lightPresenter.setHSV(widget!!.item, hue, saturation, value)
                }
            }, 300)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = Realm.getDefaultInstance()

        val args = arguments
        val serverId = args!!.getLong(ARG_SERVER)
        val jWidget = args.getString(ARG_WIDGET)
        color = args.getInt(ARG_COLOR)

        val gson = GsonHelper.createGsonBuilder()
        server = ServerDB.load(realm, serverId)?.toGeneric()
        widget = gson.fromJson(jWidget, OHWidget::class.java)
    }

    override fun getPresenter(): Presenter {
        return lightPresenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_colorpicker, container, false)
        unbinder = ButterKnife.bind(this, rootView)

        lblName.text = widget!!.label
        pcrColor.color = color

        return rootView
    }

    override fun onResume() {
        super.onResume()
        pcrColor.setOnColorChangeListener(colorChangeListener)
    }

    override fun onPause() {
        super.onPause()
        pcrColor.setOnColorChangeListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }


    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(LightFragment::class.java) as LightComponent.Builder)
                .fragmentModule(LightModule(this, arguments!!))
                .build().injectMembers(this)
    }

    companion object {

        private val TAG = "LightFragment"

        private val ARG_SERVER = "ARG_SERVER"
        private val ARG_WIDGET = "ARG_SITEMAP"
        private val ARG_COLOR = "ARG_COLOR"

        fun newInstance(serverId: Long, widget: OHWidget, color: Int): LightFragment {
            val fragment = LightFragment()

            val args = Bundle()
            val gson = GsonHelper.createGsonBuilder()
            args.putLong(ARG_SERVER, serverId)
            args.putString(ARG_WIDGET, gson.toJson(widget))
            args.putInt(ARG_COLOR, color)
            fragment.arguments = args

            return fragment
        }
    }
}
