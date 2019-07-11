package pl.sviete.dom.devices.mvp

interface IPresenter<T> {

    fun stop()

    var view: T
}