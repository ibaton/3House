package treehou.se.habit.ui.servers.create.myopenhab


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import treehou.se.habit.R
import treehou.se.habit.module.HasActivitySubcomponentBuilders
import treehou.se.habit.mvp.BaseDaggerFragment
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class CreateMyOpenhabFragment : BaseDaggerFragment<CreateMyOpenhabContract.Presenter>(), CreateMyOpenhabContract.View {

    @Inject @JvmField var presenter: CreateMyOpenhabContract.Presenter? = null

    @BindView(R.id.email) @JvmField var email : TextView? = null
    @BindView(R.id.password) @JvmField var password : TextView? = null
    @BindView(R.id.error) @JvmField var errorView : TextView? = null

    var unbinder : Unbinder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_my_openhab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        unbinder = ButterKnife.bind(this, view)
    }

    @OnClick(R.id.login)
    fun login(){
        presenter?.login(email?.text.toString(), password?.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder?.unbind()
    }

    override fun showError(error: String) {
        Flowable.just(error)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({errorValue ->
                    errorView?.visibility = View.VISIBLE
                    errorView?.text = errorValue
                })
    }

    override fun closeWindow() {
        activity?.finish()
    }

    override fun getPresenter(): CreateMyOpenhabContract.Presenter? {
        return presenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(CreateMyOpenhabFragment::class.java) as CreateMyOpenhabComponent.Builder)
                .fragmentModule(CreateMyOpenhabModule(this))
                .build().injectMembers(this)
    }

}
