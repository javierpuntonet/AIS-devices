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
import android.support.v7.app.AppCompatActivity

class DeviceDetailsPresenter(val activity: AppCompatActivity, override var view: DeviceDetailsView.View)
    : BasePresenter<DeviceDetailsView.View, DeviceDetailsView.Presenter>(), DeviceDetailsView.Presenter {

    private lateinit var mAisDeviceViewModel: AisDeviceViewModel
    private var mModel: AisDeviceEntity? = null
    private var mAreas: MutableList<AreaViewModel>? = null
    private var mSelectedAreaIdx = 0

    override fun loadView(id: Long) {
        mAisDeviceViewModel = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
        mAisDeviceViewModel.getById(id).observe(activity, Observer { device ->
            if (device != null) {
                mModel = device
                loadView()
                mAisDeviceViewModel.getById(id).removeObservers(activity)
            }
        })
        val areaVM = ViewModelProviders.of(activity).get(AreasViewModel::class.java)
        areaVM.getAll().observe(activity, Observer { areas ->
            mAreas = mutableListOf()
            mAreas?.let {
                it.add(AreaViewModel(-1, ""))
                areas?.forEach { a ->
                    mAreas!!.add(AreaViewModel(a.uid!!, a.name))
                }
                loadView()
                areaVM.getAll().removeObservers(activity)
            }
        })
    }

    override fun saveView(name: String, ip: String, area: AreaViewModel?): Boolean{
        var save = false
        var saveName = false
        if (!validate(name, ip)) return false
        mModel?.let {
            if (it.name != name) {
                it.name = name
                save = true
                saveName = true
            }
            if (it.ip != ip) {
                it.ip = ip
                save = true
            }
            if (area?.id != it.areaId) {
                it.areaId = area?.id
                save = true
            }
            if (save) {
                GlobalScope.launch(Dispatchers.Main) {
                    if (saveName) {
                        save = AisDeviceRestController.setName(ip, name)
                    }
                    if (save) {
                        mAisDeviceViewModel.update(it)
                        activity.finish()
                    } else {
                        view.showSaveErrorInfo()
                    }
                }
                return true
            } else {
                activity.finish()
                return true
            }
        }
        return false
    }

    override fun delete() {
        mAisDeviceViewModel.delete(mModel!!)
        FoundDeviceRepository.getInstance().deleteDevice(mModel!!.mac)
        activity.finish()
    }

    override fun initPairWithBox() {
        mAisDeviceViewModel.getBoxes().observe(activity, Observer { boxes ->
            if (boxes != null && boxes.count() > 0) {
                view.selectBoxToPair(boxes.map { x -> BoxVM(x.name, x.mac) })
            }
            else{
                view.showNoBoxesMessage()
            }
        })
    }

    override fun pairWithBox(boxId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val success = AisDeviceRestController.pairWithBox(mModel!!.ip!!, boxId)
            view.showPairStatus(success)
        }
    }

    private fun loadView(){
        if (mModel != null && mAreas != null){
            view.showView(mModel!!)

            var i = 0
            mAreas?.forEach {
                if (it.id == mModel!!.areaId)
                    mSelectedAreaIdx = i
                i += 1
            }
            view.setAreas(mAreas!!, mSelectedAreaIdx)
        }
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