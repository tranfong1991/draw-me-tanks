package andytran.dmap_phone;

/**
 * Created by Andy Tran on 2/29/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class ClientNSDHelper {
    public enum Action{
        ADD_SERVICE,
        REMOVE_SERVICE
    }

    public static final String SERVICE_TYPE = "_http._tcp.";
    public static final String SERVICE_NAME = "EMILY";
    public static final String EXTRA_SERVICE_NAME = "EXTRA_SERVICE_NAME";
    public static final String EXTRA_SERVICE_IP = "EXTRA_SERVICE_IP";
    public static final String EXTRA_SERVICE_PORT = "EXTRA_SERVICE_PORT";
    public static final String EXTRA_ACTION = "EXTRA_ACTION";
    public static final String TAG = "ClientNSDHelper";

    private Context mContext;
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;

    public ClientNSDHelper(Context context){
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeNsd() {
        initializeDiscoveryListener();
    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success: " + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                }  else if (service.getServiceName().contains(SERVICE_NAME)){
                    mNsdManager.resolveService(service, new MyResolveListener());
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Service lost " + serviceInfo);

                Intent intent = new Intent(mContext.getResources().getString(R.string.package_name));
                intent.putExtra(EXTRA_ACTION, Action.REMOVE_SERVICE);
                intent.putExtra(EXTRA_SERVICE_NAME, serviceInfo.getServiceName());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void discoverServices() {
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    private class MyResolveListener implements NsdManager.ResolveListener{
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.e(TAG, "Resolve failed " + errorCode);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

            Intent intent = new Intent(mContext.getResources().getString(R.string.package_name));
            intent.putExtra(EXTRA_ACTION, Action.ADD_SERVICE);
            intent.putExtra(EXTRA_SERVICE_NAME, serviceInfo.getServiceName());
            intent.putExtra(EXTRA_SERVICE_IP, serviceInfo.getHost().toString());
            intent.putExtra(EXTRA_SERVICE_PORT, serviceInfo.getPort());
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }
}


