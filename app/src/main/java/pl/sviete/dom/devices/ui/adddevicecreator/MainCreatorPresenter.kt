package pl.sviete.dom.devices.ui.adddevicecreator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.db.AisDeviceViewModel
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.mvp.BasePresenter
import pl.sviete.dom.devices.ui.adddevicecreator.apdata.ApDataCreatorFragment
import pl.sviete.dom.devices.ui.adddevicecreator.aplist.AccessPointViewModel
import pl.sviete.dom.devices.ui.adddevicecreator.aplist.AplistCreatorFragment
import pl.sviete.dom.devices.ui.adddevicecreator.area.CreatorAreaFragment
import pl.sviete.dom.devices.ui.adddevicecreator.connectdevice.ConnectDeviceFragment
import pl.sviete.dom.devices.ui.adddevicecreator.namecreator.NameCreatorFragment
import pl.sviete.dom.devices.ui.adddevicecreator.startcreator.StartCreatorFragment
import java.lang.Exception

class MainCreatorPresenter(val activity: FragmentActivity, override var view: MainCreatorView.View)
    : BasePresenter<MainCreatorView.View, MainCreatorView.Presenter>()
    , MainCreatorView.Presenter {

    private var mDeviceToAdd: AccessPointViewModel? = null
    private var mNewDeviceName: String? = null
    private var mAPName: String? = null
    private var mAPPassword: String? = null
    private var mNewDeviceMAC: String? = null
    private var mNewDeviceType: AisDeviceType? = null
    private var mNewDeviceId: Long? = null

    override fun storeAP(selected: AccessPointViewModel){
        mDeviceToAdd = selected
    }

    override fun storeName(name: String) {
        mNewDeviceName = name
    }

    override fun storeAPData(apSsid: String, password: String){
        mAPName = apSsid
        mAPPassword = password
    }

    override fun saveNewDevice(deviceType: AisDeviceType?, deviceMAC: String){
        mNewDeviceType = deviceType
        mNewDeviceMAC = deviceMAC

        val newDevice = AisDeviceEntity(null, mNewDeviceName!!, mNewDeviceMAC!!, null, mNewDeviceType?.value)
        val devVM = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
        devVM.insertionId.observe(activity, Observer {
            mNewDeviceId = it
            view.changeFragment(POS_AREA, true)
        })
        devVM.insert(newDevice)
    }

    override fun getFragment(position: Int): Fragment? {
        when (position) {
            POS_START_CREATOR -> return StartCreatorFragment.newInstance()
            POS_AP_LIST -> return AplistCreatorFragment.newInstance()
            POS_NAME -> {
                val bundle = Bundle()
                bundle.putString("APname", mDeviceToAdd?.ssid)
                bundle.putString("defDeviceName", mNewDeviceName)
                val nameFragment = NameCreatorFragment.newInstance()
                nameFragment.arguments = bundle
                return nameFragment
            }
            POS_AP_DATA -> return ApDataCreatorFragment.newInstance(mDeviceToAdd!!.ssid)
            POS_CONNECT -> return ConnectDeviceFragment.newInstance(mNewDeviceName!!, mDeviceToAdd!!.ssid, mAPName!!, mAPPassword!!)
            POS_AREA -> return CreatorAreaFragment.newInstance(mNewDeviceId!!, mNewDeviceName!!, mNewDeviceType)
        }
        throw Exception("Not implemented")
    }
}