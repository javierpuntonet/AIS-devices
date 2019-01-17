package pl.sviete.dom.devices.ui.devicedetails

import android.arch.lifecycle.ViewModelProviders
import pl.sviete.dom.devices.db.AisDeviceViewModel
import pl.sviete.dom.devices.mvp.BasePresenter

class DeviceDetailsPresenter(override var view: DeviceDetailsView.View): BasePresenter<DeviceDetailsView.View, DeviceDetailsView.Presenter>(), DeviceDetailsView.Presenter {

   // private lateinit var mAisDeviceViewModel: AisDeviceViewModel

    override fun loadView() {
        //mAisDeviceViewModel = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
        //mAisDeviceViewModel.

    }
}