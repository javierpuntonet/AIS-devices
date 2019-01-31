package pl.sviete.dom.devices.net

import android.content.Context
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.AisDeviceController
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
            mListener?.onAddDeviceFinished(false)
        }
    }

    private val timeout = object : Runnable {
        override fun run() {
            try {
                if (!mConnectingCanceled) {
                    mWiFiScanner.unregisterOnConnected()
                    mListener?.onAddDeviceFinished(false, ErrorCode.TIMEOUT)
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
            try {
                mConnectingCanceled = true
                mHandlerTimeout.removeCallbacksAndMessages(timeout)

                if (connectAndConfiguraDevice()) {
                    result = true
                }
            } catch (e: UnsupportedEncodingException) {
                Log.e(TAG, "onConnected", e)
            } finally {
                reconnect()
                mListener?.onAddDeviceFinished(result)
            }
        }
    }

    private suspend fun connectAndConfiguraDevice(): Boolean {
        // check if we have correct connection if not then exit
        val networkId = mWiFiScanner.getCurrentNetworkId()
        if (networkId != mDeviceNetworkId) {
            //Log.d(TAG, "wrong connection, info.getNetworkId(): " + info.networkId)
        }
        else {
            try {

                val result = AisDeviceController.setupNew(mFriendlyName!!, mAPName!!, mAPPassword!!)
                if (result){

                    return true
                }
                return false
            } catch (e: Exception) {
                Log.e(TAG, "ConnectAndConfiguraDevice", e)
            }
        }
        return false
    }

    private fun reconnect(){
        mWiFiScanner.removeNetwork(mDeviceNetworkId)
        mWiFiScanner.enableAllNetworks()
        mWiFiScanner.connectToNetwork(mCurrentNetworkId)
    }

    interface  OnAddDeviceFinishedListener {
        fun onAddDeviceFinished(result: Boolean, errorReason: ErrorCode = ErrorCode.OK)
    }

    enum class ErrorCode{
        OK,
        TIMEOUT,
    }
}
