package pl.sviete.dom.devices.net.ipscanner;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import jcifs.netbios.NbtAddress;

public class ScanHostsAsyncTask extends AsyncTask<Integer, Void, Void> {
    private final WeakReference<IpScannerResult> delegate;
    private static final String ARP_TABLE = "/proc/net/arp";
    private static final String ARP_INCOMPLETE = "0x0";
    private static final String ARP_INACTIVE = "00:00:00:00:00:00";
    private static final int NETBIOS_FILE_SERVER = 0x20;

    /**
     * Constructor to set the delegate
     *
     * @param delegate Called when host discovery has finished
     */
    public ScanHostsAsyncTask(IpScannerResult delegate) {
        this.delegate = new WeakReference<>(delegate);
    }

    /**
     * Scans for active hosts on the network
     *
     * @param params IP address
     */
    @Override
    protected Void doInBackground(Integer... params) {
        int ipv4 = params[0];
        int cidr = params[1];
        int timeout = params[2];

        IpScannerResult activity = delegate.get();
        File file = new File(ARP_TABLE);
        if (!file.exists() || !file.canRead()) {
            activity.scanFinish(new FileNotFoundException("Unable to access device ARP table"));
            return null;
        }

        ExecutorService executor = Executors.newCachedThreadPool();

        double hostBits = 32.0d - cidr; // How many bits do we have for the hosts.
        int netmask = (0xffffffff >> (32 - cidr)) << (32 - cidr); // How many bits for the netmask.
        int numberOfHosts = (int) Math.pow(2.0d, hostBits) - 2; // 2 ^ hostbits = number of hosts in integer.
        int firstAddr = (ipv4 & netmask) + 1; // AND the bits we care about, then first addr.

        int SCAN_THREADS = (int) hostBits;
        int chunk = (int) Math.ceil((double) numberOfHosts / SCAN_THREADS); // Chunk hosts by number of threads.
        int previousStart = firstAddr;
        int previousStop = firstAddr + (chunk - 2); // Ignore network + first addr

        for (int i = 0; i < SCAN_THREADS; i++) {
            executor.execute(new ScanHostsRunnable(previousStart, previousStop, timeout, delegate));
            previousStart = previousStop + 1;
            previousStop = previousStart + (chunk - 1);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
            executor.shutdownNow();
        } catch (InterruptedException e) {
            activity.scanFinish(e);
            return null;
        }

        publishProgress();
        return null;
    }

    /**
     * Scans the ARP table and updates the list with hosts on the network
     * Resolves both DNS and NetBIOS
     * Don't update the UI in onPostExecute since we want to do multiple UI updates here
     * onPostExecute seems to perform all UI updates at once which would hinder what we're doing here
     *
     * @param params
     */
    @Override
    protected final void onProgressUpdate(Void... params) {
        BufferedReader reader = null;
        final IpScannerResult activity = delegate.get();
        ExecutorService executor = Executors.newCachedThreadPool();
        final AtomicInteger numHosts = new AtomicInteger(0);

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(ARP_TABLE), "UTF-8"));
            reader.readLine(); // Skip header.
            String line;

            while ((line = reader.readLine()) != null) {
                String[] arpLine = line.split("\\s+");

                final String ip = arpLine[0];
                final String flag = arpLine[2];
                final String macAddress = arpLine[3];

                if (!ARP_INCOMPLETE.equals(flag) && !ARP_INACTIVE.equals(macAddress)) {
                    numHosts.incrementAndGet();
                    executor.execute(new Runnable() {

                        @Override
                        public void run() {
                            Host host = new Host(ip, macAddress);

                            IpScannerResult activity = delegate.get();
                            try {
                                InetAddress add = InetAddress.getByName(ip);
                                String hostname = add.getCanonicalHostName();
                                host.setHostname(hostname);

                                if (activity != null) {
                                    activity.hostFounded(host, numHosts);
                                }
                            } catch (UnknownHostException e) {
                                numHosts.decrementAndGet();
                                activity.scanFinish(e);
                                return;
                            }

                            try {
                                NbtAddress[] netbios = NbtAddress.getAllByAddress(ip);
                                for (NbtAddress addr : netbios) {
                                    if (addr.getNameType() == NETBIOS_FILE_SERVER) {
                                        host.setHostname(addr.getHostName());
                                        return;
                                    }
                                }
                            } catch (UnknownHostException e) {
                                // It's common that many discovered hosts won't have a NetBIOS entry.
                            }
                        }
                    });
                }
            }
        } catch (IOException e) {
            if (activity != null) {
                activity.scanFinish(e);
            }

        } finally {
            executor.shutdown();
            if (activity != null) {
                activity.scanFinish();
            }

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
                // Something's really wrong if we can't close the stream...
            }
        }
    }
}
