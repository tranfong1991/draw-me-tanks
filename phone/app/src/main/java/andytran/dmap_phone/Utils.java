package andytran.dmap_phone;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

public class Utils {
/**
 *  Sends a request to the tablet.
 *  @param context The current activity
 *  @param method Use Request.Method.METHOD_TYPE
 *  @param URL Use Utils.buildURL for this
 *  @param listener Callback for what happens upon success
 *  @param errorListener Callback for what happens upon failure
 */
    public static void sendPackage(Context context, int method, String URL, Response.Listener<String> listener, Response.ErrorListener errorListener){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(method, URL, listener, errorListener);
        queue.add(stringRequest); //sends the package
    }

/**
 *  Constructs a URL request to send to the tablet
 *  @param IP
 *  @param port
 *  @param endpoint The type of request.  Can use "/playGraphic", or "/stopGraphic"
 *  @param map List of parameters for the request
 *  @return The URL to use in the URL parameter of sendPackage
 */
    public static String buildURL(String IP, String port, String endpoint, HashMap<String,String> map){
        StringBuffer buffer = new StringBuffer();

        buffer.append("http://");
        buffer.append(IP);
        buffer.append(":");
        buffer.append(port);
        buffer.append("/");
        buffer.append(endpoint);

        if (map.size() > 0) {
            buffer.append("?");

            Object[] mapArray = map.keySet().toArray(); //convert map to array to iterate through
            for (int i = 0; i < mapArray.length; i++) { //append whatever parameters are stored in map
                String key = (String)mapArray[i];

                buffer.append(key); //append key
                buffer.append("=");
                buffer.append(map.get(key)); //append value

                if (i != mapArray.length - 1)
                    buffer.append("&");
            }
        }
        return buffer.toString();
    }
}
