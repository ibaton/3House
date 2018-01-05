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
import se.treehou.ng.ohcommunicator.util.GsonHelper
import se.treehou.ng.ohcommunicator.connector.models.OHBinding
import treehou.se.habit.R

class BindingFragment : Fragment() {

    @BindView(R.id.lbl_name) lateinit var lblName: TextView
    @BindView(R.id.lbl_author) lateinit var lblAuthor: TextView
    @BindView(R.id.lbl_description) lateinit var lblDescription: TextView

    private var binding: OHBinding? = null
    private var unbinder: Unbinder? = null

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
        unbinder = ButterKnife.bind(this, rootView)

        lblName.text = binding!!.name
        lblAuthor.text = binding!!.author
        lblDescription.text = binding!!.description

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
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
