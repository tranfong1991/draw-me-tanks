package andytran.dmap_tablet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NSDBroadcastActivity extends AppCompatActivity {
    private ServerNSDHelper nsdHelper;
    private BroadcastReceiver receiver = new ServerNSDBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd_broadcast);

        //start DMAP server
        Intent intent = new Intent(this, DMAPIntentService.class);
        startService(intent);

        //get access token
        SharedPreferences pref = getSharedPreferences(getResources().getString(R.string.pref_name), 0);
        String token = pref.getString(getResources().getString(R.string.pref_token), null);

        //start nsd if no token found
        if(token == null) {
            nsdHelper = new ServerNSDHelper(NSDBroadcastActivity.this);
            nsdHelper.initializeNsd();
            nsdHelper.registerService(DMAPServer.PORT);
        } else {
            //switch to main screen
            Intent i = new Intent(NSDBroadcastActivity.this, MainActivity.class);
            startActivity(i);
            finish();

            return;
        }

        LocalBroadcastManager.
                getInstance(this).
                registerReceiver(receiver, new IntentFilter(getResources().getString(R.string.package_name)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nsdHelper != null)
            nsdHelper.unregisterService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nsdHelper != null)
            nsdHelper.registerService(DMAPServer.PORT);
    }

    @Override
    protected void onDestroy() {
        if(nsdHelper != null)
            nsdHelper.unregisterService();
        super.onDestroy();
    }

    private class ServerNSDBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();
            DMAPServer.Action action = (DMAPServer.Action)extra.get(DMAPServer.EXTRA_ACTION);

            if(action == null)
                return;

            switch(action){
                case GO_TO_MAIN:{
                    if(nsdHelper != null) {
                        nsdHelper.unregisterService();
                        nsdHelper = null;
                    }

                    //switch to main screen
                    Intent i = new Intent(NSDBroadcastActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    break;
                }
            }
        }
    }
}
