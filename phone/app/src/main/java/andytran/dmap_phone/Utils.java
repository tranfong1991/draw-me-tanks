package andytran.dmap_phone;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

public class Utils {
    static void sendPackage(Context context, int method, String URL, Response.Listener<String> listener, Response.ErrorListener errorListener){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(method, URL, listener, errorListener);
        queue.add(stringRequest); //sends the package
    }

    static String buildURL(String IP, String port, String endpoint, HashMap<String,String> map){
        StringBuffer buffer = new StringBuffer();
        buffer.append(IP);
        buffer.append(":");
        buffer.append(port);
        buffer.append("/");
        buffer.append(endpoint);
        if (map.size() > 0) {
            buffer.append("?");
            Object[] mapArray = map.keySet().toArray(); //convert map to array to iterate through
            for (int i = 0; i < mapArray.length; i++) { //append whatever parameters are stored in map
                buffer.append((String)mapArray[i]); //append key
                buffer.append("=");
                buffer.append(map.get((String)mapArray[i])); //append value
                if (i != mapArray.length - 1)
                    buffer.append("&");
            }
        }
        String url = buffer.toString();
        return url;
    }
}
