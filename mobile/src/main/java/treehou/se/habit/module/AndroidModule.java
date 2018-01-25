package treehou.se.habit.module;


import android.content.Context;
import android.preference.PreferenceManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import treehou.se.habit.R;
import treehou.se.habit.connector.Analytics;
import treehou.se.habit.connector.Communicator;
import treehou.se.habit.core.db.OHRealm;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerHandler;
import treehou.se.habit.ui.control.ControllerUtil;
import treehou.se.habit.ui.control.cells.builders.ButtonCellBuilder;
import treehou.se.habit.ui.control.cells.builders.EmptyCellBuilder;
import treehou.se.habit.ui.control.cells.builders.IncDecCellBuilder;
import treehou.se.habit.ui.control.cells.builders.SliderCellBuilder;
import treehou.se.habit.ui.control.cells.builders.VoiceCellBuilder;
import treehou.se.habit.ui.control.cells.config.cells.ButtonConfigCellBuilder;
import treehou.se.habit.ui.control.cells.config.cells.DefaultConfigCellBuilder;
import treehou.se.habit.ui.control.cells.config.cells.IncDecConfigCellBuilder;
import treehou.se.habit.ui.control.cells.config.cells.SliderConfigCellBuilder;
import treehou.se.habit.ui.control.cells.config.cells.VoiceConfigCellBuilder;
import treehou.se.habit.ui.settings.subsettings.general.ThemeItem;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.SliderWidgetFactory;
import treehou.se.habit.ui.widgets.factories.colorpicker.ColorpickerWidgetFactory;
import treehou.se.habit.ui.widgets.factories.switches.SwitchWidgetFactory;
import treehou.se.habit.util.ConnectionFactory;
import treehou.se.habit.util.DatabaseServerLoaderFactory;
import treehou.se.habit.util.Settings;
import treehou.se.habit.util.logging.FirebaseLogger;
import treehou.se.habit.util.logging.Logger;

@Module
public class AndroidModule {
    protected final Context application;

