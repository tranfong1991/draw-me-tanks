package andytran.dmap_tablet;

/**
 * Created by Andy Tran on 2/29/2016.
 */
import android.net.nsd.NsdManager;
import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.TextView;

public class ServerNSDHelper {
    public static final String SERVICE_TYPE = "_http._tcp.";
    public static final String SERVICE_NAME = "EMILY";
    public static final String TAG = "ServerNsdHelper";

    private Context mContext;
    private NsdManager mNsdManager;
    private NsdManager.RegistrationListener mRegistrationListener;

    public ServerNSDHelper(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeNsd() {
        initializeRegistrationListener();
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(final NsdServiceInfo nsdServiceInfo) {
                Log.d(TAG, "Service Registered");


                NSDBroadcastActivity activity = (NSDBroadcastActivity)mContext;
                final TextView tabletName = (TextView) activity.findViewById(R.id.tablet_name);
                final TextView ip = (TextView) activity.findViewById(R.id.text_ip);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tabletName.setText(nsdServiceInfo.getServiceName());
                        ip.setText(Utils.getIpAddress());
                    }
                });
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                Log.d(TAG, "Service Unregistered");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }

        };
    }

    public void registerService(int port) {
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void unregisterService() {
        mNsdManager.unregisterService(mRegistrationListener);
    }
}

