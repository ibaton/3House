package treehou.se.habit.ui.colorpicker

import android.os.Bundle
import dagger.Module
import dagger.Provides
import treehou.se.habit.module.ViewModule
import javax.inject.Named

@Module
class LightModule(fragment: LightFragment, protected val args: Bundle) : ViewModule<LightFragment>(fragment) {

    @Provides
    fun provideView(): LightContract.View? {
        return view
    }

    @Provides
    fun providePresenter(presenter: LightPresenter): LightContract.Presenter {
        return presenter
    }

    @Provides
    @Named("arguments")
    fun provideArgs(): Bundle {
        return args
    }
}
