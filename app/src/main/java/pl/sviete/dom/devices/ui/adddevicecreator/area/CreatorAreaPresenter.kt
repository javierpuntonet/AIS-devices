package pl.sviete.dom.devices.ui.adddevicecreator.area

import android.support.v4.app.Fragment
import pl.sviete.dom.devices.mvp.BasePresenter

class CreatorAreaPresenter(private val fragment: Fragment, override var view: CreatorAreaView.View)
    : BasePresenter<CreatorAreaView.View, CreatorAreaView.Presenter>(), CreatorAreaView.Presenter {
}