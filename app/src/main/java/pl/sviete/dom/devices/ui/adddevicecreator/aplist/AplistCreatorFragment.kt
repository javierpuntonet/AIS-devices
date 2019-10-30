package pl.sviete.dom.devices.ui.adddevicecreator.aplist

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.net.WiFiScanner
import kotlinx.android.synthetic.main.fragment_creator_aplist_.*
import pl.sviete.dom.devices.helpers.AisDeviceHelper
import pl.sviete.dom.devices.net.models.AccessPointInfo
import pl.sviete.dom.devices.ui.adddevicecreator.MainCreatorView
import java.util.*

class AplistCreatorFragment : Fragment(), ApListView.View {
    override val presenter: ApListView.Presenter = ApListPresenter(this, this)

    private val TAG = AplistCreatorFragment::class.java.simpleName

    private var mProgressBarManager: MainCreatorView.ProgressBarManager? = null
    private var mAisAdapter: APAdapter? = null
    private val mAisList = ArrayList<AccessPointViewModel>()

    companion object {
        fun newInstance() = AplistCreatorFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_creator_aplist_, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rv_ap_list.layoutManager = LinearLayoutManager(context)
        mAisAdapter = APAdapter(
            mAisList,
            context!!,
            object : APAdapter.OnItemClickListener {
                override fun onItemClick(item: AccessPointViewModel) {
                    presenter.onApSelected(item)
                }
            })
        rv_ap_list.adapter = mAisAdapter

        ap_swipe.setOnRefreshListener {
            refreshAPList()
            ap_swipe.isRefreshing = false
        }

        presenter.loadView()
    }

    override fun onStart() {
        super.onStart()
        refreshAPList()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
        mProgressBarManager!!.hideProgressBar()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        presenter.onAttach()
        if (context is MainCreatorView.ProgressBarManager)
            mProgressBarManager = context
    }

    override fun setData(list: List<AccessPointViewModel>){
        try {
            mAisList.clear()
            mAisList.addAll(list)
            mAisList.sort()
            mAisAdapter!!.notifyDataSetChanged()
        }
        finally {
            mProgressBarManager!!.hideProgressBar()
        }
    }

    private fun refreshAPList() {
        try {
            mProgressBarManager!!.showProgressBar()
            presenter.refreshApList()
        }
        catch (ex: Exception){
            Log.e(TAG, "refreshAPList: $ex")
            mProgressBarManager!!.hideProgressBar()
        }
    }
}
