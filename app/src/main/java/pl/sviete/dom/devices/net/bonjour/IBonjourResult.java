package pl.sviete.dom.devices.net.bonjour;

import com.github.druk.rx2dnssd.BonjourService;

public interface IBonjourResult {
    void onDeviceFound(BonjourService service);
    void onBoxFound(BonjourService service);
}
