package pl.sviete.dom.devices.net

import android.content.Context
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.AisDeviceController
import pl.sviete.dom.devices.models.AisDeviceType
import java.io.UnsupportedEncodingException


class AisDeviceConfigurator(context: Context): WiFiScanner.OnWiFiConnectedListener {
    private val TAG = AisDeviceConfigurator::class.java.simpleName

    private val mWiFiScanner: WiFiScanner = WiFiScanner(context)
    private var mFriendlyName: String? = null
    private var mDeviceSsid: String? = null
    private var mAPName: String? = null
    private var mAPPassword: String? = null
    private var mDeviceNetworkId: Int = -1
    private var mCurrentNetworkId: Int = -1
    private var mListener: OnAddDeviceFinishedListener? = null
    private var mHandlerTimeout = Handler()
    private var mConnectingCanceled: Boolean = true

    init {
        if (context is OnAddDeviceFinishedListener)
            mListener = context
    }

    fun cancelPair() {
        mWiFiScanner.unregisterOnConnected()
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
            mListener?.onAddDeviceFinished(AddDeviceArgs(false))
        }
    }

    private val timeout = object : Runnable {
        override fun run() {
            try {
                if (!mConnectingCanceled) {
                    mWiFiScanner.unregisterOnConnected()
                    mListener?.onAddDeviceFinished(AddDeviceArgs(false, ErrorCode.TIMEOUT))
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
            var deviceType: AisDeviceType? = null
            try {
                mConnectingCanceled = true
                mHandlerTimeout.removeCallbacksAndMessages(timeout)

                val connectStatus = connectAndConfiguraDevice()
                if (connectStatus.first) {
                    result = true
                    deviceType = connectStatus.second
                }
            } catch (e: UnsupportedEncodingException) {
                Log.e(TAG, "onConnected", e)
            } finally {
                reconnect()
                mListener?.onAddDeviceFinished(AddDeviceArgs(result, ErrorCode.OK, deviceType))
            }
        }
    }

    private suspend fun connectAndConfiguraDevice(): Pair<Boolean, AisDeviceType?> {
        // check if we have correct connection if not then exit
        val networkId = mWiFiScanner.getCurrentNetworkId()
        if (networkId != mDeviceNetworkId) {
            //Log.d(TAG, "wrong connection, info.getNetworkId(): " + info.networkId)
        }
        else {
            try {
                val deviceType = AisDeviceController.getType(AisDeviceController.AP_IP)
                val result = AisDeviceController.setupNew(mFriendlyName!!, mAPName!!, mAPPassword!!)
                if (result){
                    return Pair(true, deviceType)
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

    interface  OnAddDeviceFinishedListener {
        fun onAddDeviceFinished(result: AddDeviceArgs)
    }

    data class AddDeviceArgs(
        val result: Boolean = false,
        val errorCode: ErrorCode = ErrorCode.OK,
        val deviceType: AisDeviceType? = null
    )

    enum class ErrorCode{
        OK,
        TIMEOUT,
    }
}
