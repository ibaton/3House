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
import treehou.se.habit.ui.servers.create.CreateServerActivity
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class CreateMyOpenhabFragment : BaseDaggerFragment<CreateMyOpenhabContract.Presenter>(), CreateMyOpenhabContract.View {

    @Inject lateinit var myPresenter: CreateMyOpenhabContract.Presenter

    @BindView(R.id.email) lateinit var emailView: TextView
    @BindView(R.id.password) lateinit var passwordView: TextView
    @BindView(R.id.error) lateinit var errorView: TextView

    var unbinder: Unbinder? = null


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
    fun login() {
        myPresenter.login(emailView.text.toString(), passwordView.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder?.unbind()
    }

    override fun showError(error: String) {
        Flowable.just(error)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ errorValue ->
                    errorView.visibility = View.VISIBLE
                    errorView.text = errorValue
                })
    }

    override fun loadUsername(name: String) {
        emailView.text = name
    }

    override fun loadPassword(password: String) {
        passwordView.text = password
    }

    /**
     * Close this window
     */
    override fun closeWindow() {
        val currentActivity = activity
        if (currentActivity is CreateServerActivity) {
            activity?.finish()
        } else {
            fragmentManager?.popBackStack()
        }
    }

    override fun getPresenter(): CreateMyOpenhabContract.Presenter? {
        return myPresenter
    }

    override fun injectMembers(hasActivitySubcomponentBuilders: HasActivitySubcomponentBuilders) {
        (hasActivitySubcomponentBuilders.getFragmentComponentBuilder(CreateMyOpenhabFragment::class.java) as CreateMyOpenhabComponent.Builder)
                .fragmentModule(CreateMyOpenhabModule(this))
                .build().injectMembers(this)
    }

    companion object {

        fun newInstance(): CreateMyOpenhabFragment {
            val fragment = CreateMyOpenhabFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(serverId: Long): CreateMyOpenhabFragment {
            val fragment = CreateMyOpenhabFragment()
            val args = Bundle()
            args.putLong(CreateMyOpenhabContract.ARG_SERVER, serverId)
            fragment.arguments = args
            return fragment
        }
    }
}
