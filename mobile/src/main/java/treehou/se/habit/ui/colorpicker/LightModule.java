package treehou.se.habit.ui.colorpicker;

import android.os.Bundle;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import treehou.se.habit.module.ViewModule;

@Module
public class LightModule extends ViewModule<LightFragment> {

    protected final Bundle args;

    public LightModule(LightFragment fragment, Bundle args) {
        super(fragment);
        this.args = args;
    }


    @Provides
    public LightContract.View provideView() {
        return getView();
    }

    @Provides
    public LightContract.Presenter providePresenter(LightPresenter presenter) {
        return presenter;
    }

    @Provides
    @Named("arguments")
    public Bundle provideArgs() {
        return args;
    }
}
