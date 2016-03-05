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
    private BroadcastReceiver receiver = new LoadActivityBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        //start DMAP server
        Intent intent = new Intent(this, DMAPIntentService.class);
        startService(intent);

        //get access token
        SharedPreferences pref = getSharedPreferences(DMAPServer.PREF_NAME, 0);
        String token = pref.getString(DMAPServer.PREF_TOKEN, null);

        //start nsd if no token found
        if(token == null) {
            nsdHelper = new ServerNSDHelper(this);
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
                registerReceiver(receiver, new IntentFilter(DMAPServer.PACKAGE_NAME));
    }

    private class LoadActivityBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();
            DMAPServer.Action action = (DMAPServer.Action)extra.get(DMAPServer.EXTRA_ACTION);

            if(action == null)
                return;

            switch(action){
                case STOP_NSD:{
                    nsdHelper.unregisterService();

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
