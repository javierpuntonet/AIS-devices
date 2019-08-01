package pl.sviete.dom.devices.netscanner

import android.content.Context
import com.github.druk.rx2dnssd.BonjourService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.AisDeviceRestController
import pl.sviete.dom.devices.aiscontrollers.BoxRestController
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.net.bonjour.BonjourScanner
import pl.sviete.dom.devices.net.bonjour.IBonjourResult
import pl.sviete.dom.devices.net.ipscanner.Host
import pl.sviete.dom.devices.net.ipscanner.IpScannerResult
import pl.sviete.dom.devices.net.ipscanner.ScanHostsAsyncTask
import pl.sviete.dom.devices.net.ipscanner.Wireless
import java.util.concurrent.atomic.AtomicInteger

class Scanner (val context: Context, private val delegate: IScannerResult): IpScannerResult, IBonjourResult {

    private val mWifi = Wireless(context)
    private var mBonjour: BonjourScanner? = null

    val devices : FoundDeviceRepository get() = FoundDeviceRepository.getInstance()
    val boxes : FoundBoxRepository get() = FoundBoxRepository.getInstance()

    fun runBonjourDeviceScanner(){
        if (mBonjour == null)
            mBonjour = BonjourScanner(context, this)
        mBonjour!!.startDiscoveryDevice()
    }

    fun stopBonjourDeviceScanner(){
        mBonjour?.stopDiscoveryDevice()
    }

    fun addDevice(ip: String, mac: String?, founded: Boolean){
        devices.add(ip, mac, founded)
        refreshDeviceStatus(ip)
    }

    fun runIpScanner() {
        val ip = mWifi.internalWifiIpAddress
        ScanHostsAsyncTask(this).execute(ip, mWifi.internalWifiSubnet, 150)
    }

    private fun refreshDeviceStatus(ip: String){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val status = AisDeviceRestController.getStatus(ip)
                if (status != null) {
                    devices.setAisDevice(ip,
                        status.StatusNET.Mac,
                        status.Status.FriendlyName.first(),
                        AisDeviceType.fromInt(status.Status.Module),
                        if (status.Status.Power == 0)  PowerStatus.Off else PowerStatus.On)
                }
                else {
                    devices.setNonAisDevice(ip)
                }
            } finally {

            }
        }
    }

    override fun onDeviceFound(service: BonjourService?) {
        val ip = service?.inet4Address?.hostAddress
        if (ip != null) {
            addDevice(ip, null, true)
        }
    }

    override fun onBoxFound(service: BonjourService?) {
        val ip = service?.inet4Address?.hostAddress
        if (ip != null) {
            getBoxInfo(ip)
        }
    }

    private fun getBoxInfo(ip: String){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val info = BoxRestController.getInfo(ip)
                if (info != null) {
                    boxes.add(info.Hostname, info.GateId, ip)
                }
            } finally {

            }
        }
    }

    //IP scanner
    override fun processFinish(output: Boolean) {
        delegate.ipScanFinished()
    }

    override fun processFinish(output: Throwable?) {
        delegate.ipScanFinished()
    }

    override fun processFinish(h: Host?, i: AtomicInteger?) {
        if (h != null) {
            addDevice(h.ip, null,true)
        }
    }
}