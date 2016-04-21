package andytran.dmap_phone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ImageButton;
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
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import core.db.InstructionalGraphicDbAccess;
import timothy.dmap_phone.InstructionalGraphicTimer;

import timothy.dmap_phone.InstructionalGraphic;

public class MainActivity extends AppCompatActivity {
    private String prefName;
    private String prefToken;
    private String prefIp;
    private String prefPort;

    private String token;
    private String hostIp;
    private int hostPort;

    private InstructionalGraphicTimer timer;

    ListView list;
    GraphicAdapter adapter;
    ArrayList<InstructionalGraphic> igs;

    int clicks = 0;
    int listPosition = 0;


/*  Creation
 *  ==============================================================================================*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView)findViewById(R.id.listView);

        buildListView();

        prefName = getResources().getString(R.string.pref_name);
        prefToken = getResources().getString(R.string.pref_token);
        prefIp = getResources().getString(R.string.pref_ip);
        prefPort = getResources().getString(R.string.pref_port);

        SharedPreferences pref = getSharedPreferences(prefName, 0);
        token = pref.getString(prefToken, null);
        hostIp = pref.getString(prefIp, null);
        hostPort = pref.getInt(prefPort, 0);



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InstructionalGraphic ig = igs.get(position);
                if (timer != null){ //if there's already a timer, stop it first
                    timer.stop();
                }
                timer = new InstructionalGraphicTimer(MainActivity.this, "10.201.149.57", "8080", "abc", ig);
                timer.start();
                if (position != listPosition) //if user clicks different IG, then reset click counter
                    clicks = 0;
                Log.d("msg2", "listview click");
                clicks++;

                if (clicks > 0 && clicks % 2 == 0)
                    timer.stop();
                listPosition = position;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("msg1","what up Karrie");
            //natalie's code
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_disconnect) {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            StringBuffer buffer = new StringBuffer();
            buffer.append("http:/")
                    .append(hostIp)
                    .append(":")
                    .append(hostPort)
                    .append("/deactivate")
                    .append("?token=")
                    .append(token);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.DELETE, buffer.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences pref = getSharedPreferences(prefName, 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(prefToken, null);
                        editor.putString(prefIp, null);
                        editor.putInt(prefPort, 0);
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, ChooseEMILYActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Error sending request.", Toast.LENGTH_SHORT).show();
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

/*  Private Methods
 *  ==============================================================================================*/
    private void buildListView() {
        InstructionalGraphicDbAccess db = new InstructionalGraphicDbAccess(this); //initialize database
        Integer randnum = new Random().nextInt()%10000;
        InstructionalGraphic ig = new InstructionalGraphic("ig" + Integer.toString(randnum));
        ig.addImage(1,Integer.toString(R.drawable.images));
        ig.addImage(2,Integer.toString(R.drawable.images));
        ig.addImage(3,Integer.toString(R.drawable.images));
        ig.setInterval(2000);
        db.addGraphicToEnd(ig);
        igs = db.getOrderedGraphicList(); // get all InstructionalGraphics in database
        adapter = new GraphicAdapter(this, igs);
        list.setAdapter(adapter); //build the listview with the adapter
    }

}
