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
import pl.sviete.dom.devices.net.models.AccessPointInfo
import pl.sviete.dom.devices.ui.adddevicecreator.MainCreatorView
import java.util.*

class AplistCreatorFragment : Fragment(), WiFiScanner.OnScanResultsListener {

    private var mApSelectedListener: OnAPSelectedListener? = null
    private var mProgressBarManager: MainCreatorView.ProgressBarManager? = null
    private var mWifi: WiFiScanner? = null
    private var mAisAdapter: APAdapter? = null
    private val mAisList = ArrayList<AccessPointInfo>()
    private var mAPList: List<AccessPointInfo> = mutableListOf()

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

        mWifi = WiFiScanner(context!!)

        rv_ap_list.layoutManager = LinearLayoutManager(context)
        mAisAdapter = APAdapter(
            mAisList,
            context!!,
            object : APAdapter.OnItemClickListener {
                override fun onItemClick(item: AccessPointInfo) {
                    mWifi?.stopScan()
                    mApSelectedListener?.onAPSelected(item, mAPList)
                }
            })
        rv_ap_list.adapter = mAisAdapter

        ap_swipe.setOnRefreshListener {
            refreshAPList()
            ap_swipe.isRefreshing = false
        }
    }

    override fun onStart() {
        super.onStart()
        refreshAPList()
    }

    override fun onStop() {
        super.onStop()
        mWifi!!.stopScan()
        mProgressBarManager!!.hideProgressBar()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnAPSelectedListener)
            mApSelectedListener = context
        if (context is MainCreatorView.ProgressBarManager)
            mProgressBarManager = context
    }

    override fun onScanResults(scanResult: List<AccessPointInfo>) {
        try {
            setData(scanResult)
            mAPList = scanResult
        }
        catch (ex: Exception){
            Log.e("AplistCreatorFragment","onScanResults", ex)
        }
        finally {
            mProgressBarManager!!.hideProgressBar()
        }
    }

    private fun setData(list: List<AccessPointInfo>){
        mAisList.clear()
        mAisList.addAll(list)
        mAisList.sort()
        mAisAdapter!!.notifyDataSetChanged()
    }

    interface OnAPSelectedListener{
        fun onAPSelected(selectedAP: AccessPointInfo, accessibleAP: List<AccessPointInfo>)
    }

    private fun refreshAPList() {
        mProgressBarManager!!.showProgressBar()
        mWifi!!.startScan(this)
    }
}
