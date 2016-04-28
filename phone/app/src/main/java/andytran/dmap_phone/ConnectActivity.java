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

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        final String prefName = getResources().getString(R.string.pref_name);
        final String prefToken = getResources().getString(R.string.pref_token);
        final String prefIp = getResources().getString(R.string.pref_ip);

        final SharedPreferences pref = getSharedPreferences(prefName, 0);
        if(pref.getString(prefToken, null) != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        final EditText ipTxt1 = (EditText)findViewById(R.id.txt_connect_ip_1);
        final EditText ipTxt2 = (EditText)findViewById(R.id.txt_connect_ip_2);
        final EditText ipTxt3 = (EditText)findViewById(R.id.txt_connect_ip_3);
        final EditText ipTxt4 = (EditText)findViewById(R.id.txt_connect_ip_4);
        final Button connectBtn = (Button)findViewById(R.id.btn_connect);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipTxt1.getText().toString() + "." +
                        ipTxt2.getText().toString() + "." +
                        ipTxt3.getText().toString() + "." +
                        ipTxt4.getText().toString();
                if(ip.length() > 0){
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
                                Toast.makeText(ConnectActivity.this, "Cannot obtain token from tablet. Please delete the tablet's token and try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, null);
                }
            }
        });
    }
}
