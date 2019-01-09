package pl.sviete.dom.devices.mvp

interface BaseView<out T : IPresenter<*>> {

    val presenter: T

}