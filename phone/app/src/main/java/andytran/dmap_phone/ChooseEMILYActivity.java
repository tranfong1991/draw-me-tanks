package andytran.dmap_phone;

import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//To write:
//Screen rotation persistence
    //http://developer.android.com/guide/topics/resources/runtime-changes.html#HandlingTheChange
//Thread for calling the package request
//Creating an intent for the main menu page


public class ChooseEMILYActivity extends AppCompatActivity {
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_IP_ADDR = "ip";
    public static final String PREF_NAME = "DMAP_PREF";
    public static final String PREF_TOKEN = "DMAP_TOKEN";
    public static final String PREF_IP = "DMAP_IP";
    public static final String PREF_PORT = "DMAP_PORT";

    private Handler updateBarHandler;
    private ProgressDialog progressDialog;

    private FrameLayout frameLayout;
    private LinearLayout linearLayout;
    private ClientNSDHelper nsdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //get access token
        SharedPreferences pref = getSharedPreferences(PREF_NAME, 0);
        String token = pref.getString(PREF_TOKEN, null);
        String ip = pref.getString(PREF_IP, null);
        String port = pref.getString(PREF_PORT, null);

        if(token != null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_IP_ADDR, ip);
            intent.putExtra(EXTRA_PORT, port);
            startActivity(intent);
            finish();

            return;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_emily);

        frameLayout = (FrameLayout) findViewById(R.id.progressBarHolder);
        frameLayout.setClickable(false);
        linearLayout = (LinearLayout) findViewById(R.id.emilyHolder);

        final TextView serviceName = (TextView)findViewById(R.id.service_name);
        final ArrayList<String> tabletNames = new ArrayList<>();
        final ArrayAdapter<String> titleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tabletNames);

        nsdHelper = new ClientNSDHelper(this);
        nsdHelper.initializeNsd();
        nsdHelper.discoverServices();

        LocalBroadcastManager.
                getInstance(this).
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Bundle extra = intent.getExtras();
                        String name = extra.getString("EXTRA_NAME");

                        tabletNames.add(name);
                        titleAdapter.notifyDataSetChanged();
                    }
                }, new IntentFilter("DMAP"));

        ListView tabletListView = (ListView) findViewById(R.id.chooseEmilyListView);
        tabletListView.setAdapter(titleAdapter);
        tabletListView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String name = String.valueOf(parent.getItemAtPosition(position));
                    serviceName.setText(name);

                    final NsdServiceInfo info = nsdHelper.getServiceInfoAt(position);

                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(ChooseEMILYActivity.this);
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("http:/")
                            .append(info.getHost())
                            .append(":")
                            .append(info.getPort())
                            .append("/generate");

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, buffer.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject json = new JSONObject(response);

                                    SharedPreferences pref = ChooseEMILYActivity.this.getSharedPreferences(PREF_NAME, 0);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString(PREF_TOKEN, json.getString("token"));
                                    editor.putString(PREF_IP, info.getHost().toString());
                                    editor.putInt(PREF_PORT, info.getPort());
                                    editor.apply();

                                    removeOverlay();
                                    nsdHelper.stopDiscovery();
                                    proceedToMain(info.getHost().toString(), info.getPort());
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                removeOverlay();
                            }
                        });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                    addOverlay();
                }
            }
        );
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void proceedToMain(String ipAddress, int port){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_IP_ADDR, ipAddress);
        intent.putExtra(EXTRA_PORT, port);
        startActivity(intent);
        finish();
    }
}
