package pl.sviete.dom.devices.net

import android.content.Context
import android.os.Handler
import android.util.Log
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class AisDeviceController(context: Context): WiFiScanner.OnWiFiConnectedListener {
    private val TAG = AisDeviceController::class.java.simpleName

    //private val mContext: Context = context
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
                    mListener?.onAddDeviceFinished(false)
                    reconnect()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                reconnect()
            }
        }
    }

    override fun onConnected() {
        try {
            mConnectingCanceled = true
            mHandlerTimeout.removeCallbacksAndMessages(timeout)

            val url = URLEncoder.encode("Backlog FriendlyName1 $mFriendlyName; SSId1 $mAPName; Password1 $mAPPassword","UTF-8")
            if (connectAndConfiguraDevice(url)) {
                mListener?.onAddDeviceFinished(true)
                return
            }
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "onConnected", e)
        }
        finally {
            reconnect()
        }
        mListener?.onAddDeviceFinished(false)
    }

    private fun connectAndConfiguraDevice(url: String): Boolean {
        // check if we have correct connection if not then exit
        val networkId = mWiFiScanner.getCurrentNetworkId()
        if (networkId != mDeviceNetworkId) {
            //Log.d(TAG, "wrong connection, info.getNetworkId(): " + info.networkId)
        }
        else {
            try {
                val obj = URL("http://192.168.4.1/cm?cmnd=$url")

                val con = obj.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.setRequestProperty("User-Agent", "Mozilla/5.0")

                if (con.responseCode == HttpURLConnection.HTTP_OK) {

                    return true
                }
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
        fun onAddDeviceFinished(result: Boolean)
    }
}