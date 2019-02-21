package pl.sviete.dom.devices.net.ipscanner;

import java.util.concurrent.atomic.AtomicInteger;

public interface IpScannerResult {

    /**
     * Delegate to handle Host + AtomicInteger outputs
     *
     * @param h
     * @param i
     */
    void processFinish(Host h, AtomicInteger i);

    //void processFinish(int output);

    /**
     * Delegate to handle boolean outputs
     *
     * @param output
     */
    void processFinish(boolean output);

    void processFinish(Throwable output);

}
