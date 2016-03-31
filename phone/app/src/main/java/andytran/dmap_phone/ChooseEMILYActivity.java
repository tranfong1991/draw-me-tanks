package andytran.dmap_phone;

import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

//To write:
//Screen rotation persistence
    //http://developer.android.com/guide/topics/resources/runtime-changes.html#HandlingTheChange
//Thread for calling the package request
//Creating an intent for the main menu page


public class ChooseEMILYActivity extends AppCompatActivity {
    static final String EXTRA_PORT = "port";
    static final String EXTRA_IP_ADDR = "ip addr";

    Handler updateBarHandler;
    ProgressDialog progressDialog;

    FrameLayout frameLayout;
    LinearLayout linearLayout;
    private ClientNSDHelper nsdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //CHECK ACCESS TOKEN
        //Read up on shared_preferences
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_emily);

        frameLayout = (FrameLayout) findViewById(R.id.progressBarHolder);
        frameLayout.setClickable(false);

        linearLayout = (LinearLayout) findViewById(R.id.emilyHolder);
        final TextView connectTextView = (TextView)findViewById(R.id.connection_status_msg);

        final ArrayList<String> tabletNames = new ArrayList<>();
        final ArrayAdapter<String> titleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tabletNames);

        nsdHelper = new ClientNSDHelper(this);
        nsdHelper.initializeNsd();

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

        Button discoverButton = (Button)findViewById(R.id.discover_button);
        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nsdHelper.discoverServices();
            }
        });

        ListView tabletListView = (ListView) findViewById(R.id.chooseEmilyListView);
        tabletListView.setAdapter(titleAdapter);

        tabletListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String name = String.valueOf(parent.getItemAtPosition(position));
//                        showMessage("Connecting to " + name, "When the connection is complete, the change will switch.");

//                        connectTextView.setText("Connecting to " + name);

                        NsdServiceInfo info = nsdHelper.getChosenServiceInfo();
                        connectTextView.setText(info.getHost().toString() + ", " + info.getPort());

//                        addOverlay();

                        //Pseudocode for getting the response
                        //RUN A NEW THREAD
                        //THREAD SHOULDO DO IT'S THING
                        //IF RESPONSE IS BAD
                            // SET AN ALERT
                            //showMessage("Could not connect to " + name, "Please try another tablet, or wait 5 minutes and attempt again");
                            //removeOverlay()
                        //IF RESPONSE IS GOOD

                            // PROCEEDTOMAIN()


//                        proceedToMain("Placeholder", "placeholder");
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

    public void showMessage(String title, String message){
        removeOverlay();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage((message));
        builder.show();
    }

    public void removeOverlay() {
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.setClickable(true);

        frameLayout.setVisibility(View.GONE);
        frameLayout.setClickable(false);
    }

    public void removeOverlay(View view) {
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
        // Handle action bar graphic_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void proceedToMain(String ipAddress, String port){

        Intent intent = new Intent(this, MainActivity.class);
//        EditText ipAddressText = (EditText)findViewById(R.id.ip_field);
//        EditText portNumberText  = (EditText)findViewById(R.id.port_field);

        intent.putExtra(EXTRA_IP_ADDR, ipAddress);
        intent.putExtra(EXTRA_PORT, port);

        startActivity(intent);
    }

    public void proceedToMain(View view, String ipAddress, String port){
        Intent intent = new Intent(this, MainActivity.class);
//        EditText ipAddressText = (EditText)findViewById(R.id.ip_field);
//        EditText portNumberText  = (EditText)findViewById(R.id.port_field);

        intent.putExtra(EXTRA_IP_ADDR, ipAddress);
        intent.putExtra(EXTRA_PORT, port);

        startActivity(intent);
    }
}
