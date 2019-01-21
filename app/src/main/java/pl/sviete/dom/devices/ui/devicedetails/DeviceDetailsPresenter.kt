package pl.sviete.dom.devices.ui.devicedetails

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import pl.sviete.dom.devices.db.AisDeviceEntity
import pl.sviete.dom.devices.db.AisDeviceViewModel
import pl.sviete.dom.devices.mvp.BasePresenter

class DeviceDetailsPresenter(val activity: FragmentActivity, override var view: DeviceDetailsView.View): BasePresenter<DeviceDetailsView.View, DeviceDetailsView.Presenter>(), DeviceDetailsView.Presenter {

    private lateinit var mAisDeviceViewModel: AisDeviceViewModel
    private lateinit var mModel: AisDeviceEntity

    override fun loadView(id: Int) {
        mAisDeviceViewModel = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
        mAisDeviceViewModel.getById(id).observe(activity, Observer { device ->
            if (device != null) {
                mModel = device
                view.showView(mModel)
            }
        })
    }

    override fun saveView(name: String, ip: String){
        mModel.name = name
        mModel.ip = ip
        mAisDeviceViewModel.update(mModel)
    }

    override fun delete() {
        mAisDeviceViewModel.delete(mModel)
        activity.finish()
    }
}