package pl.sviete.dom.devices.ui.boxdetails

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import kotlinx.coroutines.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.db.*
import pl.sviete.dom.devices.mvp.BasePresenter
import pl.sviete.dom.devices.netscanner.FoundDeviceRepository

class BoxDetailsPresenter(val activity: FragmentActivity, override var view: BoxDetailsView.View)
    : BasePresenter<BoxDetailsView.View, BoxDetailsView.Presenter>(), BoxDetailsView.Presenter {

    private lateinit var mAisDeviceViewModel: AisDeviceViewModel
    private var mModel: AisDeviceEntity? = null

    override fun loadView(id: Long) {
        mAisDeviceViewModel = ViewModelProviders.of(activity).get(AisDeviceViewModel::class.java)
        mAisDeviceViewModel.getById(id).observe(activity, Observer { device ->
            if (device != null) {
                mModel = device
                loadView()
            }
        })
    }

    override fun saveView(name: String): Boolean{
        var save = false
        if (!validate(name)) return false
        mModel?.let {
            if (it.name != name) {
                it.name = name
                save = true
            }

            if (save) {
                GlobalScope.launch(Dispatchers.Main) {
                    if (save) {
                        mAisDeviceViewModel.update(it)
                        activity.finish()
                    }
                }
                return true
            } else {
                activity.finish()
            }
        }
        return false
    }

    override fun delete() {
        mAisDeviceViewModel.delete(mModel!!)
        FoundDeviceRepository.getInstance().deleteDevice(mModel!!.mac)
        activity.finish()
    }

    private fun loadView(){
        if (mModel != null){
            view.showView(mModel!!)
        }
    }

    private fun validate(name: String) : Boolean {
        if (name.isBlank()) {
            view.showNameValidationError(R.string.empty_name)
            return false
        }
        return true
    }
}