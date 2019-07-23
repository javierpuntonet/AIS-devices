package pl.sviete.dom.devices.aiscontrollers

import android.content.Context
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.models.Status
import pl.sviete.dom.devices.net.WiFiScanner
import java.io.UnsupportedEncodingException

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
                mHandlerTimeout.postDelayed(timeout, 5000)
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

    private val timeout = object : Runnable {
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
                e.printStackTrace()
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
                mHandlerTimeout.removeCallbacksAndMessages(timeout)

                listener.onConnectedToDevice()
                val connectStatus = connectAndConfigureDevice()
                if (connectStatus.first) {
                    result = true
                    deviceStatus = connectStatus.second
                }
            } catch (e: UnsupportedEncodingException) {
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
        if (networkId != mDeviceNetworkId) {
            //Log.d(TAG, "wrong connection, info.getNetworkId(): " + info.networkId)
        }
        else {
            try {
                val deviceStatus = AisDeviceRestController.getStatus(AisDeviceRestController.AP_IP)
                val result = AisDeviceRestController.setupNew(mFriendlyName!!, mAPName!!, mAPPassword!!)
                if (result){
                    return Pair(true, deviceStatus)
                }
            } catch (e: Exception) {
                Log.e(TAG, "ConnectAndConfiguraDevice", e)
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
