package andytran.dmap_phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChooseEMILYActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    public static final String EXTRA_PORT = "EXTRA_PORT";
    public static final String EXTRA_IP = "EXTRA_IP";

    private String prefName;
    private String prefToken;
    private String prefIp;
    private String prefPort;

    private FrameLayout frameLayout;
    private LinearLayout linearLayout;
    private TextView serviceName;
    private ClientNSDHelper nsdHelper;
    private ArrayList<Host> hosts;
    private HostAdapter hostAdapter;
    private BroadcastReceiver receiver = new ClientNSDBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_emily);

        prefName = getResources().getString(R.string.pref_name);
        prefToken = getResources().getString(R.string.pref_token);
        prefIp = getResources().getString(R.string.pref_ip);
        prefPort = getResources().getString(R.string.pref_port);

        //check if access token is already there
        SharedPreferences pref = getSharedPreferences(prefName, 0);
        String token = pref.getString(prefToken, null);
        String ip = pref.getString(prefIp, null);
        int port = pref.getInt(prefPort, 0);

        if(token != null){
            proceedToMain(ip, port);
            return;
        }

        serviceName = (TextView)findViewById(R.id.service_name);
        frameLayout = (FrameLayout) findViewById(R.id.progressBarHolder);
        frameLayout.setClickable(false);
        linearLayout = (LinearLayout) findViewById(R.id.emilyHolder);

        hosts = new ArrayList<>();
        hostAdapter = new HostAdapter(this, hosts);

        nsdHelper = new ClientNSDHelper(this);
        nsdHelper.initializeNsd();
        nsdHelper.discoverServices();

        LocalBroadcastManager.
                getInstance(this).
                registerReceiver(receiver, new IntentFilter(getResources().getString(R.string.package_name)));

        ListView tabletListView = (ListView) findViewById(R.id.chooseEmilyListView);
        tabletListView.setAdapter(hostAdapter);
        tabletListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String name = String.valueOf(parent.getItemAtPosition(position));
        serviceName.setText(name);

        final Host host = hosts.get(position);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(ChooseEMILYActivity.this);
        StringBuffer buffer = new StringBuffer();
        buffer.append("http:/")
                .append(host.getIp())
                .append(":")
                .append(host.getPort())
                .append("/generate");

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buffer.toString(),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject json = new JSONObject(response);

                        SharedPreferences pref = getSharedPreferences(prefName, 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(prefToken, json.getString("token"));
                        editor.putString(prefIp, host.getIp());
                        editor.putInt(prefPort, host.getPort());
                        editor.apply();

                        removeOverlay();
                        nsdHelper.stopDiscovery();
                        proceedToMain(host.getIp(), host.getPort());
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    removeOverlay();
                    Toast.makeText(ChooseEMILYActivity.this, "Cannot connect to " + name, Toast.LENGTH_SHORT).show();
                }
            });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        addOverlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_screen, menu);
        return true;
    }

    public void removeOverlay() {
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.setClickable(true);

        frameLayout.setVisibility(View.GONE);
        frameLayout.setClickable(false);
    }

    public void addOverlay() {
        frameLayout.setVisibility(View.VISIBLE);
        frameLayout.setClickable(true);

        linearLayout.setVisibility(View.GONE);
        linearLayout.setClickable(false);
    }

    public void proceedToMain(String ipAddress, int port){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_IP, ipAddress);
        intent.putExtra(EXTRA_PORT, port);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ClientNSDBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();
            ClientNSDHelper.Action action = (ClientNSDHelper.Action) extra.get(ClientNSDHelper.EXTRA_ACTION);
            String serviceName = extra.getString(ClientNSDHelper.EXTRA_SERVICE_NAME);

            if(action == null)
                return;

            switch(action){
                case ADD_SERVICE:{
                    String ip = extra.getString(ClientNSDHelper.EXTRA_SERVICE_IP);
                    int port = extra.getInt(ClientNSDHelper.EXTRA_SERVICE_PORT);

                    hosts.add(new Host(serviceName, ip, port));
                    hostAdapter.notifyDataSetChanged();
                    break;
                }
                case REMOVE_SERVICE:{
                    hosts.remove(new Host(serviceName));
                    hostAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}
