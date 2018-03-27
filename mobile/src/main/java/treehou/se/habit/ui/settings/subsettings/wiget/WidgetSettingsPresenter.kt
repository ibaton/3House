package treehou.se.habit.ui.settings.subsettings.wiget

import javax.inject.Inject

import io.realm.Realm
import treehou.se.habit.core.db.settings.WidgetSettingsDB
import treehou.se.habit.dagger.RxPresenter

class WidgetSettingsPresenter @Inject
constructor(private val view: WidgetSettingsContract.View, private val realm: Realm) : RxPresenter(), WidgetSettingsContract.Presenter {

    override fun setWidgetBackground(backgroundType: Int) {
        val settings = WidgetSettingsDB.loadGlobal(realm)
        realm.beginTransaction()
        settings.imageBackground = backgroundType
        realm.commitTransaction()
        view.setWidgetBackground(backgroundType)
    }

    override fun setWidgetTextSize(size: Int) {
        val settings = WidgetSettingsDB.loadGlobal(realm)
        realm.beginTransaction()
        settings.textSize = size
        realm.commitTransaction()
        view.setWidgetTextSize(size)
    }

    override fun setWidgetImageSize(size: Int) {
        val settings = WidgetSettingsDB.loadGlobal(realm)
        realm.beginTransaction()
        settings.iconSize = size
        realm.commitTransaction()
        view.setWidgetImageSize(size)
    }

    override fun setCompressedWidgetSlider(isChecked: Boolean) {
        val settings = WidgetSettingsDB.loadGlobal(realm)
        realm.beginTransaction()
        settings.isCompressedSlider = isChecked
        realm.commitTransaction()
        view.setCompressedWidgetSlider(isChecked)
    }

    override fun setCompressedWidgetButton(isChecked: Boolean) {
        val settings = WidgetSettingsDB.loadGlobal(realm)
        realm.beginTransaction()
        settings.isCompressedSingleButton = isChecked
        realm.commitTransaction()
        view.setCompressedWidgetButton(isChecked)
    }
}
