package pl.sviete.dom.devices.ui.devicedetails

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.aiscontrollers.AisDeviceRestController
import pl.sviete.dom.devices.db.*
import pl.sviete.dom.devices.mvp.BasePresenter
import pl.sviete.dom.devices.netscanner.FoundDeviceRepository
import pl.sviete.dom.devices.ui.areas.AreaViewModel

class DeviceDetailsPresenter(val activity: FragmentActivity, override var view: DeviceDetailsView.View)
    : BasePresenter<DeviceDetailsView.View, DeviceDetailsView.Presenter>(), DeviceDetailsView.Presenter {

    private lateinit var mAisDeviceViewModel: AisDeviceViewModel
    private lateinit var mModel: AisDeviceEntity

    override fun loadView(id: Long) {
        mAisDeviceViewModel = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
        mAisDeviceViewModel.getById(id).observe(activity, Observer { device ->
            if (device != null) {
                mModel = device
                view.showView(mModel)
            }
        })
        val areaVM = ViewModelProviders.of(activity).get(AreasViewModel::class.java)
        areaVM.getAll().observe(activity, Observer { areas ->
            val areasModel = mutableListOf<AreaViewModel>()
            var selectedIdx = 0
            var i = 0
            areasModel.add(AreaViewModel(-1, ""))
            areas?.forEach {
                i += 1
                areasModel.add(AreaViewModel(it.uid!!, it.name))
                if (it.uid!! == mModel.areaId)
                    selectedIdx = i
            }
            view.setAreas(areasModel, selectedIdx)
        })
    }

    override fun saveView(name: String, ip: String, area: AreaViewModel?): Boolean{
        var save = false
        var saveName = false
        if (!validate(name, ip)) return false
        if (mModel.name != name) {
            mModel.name = name
            save = true
            saveName = true
        }
        if (mModel.ip != ip) {
            mModel.ip = ip
            save = true
        }
        if (area?.id != mModel.areaId) {
            mModel.areaId = area?.id
            save = true
        }
        if (save) {
            GlobalScope.launch(Dispatchers.Main) {
                if (saveName) {
                    save = AisDeviceRestController.setName(ip, name)
                }
                if (save) {
                    mAisDeviceViewModel.update(mModel)
                    activity.finish()
                }
                else {
                    view.showSaveErrorInfo()
                }
            }
            return true
        }
        return false
    }

    override fun delete() {
        mAisDeviceViewModel.delete(mModel)
        FoundDeviceRepository.getInstance().deleteDevice(mModel.mac)
        activity.finish()
    }

    private fun validate(name: String, ip: String) : Boolean {
        if (name.isBlank()) {
            view.showNameValidationError(R.string.empty_name)
            return false
        }
        if (ip.isBlank()) {
            view.showIPValidationError(R.string.empty_ip)
            return false
        }
        return true
    }
}