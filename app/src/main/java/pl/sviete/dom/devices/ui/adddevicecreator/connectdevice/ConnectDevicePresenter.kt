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
import android.util.Log
import java.lang.Exception

class ConnectDevicePresenter(private val fragment: Fragment, override var view: ConnectDeviceView.View)
    : BasePresenter<ConnectDeviceView.View, ConnectDeviceView.Presenter>(), ConnectDeviceView.Presenter
    , AisDeviceConfigurator.OnConfigurationProgressListener, IScannerResult{

    private val TAG = ConnectDevicePresenter::class.java.simpleName
    private var mListener: ConnectDeviceView.OnConnectDevice? = null
    private var mAisCtrl: AisDeviceConfigurator? = null
    private var mScanner: Scanner? = null
    private var mNewDeviceName: String? = null
    private var mNewDeviceMAC: String? = null
    private var mNewDeviceType: AisDeviceType? = null
    private var mScanFinished = false
    private var mCurrentStep = ConnectStep.SendConfiguration

    override fun onStop() {
        mAisCtrl?.cancelPair()
    }

    override fun pairDevice(deviceSsid: String, apName: String, apPassword: String, deviceName: String) {
        mNewDeviceMAC = null
        mNewDeviceType = null
        mScanner = Scanner(fragment.activity!!, this)
        mScanner!!.devices.clear()
        mScanner!!.devices.liveData.observe(fragment.activity!!, Observer {
            try {
                mScanner?.devices?.getFoundedDevices()?.forEach {
                    if (it.mac == mNewDeviceMAC) {
                        Log.d(TAG, "pairDevice found on IP: ${it.ip}")
                        mScanFinished = true
                        mScanner!!.devices?.liveData.removeObservers(fragment.activity!!)
                        mListener!!.onConnectDeviceSuccess(mNewDeviceType, mNewDeviceMAC!!)
                    }
                }
            }
            catch (ex: Exception){
                Log.e(TAG, "pairDevice", ex)
            }
        })

        mAisCtrl = AisDeviceConfigurator(fragment.context!!, this)
        mNewDeviceName = deviceName
        mAisCtrl!!.pairNewDevice(deviceSsid, apName, apPassword, deviceName)
    }

    override fun onConnectedToDevice() {
        mCurrentStep = ConnectStep.SendConfiguration
        view.onStep(ConnectStep.SendConfiguration)
    }

    override fun onAddDeviceFinished(result: AisDeviceConfigurator.AddDeviceArgs) {
        try {
            if (result.result) {
                mNewDeviceType = AisDeviceType.fromInt(result.deviceStatus!!.Status.Module)
                mNewDeviceMAC = result.deviceStatus.StatusNET.Mac

                mCurrentStep = ConnectStep.Waiting
                view.onStep(ConnectStep.Waiting)
                view.setIconForDevice(AisDeviceHelper.getResourceForType(mNewDeviceType))

                Log.d(TAG, "NetworkScan 1")
                //view.onStep(ConnectStep.NetworkScan1)
                Thread.sleep(2000)
                mScanner!!.runIpScanner()
            } else {
                view.onPairError(result.errorCode)
                mListener!!.onConnectDeviceFaild()
            }
        }catch (ex: Exception){
            Log.e(TAG, "onAddDeviceFinished", ex)
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
        try {
            if (mScanFinished) {
                Log.d(TAG, "ipScanFinished: Finish")
                return
            }
            if (mCurrentStep == ConnectStep.Waiting) {
                Thread.sleep(2000)
                mCurrentStep = ConnectStep.NetworkScan2
                Log.d(TAG, "ipScanFinished: NetworkScan 2")
                view.onStep(ConnectStep.NetworkScan2)
                mScanner!!.runIpScanner()
            } else if (mCurrentStep == ConnectStep.NetworkScan2) {
                Thread.sleep(3000)
                mCurrentStep = ConnectStep.NetworkScan3
                Log.d(TAG, "ipScanFinished: NetworkScan 3")
                view.onStep(ConnectStep.NetworkScan3)
                mScanner!!.runIpScanner()
            } else {
                Thread.sleep(4000)
                Log.d(TAG, "ipScanFinished: Last NetworkScan")
                view.onPairError(AisDeviceConfigurator.ErrorCode.TIMEOUT)
                mListener!!.onConnectDeviceFaild()
            }
        }catch (ex: Exception){
            Log.e(TAG, "ipScanFinished", ex)
        }
    }
}