package treehou.se.habit.dagger


import android.content.Context
import android.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.realm.Realm
import se.treehou.ng.ohcommunicator.util.GsonHelper
import treehou.se.habit.R
import treehou.se.habit.connector.Analytics
import treehou.se.habit.connector.Communicator
import treehou.se.habit.core.db.OHRealm
import treehou.se.habit.core.db.model.controller.CellDB
import treehou.se.habit.ui.control.CellFactory
import treehou.se.habit.ui.control.ControllerHandler
import treehou.se.habit.ui.control.ControllerUtil
import treehou.se.habit.ui.control.cells.builders.*
import treehou.se.habit.ui.control.cells.config.cells.*
import treehou.se.habit.ui.settings.subsettings.general.ThemeItem
import treehou.se.habit.util.ConnectionFactory
import treehou.se.habit.util.DatabaseServerLoaderFactory
import treehou.se.habit.util.Settings
import treehou.se.habit.util.logging.FirebaseLogger
import treehou.se.habit.util.logging.Logger
import javax.inject.Named
import javax.inject.Singleton

@Module
open class AndroidModule(protected val application: Context) {

    @Provides
    @Singleton
    internal fun provideApplicationContext(): Context {
        return application
    }

    @Provides
    fun provideSharedPreferences(): android.content.SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Provides
    fun provideOHRealm(context: Context): OHRealm {
        val ohRealm = OHRealm(context)
        ohRealm.setup(context)
        return ohRealm
    }

    @Provides
    fun provideRealm(realm: OHRealm): Realm {
        return realm.realm()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonHelper.createGsonBuilder()
    }

    @Provides
    @Singleton
    fun provideSettingsManager(): Settings {
        return Settings.instance(application)
    }

    @Provides
    @Singleton
    fun provideConnectionFactory(context: Context): ConnectionFactory {
        return ConnectionFactory(context)
    }

    @Provides
    @Singleton
    fun provideCommunicator(context: Context): Communicator {
        return Communicator.instance(context)
    }

    @Provides
    fun provideServerLoaderFactory(databaseServerLoaderFactory: DatabaseServerLoaderFactory): ServerLoaderFactory {
        return databaseServerLoaderFactory
    }

    @Provides
    fun provideLogger(): Logger {
        return FirebaseLogger()
    }

    @Provides
    @Singleton
    fun provideThemes(context: Context): Array<ThemeItem> {
        return arrayOf(ThemeItem(Settings.THEME_DEFAULT, context.getString(R.string.treehouse)), ThemeItem(Settings.THEME_HABDROID_LIGHT, context.getString(R.string.habdroid)), ThemeItem(Settings.THEME_HABDROID_DARK, context.getString(R.string.dark)))
    }

    @Provides
    @Singleton
    fun provideControllerUtil(context: Context, realm: Realm, @Named("display") factory: CellFactory): ControllerUtil {
        return ControllerUtil(context, realm, factory)
    }

    @Provides
    @Singleton
    fun provideControllHandler(realm: Realm, controllerUtil: ControllerUtil): ControllerHandler {
        return ControllerHandler(realm, controllerUtil)
    }

    @Provides
    @Singleton
    @Named("display")
    fun provideCellFactory(buttonCellBuilder: ButtonCellBuilder, sliderCellBuilder: SliderCellBuilder, incDecCellBuilder: IncDecCellBuilder, voiceCellBuilder: VoiceCellBuilder): CellFactory {
        val cellFactory = CellFactory()
        cellFactory.setDefaultBuilder(EmptyCellBuilder())
        cellFactory.addBuilder(CellDB.TYPE_BUTTON, buttonCellBuilder)
        cellFactory.addBuilder(CellDB.TYPE_INC_DEC, incDecCellBuilder)
        cellFactory.addBuilder(CellDB.TYPE_SLIDER, sliderCellBuilder)
        cellFactory.addBuilder(CellDB.TYPE_VOICE, voiceCellBuilder)

        return cellFactory
    }

    @Provides
    @Singleton
    @Named("config")
    fun provideConfigCellFactory(): CellFactory {
        val cellFactory = CellFactory()
        cellFactory.setDefaultBuilder(DefaultConfigCellBuilder())
        cellFactory.addBuilder(CellDB.TYPE_BUTTON, ButtonConfigCellBuilder())
        cellFactory.addBuilder(CellDB.TYPE_VOICE, VoiceConfigCellBuilder())
        cellFactory.addBuilder(CellDB.TYPE_SLIDER, SliderConfigCellBuilder())
        cellFactory.addBuilder(CellDB.TYPE_INC_DEC, IncDecConfigCellBuilder())
        return cellFactory
    }


    @Provides
    @Singleton
    fun provideVoiceCellBuilder(): VoiceCellBuilder {
        return VoiceCellBuilder()
    }

    @Provides
    @Singleton
    fun provideButtonCellBuilder(connectionFactory: ConnectionFactory): ButtonCellBuilder {
        return ButtonCellBuilder(connectionFactory)
    }


    @Provides
    @Singleton
    fun provideSliderCellBuilder(connectionFactory: ConnectionFactory): SliderCellBuilder {
        return SliderCellBuilder(connectionFactory)
    }

    @Provides
    @Singleton
    fun provideIncDecCellBuilder(communicator: Communicator): IncDecCellBuilder {
        return IncDecCellBuilder(communicator)
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAnalytics(firebaseAnalytics: FirebaseAnalytics): Analytics {
        return Analytics(firebaseAnalytics)
    }
}
