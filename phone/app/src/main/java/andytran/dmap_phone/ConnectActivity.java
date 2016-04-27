package andytran.dmap_phone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        final EditText ipTxt = (EditText)findViewById(R.id.txt_connect_ip);
        final Button connectBtn = (Button)findViewById(R.id.btn_connect);

        final String prefName = getResources().getString(R.string.pref_name);
        final String prefToken = getResources().getString(R.string.pref_token);
        final String prefIp = getResources().getString(R.string.pref_ip);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipTxt.getText().toString();
                if(ip.length() > 0){
                    SharedPreferences pref = getSharedPreferences(prefName, 0);
                    final SharedPreferences.Editor editor = pref.edit();
                    editor.putString(prefIp, ip);
                    editor.apply();

                    String url = Utils.buildURL(ip, "8080", "generate", null);
                    Utils.sendPackage(ConnectActivity.this, Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                String token = json.getString("token");

                                editor.putString(prefToken, token);
                                editor.apply();

                                Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }catch(JSONException e){
                                Toast.makeText(ConnectActivity.this, "Cannot parse JSON", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, null);
                }
            }
        });
    }
}
