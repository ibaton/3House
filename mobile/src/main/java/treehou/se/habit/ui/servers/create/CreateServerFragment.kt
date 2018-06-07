package treehou.se.habit.ui.servers.create


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_server.*
import treehou.se.habit.R
import treehou.se.habit.ui.servers.create.custom.SetupServerFragment
import treehou.se.habit.ui.servers.create.myopenhab.CreateMyOpenhabFragment
import treehou.se.habit.ui.servers.create.scan.ScanServersFragment


class CreateServerFragment : Fragment() {

    var introFinished = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_create_server, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        introFinished = true

        addMyOpenhab.setOnClickListener { startCreateMyOpenhabServerFlow() }
        addNewServer.setOnClickListener { startCreateServerFlow() }
        scanForServers.setOnClickListener { startScanServerFlow() }
        addDemoServerButton.setOnClickListener { addDemoServer() }
    }

    fun startCreateMyOpenhabServerFlow() {
        val fragmentManager = fragmentManager
        if (fragmentManager != null) {
            val createMyOpenhabFragment = CreateMyOpenhabFragment()
            fragmentManager.beginTransaction()
                    .replace((view!!.parent as ViewGroup).id, createMyOpenhabFragment)
                    .addToBackStack(null)
                    .commit()
        }
    }

    fun startCreateServerFlow() {
        val fragmentManager = fragmentManager
        if (fragmentManager != null) {
            val createMyOpenhabFragment = SetupServerFragment()
            fragmentManager.beginTransaction()
                    .replace((view!!.parent as ViewGroup).id, createMyOpenhabFragment)
                    .addToBackStack(null)
                    .commit()
        }
    }

    fun startScanServerFlow() {
        val fragmentManager = fragmentManager
        if (fragmentManager != null) {
            val scanServersFragment = ScanServersFragment()
            fragmentManager.beginTransaction()
                    .replace((view!!.parent as ViewGroup).id, scanServersFragment)
                    .addToBackStack(null)
                    .commit()
        }
    }

    fun addDemoServer() {
        val activity = activity
        if (activity is CreateServerContract.View) {
            activity.getPresenter()?.saveDemoServer()
            activity.finish()
        }
    }

    /**
     * Close activity
     */
    fun close() {
        fragmentManager?.popBackStack();
    }

    companion object {

        fun createFragment(): Fragment {
            val createServerFragment = CreateServerFragment()
            return createServerFragment
        }
    }
}
