package pl.sviete.dom.devices.ui.areas

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import pl.sviete.dom.devices.db.AreasViewModel
import pl.sviete.dom.devices.mvp.BasePresenter

class AreasPresenter(val activity: FragmentActivity, override var view: AreasView.View)
    : BasePresenter<AreasView.View, AreasView.Presenter>(), AreasView.Presenter {

    private lateinit var mAreaViewModel: AreasViewModel
    private var mAreasVM = ArrayList<AreaViewModel>()

    override fun loadView() {
        mAreaViewModel = ViewModelProviders.of(activity).get(AreasViewModel::class.java)
        mAreaViewModel.getAll().observe(activity, Observer { areas ->
            mAreasVM.clear()
            areas?.forEach {
                mAreasVM.add(AreaViewModel(it.uid!!, it.name))
            }
            view.refreshData(mAreasVM)
        })
    }
}