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

class AplistCreatorFragment : Fragment(), WiFiScanner.OnScanResultsListener {

    private val TAG = AplistCreatorFragment::class.java.simpleName
    private var mApSelectedListener: OnAPSelectedListener? = null
    private var mProgressBarManager: MainCreatorView.ProgressBarManager? = null
    private var mWifi: WiFiScanner? = null
    private var mAisAdapter: APAdapter? = null
    private val mAisList = ArrayList<AccessPointViewModel>()
    private var mAPList: List<AccessPointViewModel> = mutableListOf()

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
                override fun onItemClick(item: AccessPointViewModel) {
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
            val apList = mutableListOf<AccessPointViewModel>()
            scanResult.forEach {
                apList.add(AccessPointViewModel(it.ssid, AisDeviceHelper.apIsAisDevice(it.isOpen, it.mac)))
            }
            setData(apList)
            mAPList = apList
        }
        catch (ex: Exception){
            Log.e(TAG,"onScanResults: $ex")
        }
        finally {
            mProgressBarManager!!.hideProgressBar()
        }
    }

    private fun setData(list: List<AccessPointViewModel>){
        mAisList.clear()
        mAisList.addAll(list)
        mAisList.sort()
        mAisAdapter!!.notifyDataSetChanged()
    }

    interface OnAPSelectedListener{
        fun onAPSelected(selectedAP: AccessPointViewModel, accessibleAP: List<AccessPointViewModel>)
    }

    private fun refreshAPList() {
        try {
            mProgressBarManager!!.showProgressBar()
            mWifi!!.startScan(this)
        }
        catch (ex: Exception){
            Log.e(TAG, "refreshAPList: $ex")
            mProgressBarManager!!.hideProgressBar()
        }
    }
}
