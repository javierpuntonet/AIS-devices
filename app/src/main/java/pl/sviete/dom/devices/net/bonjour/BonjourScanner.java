package pl.sviete.dom.devices.net.bonjour;

import android.content.Context;
import android.util.Log;
import com.github.druk.rx2dnssd.Rx2Dnssd;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.sviete.dom.devices.AisDevicesApplication;

public class BonjourScanner {

    private Rx2Dnssd mRxDnssd;
    private Disposable mDevicesDisposable;
    private IBonjourResult mResult;

    public BonjourScanner(Context context, IBonjourResult result){
        mRxDnssd = ((AisDevicesApplication)context.getApplicationContext()).getRxDnssd();
        mResult = result;
    }

    public void startDiscoveryDevice() {
        mDevicesDisposable = mRxDnssd.browse("_http._tcp", "local.")
                .compose(mRxDnssd.resolve())
                .compose(mRxDnssd.queryIPRecords())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bonjourService -> {
                    if (!bonjourService.isLost()) {
                        if (bonjourService.getServiceName().equals("ais-dom"))
                            mResult.onBoxFound(bonjourService);
                        else
                            mResult.onDeviceFound(bonjourService);
                    }
                }, s -> {
                    Log.e("startDiscoveryDevice", "Error: ", s);
                });
    }

    public void stopDiscoveryDevice() {
        if (mDevicesDisposable != null) {
            mDevicesDisposable.dispose();
        }
    }
}
