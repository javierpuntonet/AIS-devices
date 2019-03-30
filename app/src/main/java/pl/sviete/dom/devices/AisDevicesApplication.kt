package pl.sviete.dom.devices

import android.app.Application
import android.content.Context
import com.github.druk.rx2dnssd.Rx2Dnssd
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded

class AisDevicesApplication : Application() {

    private var rxDnssd: Rx2Dnssd? = null

    override fun onCreate() {
        super.onCreate()

        rxDnssd = Rx2DnssdEmbedded(this)
    }

    fun getRxDnssd(): Rx2Dnssd {
        return rxDnssd!!
    }
}