package pl.sviete.dom.devices.ui.adddevicecreator.area

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import pl.sviete.dom.devices.db.*
import pl.sviete.dom.devices.mvp.BasePresenter
import pl.sviete.dom.devices.ui.areas.AreaViewModel

class CreatorAreaPresenter(private val fragment: Fragment, override var view: CreatorAreaView.View)
    : BasePresenter<CreatorAreaView.View, CreatorAreaView.Presenter>(), CreatorAreaView.Presenter {

    private var mListener: CreatorAreaView.OnFinishCreatorArea? = null
    private lateinit var mAreasViewModel: AreasViewModel
    private var mSelectedArea = 0L
    private var mDeviceId : Long? = null

    override fun storeInitData(deviceId: Long) {
        mDeviceId = deviceId
    }

    override fun loadView() {
        mAreasViewModel = ViewModelProviders.of(fragment).get(AreasViewModel::class.java)
        mAreasViewModel.getAll().observe(fragment, Observer {
            val areas = mutableListOf<AreaViewModel>()
            areas.add(AreaViewModel(-1, ""))
            it?.forEach { a ->
                areas.add(AreaViewModel(a.uid!!, a.name))
            }
            view.refreshAreas(areas, mSelectedArea)
        })
        mAreasViewModel.insertionId.observe(fragment, Observer {
            mSelectedArea = it ?: 0
            view.selectArea(mSelectedArea)
        })
    }

    override fun addArea(areaName: String) {
        mAreasViewModel.insert(AreaEntity(null, areaName))
    }

    override fun finishClick(area: AreaViewModel?){
        if (area != null) {
            val devVM = ViewModelProviders.of(fragment).get(AisDeviceViewModel::class.java)
            devVM.updateArea(mDeviceId!!, area.id)
        }
        mListener?.onAreaFinish()
    }

    override fun attach(listener: CreatorAreaView.OnFinishCreatorArea){
        mListener = listener
    }

    override fun detach() {
        mListener = null
    }
}