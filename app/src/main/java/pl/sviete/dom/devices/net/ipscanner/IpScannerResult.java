package pl.sviete.dom.devices.net.ipscanner;

import java.util.concurrent.atomic.AtomicInteger;

public interface IpScannerResult {

    void processFinish(Host h, AtomicInteger i);
    void processFinish(boolean output);
    void processFinish(Throwable output);

}
