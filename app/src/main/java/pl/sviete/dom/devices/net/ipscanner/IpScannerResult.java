package pl.sviete.dom.devices.net.ipscanner;

import java.util.concurrent.atomic.AtomicInteger;

public interface IpScannerResult {
    void hostFounded(Host h, AtomicInteger i);
    void scanFinish();
    void scanFinish(Throwable ex);
}
