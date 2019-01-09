package pl.sviete.dom.devices.mvp

import android.support.annotation.CallSuper

abstract class BasePresenter<V : BaseView<P>, out P : IPresenter<V>> : IPresenter<V> {

    override lateinit var view: V

    @CallSuper
    override fun stop() {

    }
}