package treehou.se.habit.dagger.fragment

import android.os.Bundle
import dagger.Module
import dagger.Provides
import treehou.se.habit.dagger.ViewModule
import treehou.se.habit.ui.colorpicker.LightContract
import treehou.se.habit.ui.colorpicker.LightFragment
import treehou.se.habit.ui.colorpicker.LightPresenter
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
