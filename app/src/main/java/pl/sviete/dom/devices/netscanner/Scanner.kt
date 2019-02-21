package pl.sviete.dom.devices.netscanner

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.AisDeviceController
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.net.ipscanner.Host
import pl.sviete.dom.devices.net.ipscanner.IpScannerResult
import pl.sviete.dom.devices.net.ipscanner.ScanHostsAsyncTask
import pl.sviete.dom.devices.net.ipscanner.Wireless
import java.util.concurrent.atomic.AtomicInteger

class Scanner (context: Context, private val delegate: IScannerResult): IpScannerResult {
    private val mWifi = Wireless(context)

    fun scan(){
        runIpScanner()
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
            FoundDeviceRepository.getInstance().add(h.mac, h.ip)
            refreshDeviceStatus(h.mac, h.ip)
        }
    }
}