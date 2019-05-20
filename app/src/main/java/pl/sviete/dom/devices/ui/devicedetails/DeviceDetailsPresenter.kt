package pl.sviete.dom.devices.ui.devicedetails

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.aiscontrollers.AisDeviceController
import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.db.AisDeviceViewModel
import pl.sviete.dom.devices.mvp.BasePresenter

class DeviceDetailsPresenter(val activity: FragmentActivity, override var view: DeviceDetailsView.View): BasePresenter<DeviceDetailsView.View, DeviceDetailsView.Presenter>(), DeviceDetailsView.Presenter {

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
    }

    override fun saveView(name: String, ip: String){
        var save = false
        var saveName = false
        if (!validate(name, ip)) return
        if (mModel.name != name) {
            mModel.name = name
            save = true
            saveName = true
        }
        if (mModel.ip != ip) {
            mModel.ip = ip
            save = true
        }
        if (save) {
            GlobalScope.launch(Dispatchers.Main) {
                if (saveName)
                    save = AisDeviceController.setName(ip, name)
                if (save)
                    mAisDeviceViewModel.update(mModel)
            }
        }
    }

    override fun delete() {
        mAisDeviceViewModel.delete(mModel)
        activity.finish()
    }

    private fun validate(name: String, ip: String) : Boolean {
        if (name.isNullOrBlank()) {
            view.showNameValidationError(R.string.empty_name)
            return false
        }
        if (ip.isNullOrBlank()) {
            view.showIPValidationError(R.string.empty_ip)
            return false
        }
        return true
    }
}