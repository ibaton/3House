package treehou.se.habit.util

import android.app.Activity
import android.app.Service
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.support.annotation.ColorRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatDialog
import android.support.v7.graphics.Palette
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View

import com.mattyork.colours.Colour
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.octicons_typeface_library.Octicons

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.core.db.settings.WidgetSettingsDB
import treehou.se.habit.dagger.ApplicationComponent
import treehou.se.habit.ui.adapter.IconAdapter

object Util {

    private val CELL_ICON_MAP = HashMap<String, IIcon>()
    private val CELL_ICONS = ArrayList<IIcon>()

    val CAT_ICONS: MutableMap<IconCategory, List<IIcon>> = HashMap()

    /**
     * Get a shallow copy of all available icons.
     *
     * @return list of icons.
     */
    val icons: List<IIcon>
        get() = ArrayList(CELL_ICONS)

    init {
        CELL_ICONS.addAll(Arrays.asList<IIcon>(*CommunityMaterial.Icon.values()))
        CELL_ICONS.addAll(Arrays.asList<IIcon>(*GoogleMaterial.Icon.values()))
        CELL_ICONS.addAll(Arrays.asList<IIcon>(*Octicons.Icon.values()))
        CELL_ICONS.addAll(Arrays.asList<IIcon>(*FontAwesome.Icon.values()))

        for (icon in CELL_ICONS) {
            CELL_ICON_MAP[icon.name] = icon
        }
    }

    enum class IconCategory {
        EMPTY, SENSORS, MEDIA, COMMANDS, ARROWS, ALL
    }

    init {
        val sensors = ArrayList<IIcon>()
        sensors.add(CommunityMaterial.Icon.cmd_alarm)
        sensors.add(CommunityMaterial.Icon.cmd_alarm_plus)
        sensors.add(CommunityMaterial.Icon.cmd_alert)
        sensors.add(CommunityMaterial.Icon.cmd_bell)
        sensors.add(CommunityMaterial.Icon.cmd_bell_off)
        sensors.add(CommunityMaterial.Icon.cmd_bell_ring)
        sensors.add(CommunityMaterial.Icon.cmd_brightness_5)
        sensors.add(CommunityMaterial.Icon.cmd_brightness_6)
        sensors.add(CommunityMaterial.Icon.cmd_brightness_7)
        CAT_ICONS[IconCategory.SENSORS] = sensors

        val arrows = ArrayList<IIcon>()
        arrows.add(CommunityMaterial.Icon.cmd_arrow_down)
        arrows.add(CommunityMaterial.Icon.cmd_arrow_down_bold)
        arrows.add(CommunityMaterial.Icon.cmd_chevron_down)
        arrows.add(CommunityMaterial.Icon.cmd_arrow_up)
        arrows.add(CommunityMaterial.Icon.cmd_arrow_up_bold)
        arrows.add(CommunityMaterial.Icon.cmd_chevron_up)
        arrows.add(CommunityMaterial.Icon.cmd_arrow_left)
        arrows.add(CommunityMaterial.Icon.cmd_arrow_left_bold)
        arrows.add(CommunityMaterial.Icon.cmd_chevron_left)
        arrows.add(CommunityMaterial.Icon.cmd_arrow_right)
        arrows.add(CommunityMaterial.Icon.cmd_arrow_right_bold)
        arrows.add(CommunityMaterial.Icon.cmd_chevron_right)
        CAT_ICONS[IconCategory.ARROWS] = arrows

        val media = ArrayList<IIcon>()
        media.add(CommunityMaterial.Icon.cmd_play)
        media.add(CommunityMaterial.Icon.cmd_pause)
        media.add(CommunityMaterial.Icon.cmd_stop)
        media.add(CommunityMaterial.Icon.cmd_forward)
        media.add(CommunityMaterial.Icon.cmd_rewind)
        media.add(CommunityMaterial.Icon.cmd_skip_next)
        media.add(CommunityMaterial.Icon.cmd_skip_previous)
        media.add(CommunityMaterial.Icon.cmd_microphone_off)
        media.add(CommunityMaterial.Icon.cmd_microphone)
        media.add(CommunityMaterial.Icon.cmd_microphone_off)
        media.add(CommunityMaterial.Icon.cmd_volume_off)
        media.add(CommunityMaterial.Icon.cmd_volume_low)
        media.add(CommunityMaterial.Icon.cmd_volume_medium)
        media.add(CommunityMaterial.Icon.cmd_volume_high)
        CAT_ICONS[IconCategory.MEDIA] = media

        val commands = ArrayList<IIcon>()
        commands.add(CommunityMaterial.Icon.cmd_airplane)
        commands.add(CommunityMaterial.Icon.cmd_airplane_off)
        commands.add(CommunityMaterial.Icon.cmd_bell_ring)
        commands.add(CommunityMaterial.Icon.cmd_lock)
        commands.add(CommunityMaterial.Icon.cmd_lock_open)
        commands.add(CommunityMaterial.Icon.cmd_power)
        commands.add(CommunityMaterial.Icon.cmd_coffee)
        commands.add(CommunityMaterial.Icon.cmd_beer)
        CAT_ICONS[IconCategory.COMMANDS] = commands

        CAT_ICONS[IconCategory.ALL] = icons
    }

