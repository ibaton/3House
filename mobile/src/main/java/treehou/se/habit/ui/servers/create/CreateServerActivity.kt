package treehou.se.habit.ui.servers.create

import android.content.Context
import android.content.Intent
import android.os.Bundle
import treehou.se.habit.R
import treehou.se.habit.module.HasActivitySubcomponentBuilders
import treehou.se.habit.mvp.BaseDaggerActivity
import javax.inject.Inject
import android.view.View
import treehou.se.habit.ui.anim.AnimationFinishListener
import butterknife.*


class CreateServerActivity : BaseDaggerActivity<CreateServerContract.Presenter>(), CreateServerContract.View {

    @Inject
    @JvmField
    var serverPresenter: CreateServerPresenter? = null;

    @BindView(R.id.container) lateinit var container: View

    var unbinder : Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_server)
        unbinder = ButterKnife.bind(this)

        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if(fragment == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CreateServerFragment.createFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if(fragment is CreateServerFragment){
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder?.unbind()
    }

    override fun getPresenter(): CreateServerContract.Presenter? {
        return serverPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders?.getActivityComponentBuilder(CreateServerActivity::class.java) as CreateServerActivityComponent.Builder)
                .activityModule(CreateServerModule(this))
                .build().injectMembers(this)
    }

    companion object {

        fun createIntent(context: Context): Intent {
            val intent = Intent(context, CreateServerActivity::class.java)
            return intent
        }
    }
}
