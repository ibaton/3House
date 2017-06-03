package treehou.se.habit.module;


import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import se.treehou.ng.ohcommunicator.util.GsonHelper;
import se.treehou.ng.ohcommunicator.connector.models.OHWidget;
import treehou.se.habit.R;
import treehou.se.habit.core.db.model.OHRealm;
import treehou.se.habit.core.db.model.controller.CellDB;
import treehou.se.habit.ui.control.CellFactory;
import treehou.se.habit.ui.control.ControllerHandler;
import treehou.se.habit.ui.control.ControllerUtil;
import treehou.se.habit.ui.control.builders.ButtonCellBuilder;
import treehou.se.habit.ui.control.builders.EmptyCellBuilder;
import treehou.se.habit.ui.control.builders.IncDecCellBuilder;
import treehou.se.habit.ui.control.builders.SliderCellBuilder;
import treehou.se.habit.ui.control.builders.VoiceCellBuilder;
import treehou.se.habit.ui.settings.subsettings.general.ThemeItem;
import treehou.se.habit.ui.widgets.WidgetFactory;
import treehou.se.habit.ui.widgets.factories.colorpicker.ColorpickerWidgetFactory;
import treehou.se.habit.ui.widgets.factories.SliderWidgetFactory;
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

    @Provides @Singleton Context provideApplicationContext() {
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
    public Settings provideSettingsManager(){
        return Settings.instance(application);
    }

    @Provides
    @Singleton
    public ConnectionFactory provideConnectionFactory(Context context){
        return new ConnectionFactory(context);
    }

    @Provides
    public ServerLoaderFactory provideServerLoaderFactory(DatabaseServerLoaderFactory databaseServerLoaderFactory){
        return databaseServerLoaderFactory;
    }

    @Provides
    public Logger provideLogger(){
        return new FirebaseLogger();
    }

    @Provides
    public WidgetFactory provideWidgetFactory(SliderWidgetFactory sliderWidgetFactory,
                  SwitchWidgetFactory switchWidgetFactory, ColorpickerWidgetFactory provideColorWidgetFactory){

        WidgetFactory factory = new WidgetFactory();
        factory.addWidgetFactory(OHWidget.WIDGET_TYPE_SLIDER, sliderWidgetFactory);
        factory.addWidgetFactory(OHWidget.WIDGET_TYPE_COLORPICKER, provideColorWidgetFactory);
        factory.addWidgetFactory(OHWidget.WIDGET_TYPE_SWITCH, switchWidgetFactory);

        return factory;
    }

    @Provides
    public SliderWidgetFactory provideSliderWidgetFactory(ConnectionFactory connectionFactory){
        SliderWidgetFactory factory = new SliderWidgetFactory(connectionFactory);

        return factory;
    }

    @Provides
    public ColorpickerWidgetFactory provideColorWidgetFactory(ConnectionFactory connectionFactory){
        ColorpickerWidgetFactory factory = new ColorpickerWidgetFactory(connectionFactory);

        return factory;
    }

    @Provides
    public SwitchWidgetFactory provideSwitchWidgetFactory(ConnectionFactory connectionFactory){
        SwitchWidgetFactory factory = new SwitchWidgetFactory(connectionFactory);
        return factory;
    }

    @Provides
    @Singleton
    public ThemeItem[] provideThemes(Context context) {
        ThemeItem[] themes = new ThemeItem[] {
            new ThemeItem(Settings.Themes.THEME_DEFAULT, context.getString(R.string.treehouse)),
                    new ThemeItem(Settings.Themes.THEME_HABDROID_LIGHT, context.getString(R.string.habdroid)),
                    new ThemeItem(Settings.Themes.THEME_HABDROID_DARK, context.getString(R.string.dark))
        };
        return themes;
    }

    @Provides
    @Singleton
    public ControllerUtil provideControllerUtil(Context context, Realm realm, CellFactory<Integer> factory){
        return new ControllerUtil(context, realm, factory);
    }

    @Provides
    @Singleton
    public ControllerHandler provideControllHandler(Realm realm, ControllerUtil controllerUtil){
        return new ControllerHandler(realm, controllerUtil);
    }

    @Provides
    @Singleton
    public CellFactory<Integer> provideCellFactory(ButtonCellBuilder buttonCellBuilder){
        CellFactory<Integer> cellFactory = new CellFactory<>();
        cellFactory.setDefaultBuilder(new EmptyCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_BUTTON, buttonCellBuilder);
        cellFactory.addBuilder(CellDB.TYPE_INC_DEC, new IncDecCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_SLIDER, new SliderCellBuilder());
        cellFactory.addBuilder(CellDB.TYPE_VOICE, new VoiceCellBuilder());

        return cellFactory;
    }

    @Provides
    @Singleton
    public ButtonCellBuilder provideButtonCellBuilder(ConnectionFactory connectionFactory){
        return new ButtonCellBuilder(connectionFactory);
    }
}
