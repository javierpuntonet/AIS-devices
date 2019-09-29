package pl.sviete.dom.devices.ui.areas

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import android.util.Log
import kotlinx.coroutines.*
import pl.sviete.dom.devices.db.*
import pl.sviete.dom.devices.mvp.BasePresenter

class AreaDetailsPresenter(val activity: FragmentActivity, override var view: AreaDetailsView.View)
    : BasePresenter<AreaDetailsView.View, AreaDetailsView.Presenter>(), AreaDetailsView.Presenter {

    private val TAG = AreaDetailsPresenter::class.java.simpleName
    private lateinit var mAreasViewModel: AreasViewModel
    private var mAreaId: Long? = null

    override fun loadView(area: AreaViewModel?) {
        mAreaId = area?.id
        mAreasViewModel = ViewModelProviders.of(activity).get(AreasViewModel::class.java)
    }

    override fun delete() {
        mAreasViewModel.delete(AreaEntity(mAreaId!!, ""))
        activity.finish()
    }

    override fun save(name: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (mAreaId != null) {
                    mAreasViewModel.update(AreaEntity(mAreaId, name))
                } else {
                    mAreasViewModel.insert(AreaEntity(null, name))
                }
                activity.finish()
            }catch (ex: Exception){
                Log.e(TAG, "save: $ex")
            }
        }
    }
}