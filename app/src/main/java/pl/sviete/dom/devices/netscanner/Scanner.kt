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

    private val mWifi = Wireless(context)
    private var mBonjour: BonjourScanner? = null

    val repository : FoundDeviceRepository get() = FoundDeviceRepository.getInstance()

    fun scan(){
        runBonjourScanner()
        //runIpScanner()
    }

    fun stop(){
        mBonjour?.stopDiscovery()
    }

    fun add(ip: String, founded: Boolean){
        if (FoundDeviceRepository.getInstance().add(ip, founded))
            refreshDeviceStatus(ip)
    }

    private fun runBonjourScanner() {
        if (mBonjour == null)
            mBonjour = BonjourScanner(context, this)
        mBonjour!!.startDiscovery()
    }

    private fun runIpScanner() {
        val ip = mWifi.internalWifiIpAddress
        ScanHostsAsyncTask(this).execute(ip, mWifi.internalWifiSubnet, 150)
    }

    private fun refreshDeviceStatus(ip: String){
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val status = AisDeviceController.getStatus(ip)
                if (status != null) {
                    FoundDeviceRepository.getInstance().set(ip,
                        true,
                        status.StatusNET.Mac,
                        status.Status.FriendlyName.first(),
                        AisDeviceType.fromInt(status.Status.Module),
                        if (status.Status.Power == 0)  PowerStatus.Off else PowerStatus.On)
                }
                else {
                    FoundDeviceRepository.getInstance().set(ip,false)
                }
            } finally {

            }
        }
    }

    override fun onFound(service: BonjourService?) {
        val ip = service?.inet4Address?.hostAddress
        if (ip != null) {
            add(ip, true)
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
            add(h.ip, true)
        }
    }
}