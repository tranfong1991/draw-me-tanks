package andytran.dmap_tablet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_token) {
            SharedPreferences pref = getSharedPreferences(getResources().getString(R.string.pref_name), 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(getResources().getString(R.string.pref_token), null);
            editor.apply();

            Intent intent = new Intent(this, NSDBroadcastActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
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
