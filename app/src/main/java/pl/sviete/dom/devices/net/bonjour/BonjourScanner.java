package pl.sviete.dom.devices.net.bonjour;

import android.content.Context;
import com.github.druk.rx2dnssd.BonjourService;
import com.github.druk.rx2dnssd.Rx2Dnssd;
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BonjourScanner {

    private Rx2Dnssd mRxDnssd;
    private Disposable mDisposable;
    private IBonjourResult mResult;

    public BonjourScanner(Context context, IBonjourResult result){
        mRxDnssd = new Rx2DnssdEmbedded(context);
        mResult = result;
    }

    public void startDiscovery() {
        mDisposable = mRxDnssd.browse("_http._tcp", "local.")
                .compose(mRxDnssd.resolve())
                .compose(mRxDnssd.queryIPRecords())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BonjourService>() {
                    @Override
                    public void accept(BonjourService bonjourService) {
                        Object a = bonjourService.getInetAddresses();
                        Object a1 = bonjourService.getInet4Address();
                        if (!bonjourService.isLost()) {
                            mResult.onFound(bonjourService);
                        } else {
                            mResult.onLost(bonjourService);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable s) {
                        System.out.println("Hello " + s + "!");
                    }
                });
    }

    protected void stopDiscovery() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
