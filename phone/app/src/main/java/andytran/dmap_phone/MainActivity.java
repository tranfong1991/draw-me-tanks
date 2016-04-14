package andytran.dmap_phone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

import timothy.dmap_phone.InstructionalGraphic;

public class MainActivity extends AppCompatActivity {
    private String prefName;
    private String prefToken;
    private String prefIp;
    private String prefPort;

    private String token;
    private String hostIp;
    private int hostPort;

    ListView list;
    GraphicAdapter adapter;

/*  Creation
 *  ==============================================================================================*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("id", "1");
        map.put("token", "token1");

        String URL = Util.buildURL("172.92.43.25", "8080", "display", map);
        Log.d("MainActivity", URL);

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
//        ArrayList<String> names = new ArrayList<>();
//        ArrayList<Integer> imageIds = new ArrayList<>();
//
//        Iterator<InstructionalGraphic> iterator  = DataHolder.instance().getGraphicsListIterator();
//        while(iterator.hasNext()) {
//            names.add(graphic.getName());
//            imageIds.add(graphic.idAt(0));
//        }
//
//        list.setAdapter(new GraphicAdapter(this, names.toArray(new String[names.size()]), imageIds.toArray(new Integer[imageIds.size()])));
    }

}
