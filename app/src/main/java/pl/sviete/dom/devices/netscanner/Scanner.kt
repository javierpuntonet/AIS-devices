package pl.sviete.dom.devices.netscanner

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.AisDeviceRestController
import pl.sviete.dom.devices.aiscontrollers.BoxRestController
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.net.ipscanner.Host
import pl.sviete.dom.devices.net.ipscanner.IpScannerResult
import pl.sviete.dom.devices.net.ipscanner.ScanHostsAsyncTask
import pl.sviete.dom.devices.net.ipscanner.Wireless
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger

class Scanner (val context: Context, private val delegate: IScannerResult): IpScannerResult {
    private val TAG = Scanner::class.java.simpleName
    private val mWifi = Wireless(context)

    val devices : FoundDeviceRepository get() = FoundDeviceRepository.getInstance()
    val boxes : FoundBoxRepository get() = FoundBoxRepository.getInstance()

    fun addDevice(ip: String, mac: String?, founded: Boolean){
        devices.add(ip, mac, founded)
        refreshDeviceStatus(ip)
    }

    fun runIpScanner(): Boolean {
        try {
            val ip = mWifi.internalWifiIpAddress
            if (ip != 0) {
                ScanHostsAsyncTask(this).execute(ip, mWifi.internalWifiSubnet, 150)
                return true
            }
        }catch (ex: Exception){
            Log.e(TAG, "runIpScanner $ex")
        }
        return false
    }

    private fun refreshDeviceStatus(ip: String){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "refreshDeviceStatus: $ip")
                val status = AisDeviceRestController.getStatus(ip)
                if (status != null) {
                    Log.d(TAG, "refreshDeviceStatus found AIS on $ip")
                    devices.setAisDevice(ip,
                        status.StatusNET.Mac,
                        status.Status.FriendlyName.first(),
                        AisDeviceType.fromInt(status.Status.Module),
                        if (status.Status.Power == 0)  PowerStatus.Off else PowerStatus.On)
                }
                else {
                    Log.d(TAG, "refreshDeviceStatus not AIS on $ip")
                    devices.setNonAisDevice(ip)
                }
            } catch (ex: Exception){
                Log.e(TAG, "refreshDeviceStatus: $ex")
            }
        }
    }

    fun addBox(ip: String, gateId: String, name: String){
        boxes.add(name,  gateId, ip, false)
    }

    private fun getBoxInfo(ip: String){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val info = BoxRestController.getInfo(ip)
                if (info != null) {
                    boxes.add(info.Hostname, info.GateId, ip, true)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "getBoxInfo: $ex")
            }
        }
    }

    //IP scanner results method
    override fun scanFinish() {
        Log.d(TAG, "IP scanner: scan finish")
        delegate.ipScanFinished()
    }

    override fun scanFinish(ex: Throwable?) {
        Log.w(TAG, "IP scanner: scan exception", ex)
        delegate.ipScanFinished()
    }

    override fun hostFounded(h: Host?, i: AtomicInteger?) {
        if (h != null) {
            Log.d(TAG, "IP scanner found: ${h.ip}")
            addDevice(h.ip, null,true)
        }
    }
}