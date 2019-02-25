package pl.sviete.dom.devices.netscanner

import android.content.Context
import com.github.druk.rx2dnssd.BonjourService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.AisDeviceController
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
    override fun onFound(service: BonjourService?) {
        if (service != null && service.inet4Address?.hostAddress != null) {
            if (FoundDeviceRepository.getInstance().add("unknown", service.inet4Address!!.hostAddress))
                refreshDeviceStatus("unknown", service.inet4Address!!.hostAddress)
        }
    }

    override fun onLost(service: BonjourService?) {

    }

    private val mWifi = Wireless(context)
    var b: BonjourScanner? = null

    fun scan(){
        runBonjourScanner()
        //runIpScanner()
    }

    private fun runBonjourScanner() {
        if (b == null)
            b = BonjourScanner(context, this)
        b!!.startDiscovery()
    }

    private fun runIpScanner() {
        val ip = mWifi.internalWifiIpAddress
        ScanHostsAsyncTask(this).execute(ip, mWifi.internalWifiSubnet, 150)
    }

    private fun refreshDeviceStatus(mac: String, ip: String){
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val status = AisDeviceController.getStatus(ip)
                if (status != null) {
                    FoundDeviceRepository.getInstance().set(mac,
                        true,
                        status.Status.FriendlyName.first(),
                        AisDeviceType.fromInt(status.Status.Module),
                        if (status.Status.Power == 0)  PowerStatus.Off else PowerStatus.On)
                }
                else {
                    FoundDeviceRepository.getInstance().set(mac,false)
                }
            } finally {

            }
        }
    }

    override fun processFinish(output: Boolean) {
        delegate.scanFinished()
    }

    override fun processFinish(output: Throwable?) {
        delegate.scanFinished()
    }

    override fun processFinish(h: Host?, i: AtomicInteger?) {
        if (h != null) {
            if (FoundDeviceRepository.getInstance().add(h.mac, h.ip))
                refreshDeviceStatus(h.mac, h.ip)
        }
    }
}