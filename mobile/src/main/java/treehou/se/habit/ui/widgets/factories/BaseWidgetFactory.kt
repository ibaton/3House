package treehou.se.habit.ui.widgets.factories

import android.content.Context
import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.connector.models.OHWidget
import treehou.se.habit.R
import treehou.se.habit.connector.Communicator
import treehou.se.habit.core.db.settings.WidgetSettingsDB
import treehou.se.habit.ui.widgets.WidgetFactory
import treehou.se.habit.util.OpenHabUtil
import treehou.se.habit.util.Util
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class BaseWidgetFactory {

    @JvmOverloads
    fun build(
            context: Context, widgetFactory: WidgetFactory,
            server: OHServer, page: OHLinkedPage,
            widget: OHWidget, parent: OHWidget?, flat: Boolean = false): WidgetFactory.IWidgetHolder {

        val inflater = LayoutInflater.from(context)
        var rootView = if (flat) inflater.inflate(R.layout.item_widget_base_flat, null) else inflater.inflate(R.layout.item_widget_base, null)


        // Put in dummy frame if not in frame
        if (parent == null) {
            val holderView = inflater.inflate(R.layout.widget_container, null)
            val holder = holderView.findViewById<View>(R.id.lou_widget_frame_holder) as LinearLayout
            holder.addView(rootView)

            rootView = holderView
        }

        val holder = BaseWidgetHolder(context, rootView, server, page, widget, widgetFactory)
        holder.update(widget)

        return holder
    }

    class BaseWidgetHolder internal constructor(internal var context: Context, internal var rootView: View?, private val server: OHServer, private val page: OHLinkedPage, widget: OHWidget?, internal var widgetFactory: WidgetFactory) : WidgetFactory.WidgetHolder(rootView) {
        internal var baseDataHolder: View? = null
        internal var lblName: TextView
        internal var iconHolder: View
        internal var imgIcon: ImageView
        internal var btnNextPage: ImageButton
        var subView: LinearLayout
            internal set
        internal var showLabel = true

        private val labelColor: ColorStateList

        private val widgetHolders = ArrayList<WidgetFactory.IWidgetHolder>()
        private val widgets = ArrayList<OHWidget>()
        /**
         * Get previously stored widget.
         *
         * @return widget
         */
        protected var widget: OHWidget? = null
            private set

        init {
            this.widget = widget
            baseDataHolder = rootView!!.findViewById(R.id.lou_base_data_holder)
            iconHolder = rootView!!.findViewById(R.id.img_widget_icon_holder)
            lblName = rootView!!.findViewById(R.id.lbl_widget_name)
            imgIcon = rootView!!.findViewById(R.id.img_widget_icon)
            btnNextPage = rootView!!.findViewById(R.id.btn_next_page)
            subView = rootView!!.findViewById(R.id.lou_widget_holder)

            labelColor = lblName.textColors
        }

        override fun update(widget: OHWidget?) {

            if (widget == null) {
                return
            }

            Log.d(TAG, "update " + (if (widget.item != null) widget.item.name else "") + " " + widget.label)
            setLabelColor(Util.getLabelColor(context, widget.labelColor))
            setName(widget.label, widget.valueColor)

            if (widget.linkedPage != null) {
                btnNextPage.visibility = View.VISIBLE
                rootView?.setOnClickListener { EventBus.getDefault().post(widget.linkedPage) }
            } else {
                btnNextPage.visibility = View.GONE
            }

            loadIcon(widget)

            this.widget = widget
        }

        @Synchronized
        private fun updateWidgets(pageWidgets: List<OHWidget>) {

            Log.d(TAG, "frame widgets update " + pageWidgets.size + " : " + widgets.size)
            var invalidate = pageWidgets.size != widgets.size
            if (!invalidate) {
                for (i in widgets.indices) {
                    //val currentWidget = widgets[i]
                    //val newWidget = pageWidgets[i]

                    // TODO handle update
                    //if(currentWidget.needUpdate(newWidget)){
                    invalidate = true
                    //    break;
                    //}
                }
            }

            if (invalidate) {
                Log.d(TAG, "Invalidating frame widgets " + pageWidgets.size + " : " + widgets.size)

                widgetHolders.clear()
                subView.removeAllViews()

                for (widget in pageWidgets) {
                    val result = widgetFactory.createWidget(context, server, page, widget, null)
                    widgetHolders.add(result)
                    subView.addView(result.view)
                }
                widgets.clear()
                widgets.addAll(pageWidgets)
            } else {
                Log.d(TAG, "updating widgets")
                for (i in widgetHolders.indices) {
                    val holder = widgetHolders[i]

                    Log.d(TAG, "updating widget " + holder.javaClass.simpleName)
                    val newWidget = pageWidgets[i]

                    holder.update(newWidget)
                }
            }

            widgets.clear()
            widgets.addAll(pageWidgets)
        }

        /**
         * Set name of widget
         *
         * @param name
         */
        private fun setName(name: String, color: String?) {
            Log.d(TAG, "setName " + name)

            lblName.text = Util.createLabel(context, name, color)

            if (baseDataHolder != null && "" == name.trim { it <= ' ' }) {
                baseDataHolder!!.visibility = View.GONE
            }
        }

        private fun setLabelColor(labelColor: Int) {
            lblName.setTextColor(labelColor)
        }

        /**
         * Set name of widget
         *
         * @param showLabel
         */
        private fun setShowLabel(showLabel: Boolean) {

            if (this.showLabel != showLabel) {
                this.showLabel = showLabel

                baseDataHolder!!.visibility = if (showLabel) View.VISIBLE else View.GONE
            }
        }

        /**
         * Load icon and populate image view with it.
         * @param widget the widget to load icon for.
         */
        private fun loadIcon(widget: OHWidget) {

            // TODO text is default value. Remove when fixed on server
            val ignoreDefault = "text".equals(widget.icon, ignoreCase = true)
            if (widget.iconPath != null && !ignoreDefault) {
                iconHolder.visibility = View.VISIBLE
                imgIcon.visibility = View.INVISIBLE
                try {
                    Log.d(TAG, "widget.getIconPath " + widget.iconPath + " : " + page.baseUrl)
                    val imageUrl = URL(page.baseUrl + widget.iconPath)
                    val communicator = Communicator.instance(context)
                    communicator.loadImage(server, imageUrl, imgIcon, false)
                    Log.d(TAG, "Loading image url " + imageUrl)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }

            } else {
                iconHolder.visibility = View.INVISIBLE
                imgIcon.visibility = View.INVISIBLE
                if (baseDataHolder != null && (widget.label == null || "" == widget.label.trim { it <= ' ' })) {
                    baseDataHolder!!.visibility = View.GONE
                }
            }
        }

        class Builder(private val context: Context, private val factory: WidgetFactory, val server: OHServer, val page: OHLinkedPage) {

            private var widget: OHWidget? = null
            private var parent: OHWidget? = null
            private var view: View? = null
            private var flat = false
            private var showLabel = true

            /**
             * Set the widget to populate view with.
             *
             * @param widget the widget to display.
             * @return this builder.
             */
            fun setWidget(widget: OHWidget): Builder {
                this.widget = widget
                return this
            }

            /**
             * Set if the widget should be displayed compressed.
             *
             * @param flat true to show it compressed, else false.
             * @return this builder.
             */
            fun setFlat(flat: Boolean): Builder {
                this.flat = flat
                return this
            }

            /**
             * Set parent widget
             * @param parent the parent of widget.
             * @return this builder.
             */
            fun setParent(parent: OHWidget): Builder {
                this.parent = parent
                return this
            }

            /**
             * The view to display widget in.
             *
             * @param view the view to display.
             * @return this builder.
             */
            fun setView(view: View): Builder {
                this.view = view
                return this
            }

            fun setShowLabel(showLabel: Boolean): Builder {
                this.showLabel = showLabel
                return this
            }

            fun getWidget(): OHWidget? {
                return widget
            }

            fun getParent(): OHWidget? {
                return parent
            }

            fun getView(): View? {
                return view
            }

            fun isFlat(): Boolean {
                return flat
            }

            fun isShowLabel(): Boolean {
                return showLabel
            }

            fun build(): BaseWidgetHolder {

                val baseBuilderHolder = BaseWidgetHolder.create(context, factory, this)
                baseBuilderHolder.setShowLabel(showLabel)

                return baseBuilderHolder
            }
        }

        companion object {

            fun create(context: Context, factory: WidgetFactory, builder: Builder): BaseWidgetHolder {

                val inflater = LayoutInflater.from(context)

                var rootView = builder.getView()
                if (rootView == null) {
                    rootView = if (builder.isFlat()) inflater.inflate(R.layout.item_widget_base_flat, null) else inflater.inflate(R.layout.item_widget_base, null)
                }

                // Put in dummy frame if not in frame
                if (builder.getParent() == null) {
                    val holderView = inflater.inflate(R.layout.widget_container, null)
                    val holder = holderView.findViewById<View>(R.id.lou_widget_frame_holder) as LinearLayout
                    holder.addView(rootView)

                    rootView = holderView
                }

                val holder = BaseWidgetHolder(context, rootView, builder.server, builder.page, builder.getWidget(), factory)

                val realm = Realm.getDefaultInstance()
                val settings = WidgetSettingsDB.loadGlobal(realm)
                val percentage = Util.toPercentage(settings.textSize)
                // Set size of icon
                val imageSizePercentage = Util.toPercentage(settings.iconSize)
                realm.close()

                holder.lblName.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.lblName.textSize * percentage)

                val layoutParams = holder.imgIcon.layoutParams
                layoutParams.width = (layoutParams.width.toFloat() * imageSizePercentage).toInt()
                holder.imgIcon.layoutParams = layoutParams

                holder.update(builder.getWidget())

                return holder
            }
        }
    }

    companion object {

        private val TAG = "BaseWidgetFactory"
    }
}
