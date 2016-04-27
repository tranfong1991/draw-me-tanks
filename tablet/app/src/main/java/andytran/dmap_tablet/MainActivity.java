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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private ImageView graphicView;
    private BroadcastReceiver receiver = new MainActivityBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(Utils.getIpAddress());

        Intent intent = new Intent(this, DMAPIntentService.class);
        startService(intent);

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

        if (id == R.id.action_delete_token) {
            SharedPreferences pref = getSharedPreferences(getResources().getString(R.string.pref_name), 0);
            String token = pref.getString(getResources().getString(R.string.pref_token),  null);

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, "http://localhost:8080/deactivate?token=" + token, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject json = new JSONObject(response);
                        int status = json.getInt("status");

                        if(status == 200)
                            Toast.makeText(MainActivity.this, "Successfully deleted token.", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(MainActivity.this, "Failed to delete token.", Toast.LENGTH_SHORT).show();
                    }catch(JSONException e){
                        Toast.makeText(MainActivity.this, "Failed to delete token.", Toast.LENGTH_SHORT).show();
                    }
                }
            }, null);
            queue.add(stringRequest);

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

                    if(Utils.isInteger(fileName, 10))
                        Picasso.with(MainActivity.this).load(Integer.parseInt(fileName)).into(graphicView);
                    else{
                        File file = new File(getFilesDir(), fileName);
                        Ion.with(MainActivity.this).load(file).intoImageView(graphicView);
                    }

                    break;
                }
                case STOP_GRAPHIC:{
                    graphicView.setImageDrawable(null);
                    break;
                }
                case GO_TO_LOAD:{
                    Intent i = new Intent(MainActivity.this, NSDBroadcastActivity.class);
                    startActivity(i);
                    finish();
                    break;
                }
            }
        }
    }
}
