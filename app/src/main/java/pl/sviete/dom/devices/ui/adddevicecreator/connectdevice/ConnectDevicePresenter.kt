package pl.sviete.dom.devices.ui.adddevicecreator.connectdevice

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v4.app.Fragment
import pl.sviete.dom.devices.aiscontrollers.AisDeviceConfigurator
import pl.sviete.dom.devices.helpers.AisDeviceHelper
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.mvp.BasePresenter
import pl.sviete.dom.devices.netscanner.IScannerResult
import pl.sviete.dom.devices.netscanner.Scanner
import pl.sviete.dom.devices.ui.adddevicecreator.*

class ConnectDevicePresenter(private val fragment: Fragment, override var view: ConnectDeviceView.View)
    : BasePresenter<ConnectDeviceView.View, ConnectDeviceView.Presenter>(), ConnectDeviceView.Presenter
    , AisDeviceConfigurator.OnConfigurationProgressListener, IScannerResult{

    private var mAisCtrl: AisDeviceConfigurator? = null
    private var mScanner: Scanner? = null
    private var mNewDeviceName: String? = null
    private var mNewDeviceMAC: String? = null
    private var mNewDeviceType: AisDeviceType? = null

    override fun onStop() {
        mAisCtrl?.cancelPair()
        mScanner?.stop()
    }

    override fun pairDevice(deviceSsid: String, apName: String, apPassword: String, deviceName: String) {
        mNewDeviceMAC = null
        mNewDeviceType = null
        mScanner = Scanner(fragment.activity!!, this)
        mScanner!!.repository.devices.observe(fragment.activity!!, Observer {
            mScanner?.repository?.getFoundedDevices()?.forEach {
                if (it.mac == mNewDeviceMAC){
                    finishCreator()
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

            //mScanner!!.scan()
            //mScanner!!.runIpScanner()
            finishCreator()
        }
        else{
            view.onPairError(result.errorCode)
            fragment.fragmentManager?.popBackStack()
        }
    }

    override fun ipScanFinished() {
        fragment.fragmentManager?.popBackStack()
    }

    private fun finishCreator(){
        val activity = fragment.activity!!
        val intentResult = Intent()
        intentResult.putExtra(MainCreatorActivity.RESULT_NAME, mNewDeviceName)
        intentResult.putExtra(MainCreatorActivity.RESULT_MAC, mNewDeviceMAC)
        intentResult.putExtra(MainCreatorActivity.RESULT_TYPE, mNewDeviceType)
        activity.setResult(MainCreatorActivity.CREATOR_REQUEST_CODE, intentResult)

        activity.runOnUiThread {
            fragment.activity?.finish()
        }
    }
}