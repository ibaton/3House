package treehou.se.habit.ui.servers.create.myopenhab


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_create_my_openhab.*
import treehou.se.habit.R
import treehou.se.habit.dagger.HasActivitySubcomponentBuilders
import treehou.se.habit.dagger.fragment.CreateMyOpenhabComponent
import treehou.se.habit.dagger.fragment.CreateMyOpenhabModule
import treehou.se.habit.mvp.BaseDaggerFragment
import treehou.se.habit.ui.servers.create.CreateServerActivity
import javax.inject.Inject


class CreateMyOpenhabFragment : BaseDaggerFragment<CreateMyOpenhabContract.Presenter>(), CreateMyOpenhabContract.View {

    @Inject
    lateinit var myPresenter: CreateMyOpenhabContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_create_my_openhab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginView.setOnClickListener { login() }
    }

    fun login() {
        myPresenter.login(openhabServerNameText.text.toString(), emailView.text.toString(), passwordView.text.toString())
    }

    override fun showError(error: String) {
        Flowable.just(error)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { errorValue ->
                    val errorView: TextView? = errorView
                    if(errorView != null) {
                        errorView.visibility = View.VISIBLE
                        errorView.text = errorValue
                    }
                }
    }

    override fun loadServerName(name: String) {
        openhabServerNameText.setText(name)
    }

    override fun loadUsername(name: String) {
        emailView.setText(name)
    }

    override fun loadPassword(password: String) {
        passwordView.setText(password)
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