    /**
     * Get icon from icon name.
     *
     * @param value name of icon
     * @return Icon coresponding to the name. Null if no match found
     */
    fun getIcon(value: String?): IIcon? {

        return CELL_ICON_MAP[value]
    }

    fun getApplicationComponent(service: Service): ApplicationComponent {
        return (service.application as HabitApplication).component()
    }

    fun getApplicationComponent(activity: Activity): ApplicationComponent {
        return (activity.application as HabitApplication).component()
    }

    fun getApplicationComponent(fragment: Fragment): ApplicationComponent {
        return (fragment.context!!.applicationContext as HabitApplication).component()
    }

    fun getApplicationComponent(context: Context): ApplicationComponent {
        return (context.applicationContext as HabitApplication).component()
    }

    /**
     * Create label text correctly formated to display values.
     *
     * @param context calling context.
     * @param name the label.
     * @return formated label
     */
    @JvmOverloads
    fun createLabel(context: Context, name: String, valueColor: String? = null): Spanned {
        val nameSpaned = name.replace("(\\[)(.*)(\\])".toRegex(), "<font color='" + String.format("#%06X", 0xFFFFFF and getValueColor(context, valueColor)) + "'>$2</font>")
        return Html.fromHtml(nameSpaned)
    }

    /**
     * Get value color
     * @param context
     * @param value
     * @return
     */
    private fun getColor(context: Context, value: String?, @ColorRes defaultColorRes: Int): Int {
        val colorRes = OpenHabUtil.openhabColors[value]
        if (colorRes != null) {
            return ContextCompat.getColor(context, colorRes)
        }

        try {
            return Color.parseColor(value)
        } catch (ignore: Exception) {
        }

        return ContextCompat.getColor(context, defaultColorRes)
    }

    /**
     * Get value color
     *
     * @param context
     * @param value
     * @return
     */
    fun getValueColor(context: Context, value: String?): Int {
        return getColor(context, value, R.color.colorAccent)
    }

    /**
     * Get label color
     * @param context
     * @param value
     * @return
     */
    fun getLabelColor(context: Context, value: String?): Int {
        return getColor(context, value, R.color.text_primary)
    }

    /**
     * Get bitmap for icon based on icon name
     *
     * @param context
     * @param value icon name
     * @return bitmap for icon. Null if no bitmap found
     */
    fun getIconBitmap(context: Context, value: String?): Bitmap? {
        val drawable = getIconDrawable(context, value) ?: return null

        return drawable.toBitmap()
    }

    /**
     * TODO move to dialog fragment
     * Create select icon dialog.
     *
     * @param context
     * @param listener triggers when icon is selected
     */
    fun crateIconSelected(context: Context, listener: IconAdapter.IconSelectListener) {
        val dialog = AppCompatDialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.icon_picker, null)
        val iconList = view.findViewById<RecyclerView>(R.id.list)
        iconList.itemAnimator = DefaultItemAnimator()
        iconList.layoutManager = GridLayoutManager(context, 4)
        val adapter = IconAdapter(context)

        adapter.setIconSelectListener(object : IconAdapter.IconSelectListener {
            override fun iconSelected(icon: IIcon) {
                listener.iconSelected(icon)
                dialog.dismiss()
            }
        })
        iconList.adapter = adapter

        dialog.setContentView(view)
        dialog.show()
    }

    /**
     * Get drawable for icon.
     *
     * @param context
     * @param value name of icon
     * @return drawable for icon. Null if not found
     */
    fun getIconDrawable(context: Context?, value: String?): IconicsDrawable? {
        val icon = Util.getIcon(value) ?: return null

        return IconicsDrawable(context, icon).color(Color.BLACK).sizeDp(24)
    }

    @JvmOverloads
    fun getBackground(context: Context, bitmap: Bitmap, type: Int = WidgetSettingsDB.NO_COLOR): Int {
        return when (type) {
            WidgetSettingsDB.MUTED_COLOR -> Palette.from(bitmap).generate().getMutedColor(ResourcesCompat.getColor(context.resources, R.color.image_background,context.theme))
            WidgetSettingsDB.LIGHT_MUTED_COLOR -> Palette.from(bitmap).generate().getLightMutedColor(ResourcesCompat.getColor(context.resources, R.color.image_background,context.theme))
            WidgetSettingsDB.DARK_MUTED_COLOR -> Palette.from(bitmap).generate().getDarkMutedColor(ResourcesCompat.getColor(context.resources, R.color.image_background,context.theme))
            WidgetSettingsDB.VIBRANT_COLOR -> Palette.from(bitmap).generate().getVibrantColor(ResourcesCompat.getColor(context.resources, R.color.image_background,context.theme))
            WidgetSettingsDB.LIGHT_VIBRANT_COLOR -> Palette.from(bitmap).generate().getLightVibrantColor(ResourcesCompat.getColor(context.resources, R.color.image_background,context.theme))
            WidgetSettingsDB.DARK_VIBRANT_COLOR -> Palette.from(bitmap).generate().getDarkVibrantColor(ResourcesCompat.getColor(context.resources, R.color.image_background,context.theme))
            else -> Color.TRANSPARENT
        }
    }

    fun toPercentage(percentage: Int): Float {
        return percentage.toFloat() / 100
    }

    fun generatePallete(color: Int): IntArray {
        return Colour.colorSchemeOfType(color, Colour.ColorScheme.ColorSchemeAnalagous)
    }


}
