package pl.sviete.dom.devices.ui.mainview

import android.arch.lifecycle.LiveData
import pl.sviete.dom.devices.db.AisDeviceEntity
import android.arch.lifecycle.MutableLiveData
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus


class DeviceViewModel(val uid: Int, val name: String, val ip: String?) {
    var status = PowerStatus.UNKNOWN
}