package pl.sviete.dom.devices.aiscontrollers

import android.content.Context
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.models.Status
import pl.sviete.dom.devices.net.WiFiScanner

class AisDeviceConfigurator(context: Context, val listener: OnConfigurationProgressListener): WiFiScanner.OnWiFiConnectedListener {
    private val TAG = AisDeviceConfigurator::class.java.simpleName

    private val mWiFiScanner: WiFiScanner = WiFiScanner(context)
    private var mFriendlyName: String? = null
    private var mDeviceSsid: String? = null
    private var mAPName: String? = null
    private var mAPPassword: String? = null
    private var mDeviceNetworkId: Int = -1
    private var mCurrentNetworkId: Int = -1
    private var mHandlerTimeout = Handler()
    private var mConnectingCanceled: Boolean = true

    fun cancelPair() {
        mWiFiScanner.unregisterOnConnected()
        reconnect()
    }

    fun pairNewDevice(ssid: String, apName: String, apPassword: String, friendlyName: String){
        mDeviceSsid = ssid
        mAPName = apName
        mAPPassword = apPassword
        mFriendlyName = friendlyName

        // save the current connection - to reconnect after the device will be added
        mCurrentNetworkId = mWiFiScanner.getCurrentNetworkId()

        // check if the connection exists and remove it
        mWiFiScanner.removeSsid(ssid)

        // disable all other wifi network for the time of connection
        mWiFiScanner.disableAllNetworks()

        mWiFiScanner.disconnect()

        mConnectingCanceled = false
        // create new connection
        mDeviceNetworkId = mWiFiScanner.addNewNetwork(ssid)
        if (mDeviceNetworkId != -1) {
            mWiFiScanner.registerOnConncted(this, ssid)

            mWiFiScanner.connectToNetwork(mDeviceNetworkId)
            if (!mConnectingCanceled)
                mHandlerTimeout.postDelayed(doTimeout, 5000)
        }
        else {
            reconnect()
            listener.onAddDeviceFinished(
                AddDeviceArgs(
                    false
                )
            )
        }
    }

    private val doTimeout = object : Runnable {
        override fun run() {
            try {
                if (!mConnectingCanceled) {
                    mWiFiScanner.unregisterOnConnected()
                    listener.onAddDeviceFinished(
                        AddDeviceArgs(
                            false,
                            ErrorCode.TIMEOUT
                        )
                    )
                    reconnect()
                }
            } catch (e: Exception) {
                Log.e(TAG, "doTimeout", e)
                reconnect()
            }
        }
    }

    override fun onConnected() {
        GlobalScope.launch(Dispatchers.IO) {
            var result = false
            var deviceStatus: Status? = null
            try {
                mConnectingCanceled = true
                mHandlerTimeout.removeCallbacksAndMessages(doTimeout)

                listener.onConnectedToDevice()
                val connectStatus = connectAndConfigureDevice()
                if (connectStatus.first) {
                    result = true
                    deviceStatus = connectStatus.second
                }
            } catch (e: Exception) {
                Log.e(TAG, "onConnected", e)
            } finally {
                reconnect()
                listener.onAddDeviceFinished(
                    AddDeviceArgs(
                        result,
                        ErrorCode.OK,
                        deviceStatus
                    )
                )
            }
        }
    }

    private suspend fun connectAndConfigureDevice(): Pair<Boolean, Status?> {
        // check if we have correct connection if not then exit
        val networkId = mWiFiScanner.getCurrentNetworkId()
        var bindedToWiFiNet = false;
        if (networkId != mDeviceNetworkId) {
            Log.e(TAG, "Connected to wrong network")
        }
        else {
            try {
                bindedToWiFiNet = mWiFiScanner.bindToWifiNetwork()//Issue#2
                Log.d(TAG, "bindedToNet: ${bindedToWiFiNet.toString()}")
                var deviceStatus = AisDeviceRestController.getStatus(AisDeviceRestController.AP_IP)
                //try again
                if (deviceStatus == null) {
                    if (!bindedToWiFiNet) {
                        mWiFiScanner.bindToWifiNetwork()//Issue#2
                        Log.d(TAG, "bindedToNet: ${bindedToWiFiNet.toString()}")
                    }
                    deviceStatus = AisDeviceRestController.getStatus(AisDeviceRestController.AP_IP)
                }
                //cant get device status then return false
                if (deviceStatus?.Status == null) {
                    Log.e(TAG, "Connected device doesn't return status")
                    return Pair(false, null)
                }
                if (!bindedToWiFiNet) {
                    bindedToWiFiNet = mWiFiScanner.bindToWifiNetwork()//Issue#2
                    Log.d(TAG, "bindToWifiNetwork: ${bindedToWiFiNet.toString()}")
                }
                val result = AisDeviceRestController.setupNew(mFriendlyName!!, mAPName!!, mAPPassword!!)
                if (result){
                    return Pair(true, deviceStatus)
                }
                else{
                    Log.e(TAG, "Connected device return false on setup")
                }
            } catch (e: Exception) {
                Log.e(TAG, "ConnectAndConfiguraDevice", e)
            }
            finally {
                bindedToWiFiNet = mWiFiScanner.unBindFromNetwork()
                Log.d(TAG, "unBindFromNetwork: ${bindedToWiFiNet.toString()}")
            }
        }
        return Pair(false, null)
    }

    private fun reconnect(){
        mWiFiScanner.removeNetwork(mDeviceNetworkId)
        mWiFiScanner.enableAllNetworks()
        mWiFiScanner.connectToNetwork(mCurrentNetworkId)
    }

    interface  OnConfigurationProgressListener {
        fun onAddDeviceFinished(result: AddDeviceArgs)
        fun onConnectedToDevice()
    }

    data class AddDeviceArgs(
        val result: Boolean = false,
        val errorCode: ErrorCode = ErrorCode.OK,
        val deviceStatus: Status? = null
    )

    enum class ErrorCode{
        OK,
        TIMEOUT,
    }
}
