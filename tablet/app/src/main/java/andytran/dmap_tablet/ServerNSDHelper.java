package andytran.dmap_tablet;

/**
 * Created by Andy Tran on 2/29/2016.
 */

import android.net.nsd.NsdManager;
import android.content.Context;
import android.net.nsd.NsdServiceInfo;

public class ServerNSDHelper {
    public static final String SERVICE_TYPE = "_http._tcp.";
    public static final String TAG = "ServerNsdHelper";
    public String mServiceName = "DMAP";

    private Context mContext;
    private NsdManager mNsdManager;
    private NsdServiceInfo mService;
    private NsdManager.RegistrationListener mRegistrationListener;

    public ServerNSDHelper(Context context) {
        mContext = context;
        mService = null;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeNsd() {
        initializeRegistrationListener();
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                mServiceName = NsdServiceInfo.getServiceName();
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                mServiceName = null;
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }

        };
    }

    public void registerService(int port) {
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(SERVICE_TYPE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void unregisterService() {
        mNsdManager.unregisterService(mRegistrationListener);
    }
}

