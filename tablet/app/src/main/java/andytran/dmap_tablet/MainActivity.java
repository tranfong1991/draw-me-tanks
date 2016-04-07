package andytran.dmap_tablet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private ImageView graphicView;
    private BroadcastReceiver receiver = new MainActivityBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphicView = (ImageView)findViewById(R.id.graphic_view);
        LocalBroadcastManager.
                getInstance(this).
                registerReceiver(receiver, new IntentFilter(getResources().getString(R.string.package_name)));
    }

    private class MainActivityBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();
            DMAPServer.Action action = (DMAPServer.Action)extra.get(DMAPServer.EXTRA_ACTION);

            if(action == null)
                return;

            switch(action){
                case PLAY_GRAPHIC: {
                    String fileName = extra.getString(DMAPServer.EXTRA_GRAPHIC_NAME);
                    File file = new File(getFilesDir(), fileName);

                    Ion.with(MainActivity.this).load(file).intoImageView(graphicView);
                    break;
                }
                case STOP_GRAPHIC:{
                    graphicView.setImageDrawable(null);
                    break;
                }
                case GO_TO_LOAD:{
                    Intent service = new Intent(MainActivity.this, DMAPIntentService.class);
                    stopService(service);

                    Intent i = new Intent(MainActivity.this, NSDBroadcastActivity.class);
                    startActivity(i);
                    finish();
                    break;
                }
            }
        }
    }
}