    public AndroidModule(Context application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    public android.content.SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    public OHRealm provideOHRealm(Context context) {
        OHRealm ohRealm = new OHRealm(context);
        ohRealm.setup(context);
        return ohRealm;
    }

    @Provides
    public Realm provideRealm(OHRealm realm) {
        return realm.realm();
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return GsonHelper.createGsonBuilder();
    }

    @Provides
    @Singleton
    public Settings provideSettingsManager() {
        return Settings.instance(application);
    }

    @Provides
    @Singleton
    public ConnectionFactory provideConnectionFactory(Context context) {
        return new ConnectionFactory(context);
    }

    @Provides
    @Singleton
    public Communicator provideCommunicator(Context context) {
        return Communicator.instance(context);
    }

    @Provides
    public ServerLoaderFactory provideServerLoaderFactory(DatabaseServerLoaderFactory databaseServerLoaderFactory) {
        return databaseServerLoaderFactory;
    }

    @Provides
    public Logger provideLogger() {
        return new FirebaseLogger();
    }

    @Provides
    public WidgetFactory provideWidgetFactory(ConnectionFactory connectionFactory, SliderWidgetFactory sliderWidgetFactory,
                                              SwitchWidgetFactory switchWidgetFactory, ColorpickerWidgetFactory provideColorWidgetFactory) {

        WidgetFactory factory = new WidgetFactory(connectionFactory);
        factory.addWidgetFactory(OHWidget.WIDGET_TYPE_SLIDER, sliderWidgetFactory);
        factory.addWidgetFactory(OHWidget.WIDGET_TYPE_COLORPICKER, provideColorWidgetFactory);
        factory.addWidgetFactory(OHWidget.WIDGET_TYPE_SWITCH, switchWidgetFactory);

        return factory;
    }

    @Provides
    public SliderWidgetFactory provideSliderWidgetFactory(ConnectionFactory connectionFactory) {
        SliderWidgetFactory factory = new SliderWidgetFactory(connectionFactory);

        return factory;
    }

    @Provides
    public ColorpickerWidgetFactory provideColorWidgetFactory(ConnectionFactory connectionFactory) {
        ColorpickerWidgetFactory factory = new ColorpickerWidgetFactory(connectionFactory);

        return factory;
    }

    @Provides
    public SwitchWidgetFactory provideSwitchWidgetFactory(ConnectionFactory connectionFactory) {
        SwitchWidgetFactory factory = new SwitchWidgetFactory(connectionFactory);
        return factory;
    }

    @Provides
    @Singleton
    public ThemeItem[] provideThemes(Context context) {
        ThemeItem[] themes = new ThemeItem[]{
                new ThemeItem(Settings.Themes.THEME_DEFAULT, context.getString(R.string.treehouse)),
                new ThemeItem(Settings.Themes.THEME_HABDROID_LIGHT, context.getString(R.string.habdroid)),
                new ThemeItem(Settings.Themes.THEME_HABDROID_DARK, context.getString(R.string.dark))
        };
        return themes;
    }

    @Provides
    @Singleton
    public ControllerUtil provideControllerUtil(Context context, Realm realm, @Named("display") CellFactory factory) {
        return new ControllerUtil(context, realm, factory);
    }

    @Provides
    @Singleton
    public ControllerHandler provideControllHandler(Realm realm, ControllerUtil controllerUtil) {
        return new ControllerHandler(realm, controllerUtil);
    }

    @Provides
    @Singleton
    @Named("display")
    public CellFactory provideCellFactory(ButtonCellBuilder buttonCellBuilder, SliderCellBuilder sliderCellBuilder, IncDecCellBuilder incDecCellBuilder, VoiceCellBuilder voiceCellBuilder) {
        CellFactory cellFactory = new CellFactory();
        cellFactory.setDefaultBuilder(new EmptyCellBuilder());
        cellFactory.addBuilder(CellDB.Companion.getTYPE_BUTTON(), buttonCellBuilder);
        cellFactory.addBuilder(CellDB.Companion.getTYPE_INC_DEC(), incDecCellBuilder);
        cellFactory.addBuilder(CellDB.Companion.getTYPE_SLIDER(), sliderCellBuilder);
        cellFactory.addBuilder(CellDB.Companion.getTYPE_VOICE(), voiceCellBuilder);

        return cellFactory;
    }

    @Provides
    @Singleton
    @Named("config")
    public CellFactory provideConfigCellFactory() {
        CellFactory cellFactory = new CellFactory();
        cellFactory.setDefaultBuilder(new DefaultConfigCellBuilder());
        cellFactory.addBuilder(CellDB.Companion.getTYPE_BUTTON(), new ButtonConfigCellBuilder());
        cellFactory.addBuilder(CellDB.Companion.getTYPE_VOICE(), new VoiceConfigCellBuilder());
        cellFactory.addBuilder(CellDB.Companion.getTYPE_SLIDER(), new SliderConfigCellBuilder());
        cellFactory.addBuilder(CellDB.Companion.getTYPE_INC_DEC(), new IncDecConfigCellBuilder());
        return cellFactory;
    }


    @Provides
    @Singleton
    public VoiceCellBuilder provideVoiceCellBuilder() {
        return new VoiceCellBuilder();
    }

    @Provides
    @Singleton
    public ButtonCellBuilder provideButtonCellBuilder(ConnectionFactory connectionFactory) {
        return new ButtonCellBuilder(connectionFactory);
    }


    @Provides
    @Singleton
    public SliderCellBuilder provideSliderCellBuilder(ConnectionFactory connectionFactory) {
        return new SliderCellBuilder(connectionFactory);
    }

    @Provides
    @Singleton
    public IncDecCellBuilder provideIncDecCellBuilder(Communicator communicator) {
        return new IncDecCellBuilder(communicator);
    }

    @Provides
    @Singleton
    public FirebaseAnalytics provideFirebaseAnalytics(Context context) {
        return FirebaseAnalytics.getInstance(context);
    }

    @Provides
    @Singleton
    public Analytics provideAnalytics(FirebaseAnalytics firebaseAnalytics) {
        return new Analytics(firebaseAnalytics);
    }
}
