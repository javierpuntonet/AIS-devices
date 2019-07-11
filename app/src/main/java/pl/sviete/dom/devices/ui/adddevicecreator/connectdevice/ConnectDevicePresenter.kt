package pl.sviete.dom.devices.ui.adddevicecreator.connectdevice

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import pl.sviete.dom.devices.aiscontrollers.AisDeviceConfigurator
import pl.sviete.dom.devices.helpers.AisDeviceHelper
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.mvp.BasePresenter
import pl.sviete.dom.devices.netscanner.IScannerResult
import pl.sviete.dom.devices.netscanner.Scanner
import java.util.*
import kotlin.concurrent.schedule

class ConnectDevicePresenter(private val fragment: Fragment, override var view: ConnectDeviceView.View)
    : BasePresenter<ConnectDeviceView.View, ConnectDeviceView.Presenter>(), ConnectDeviceView.Presenter
    , AisDeviceConfigurator.OnConfigurationProgressListener, IScannerResult{

    private var mListener: ConnectDeviceView.OnConnectDevice? = null
    private var mAisCtrl: AisDeviceConfigurator? = null
    private var mScanner: Scanner? = null
    private var mNewDeviceName: String? = null
    private var mNewDeviceMAC: String? = null
    private var mNewDeviceType: AisDeviceType? = null
    private val mTimer = Timer()

    override fun onStop() {
        mAisCtrl?.cancelPair()
        mScanner?.stopBonjourScanner()
    }

    override fun pairDevice(deviceSsid: String, apName: String, apPassword: String, deviceName: String) {
        mNewDeviceMAC = null
        mNewDeviceType = null
        mScanner = Scanner(fragment.activity!!, this)
        mScanner!!.repository.clear()
        mScanner!!.repository.devices.observe(fragment.activity!!, Observer {
            mScanner?.repository?.getFoundedDevices()?.forEach {
                if (it.mac == mNewDeviceMAC){
                    mTimer.cancel()
                    mScanner?.stopBonjourScanner()
                    mScanner!!.repository.devices.removeObservers(fragment.activity!!)
                    mListener!!.onConnectDeviceSuccess(mNewDeviceType!!, mNewDeviceMAC!!)
                }
            }
        })

        mAisCtrl = AisDeviceConfigurator(fragment.context!!, this)
        mNewDeviceName = deviceName
        mAisCtrl!!.pairNewDevice(deviceSsid, apName, apPassword, deviceName)
    }

    override fun onConnectedToDevice() {
        view.onStep(ConnectStep.SendConfiguration)
    }

    override fun onAddDeviceFinished(result: AisDeviceConfigurator.AddDeviceArgs) {
        if (result.result) {
            mNewDeviceType = AisDeviceType.fromInt(result.deviceStatus!!.Status.Module)
            mNewDeviceMAC = result.deviceStatus.StatusNET.Mac

            view.onStep(ConnectStep.Waiting)
            view.setIconForDevice(AisDeviceHelper.getResourceForType(mNewDeviceType))

            mTimer.schedule(60000){
                view.onStep(ConnectStep.NetworkScan)
                mScanner!!.runIpScanner()

                mTimer.schedule(15000){
                    view.onPairError(AisDeviceConfigurator.ErrorCode.TIMEOUT)
                    mListener!!.onConnectDeviceFaild()
                }
            }

            mScanner!!.runBonjourScanner()
        }
        else{
            //test
            //mListener!!.onConnectDeviceSuccess(AisDeviceType.Socket, "AA:BB:CC:DD:EE")
            view.onPairError(result.errorCode)
            mListener!!.onConnectDeviceFaild()
        }
    }

    override fun attach(listener: ConnectDeviceView.OnConnectDevice) {
        mListener = listener
    }

    override fun detach() {
        mListener = null
    }

    override fun ipScanFinished() {

    }


}