package treehou.se.habit.ui.bindings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import kotlinx.android.synthetic.main.fragment_binding.*
import se.treehou.ng.ohcommunicator.util.GsonHelper
import se.treehou.ng.ohcommunicator.connector.models.OHBinding
import treehou.se.habit.R

class BindingFragment : Fragment() {

    private var binding: OHBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        if (arguments != null) {
            if (arguments!!.containsKey(ARG_BINDING)) {
                binding = GsonHelper.createGsonBuilder().fromJson(arguments!!.getString(ARG_BINDING), OHBinding::class.java)
            }
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_binding, container, false)
        lblName.text = binding!!.name
        lblAuthor.text = binding!!.author
        lblDescription.text = binding!!.description

        return rootView
    }

    companion object {

        private val ARG_BINDING = "ARG_BINDING"

        fun newInstance(binding: OHBinding): BindingFragment {
            val fragment = BindingFragment()
            val args = Bundle()
            args.putString(ARG_BINDING, GsonHelper.createGsonBuilder().toJson(binding))
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
