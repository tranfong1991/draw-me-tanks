package andytran.dmap_phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import core.db.InstructionalGraphicDbAccess;


public class Utils {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";


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
 *  @param endpoint The type of request.  Can use "/play", or "/stop"
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

        if (map != null && map.size() > 0) {
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
        Log.d("URL", buffer.toString());
        return buffer.toString();
    }

    public static String generateRandomString(int length){
        StringBuffer buffer = new StringBuffer();
        Random rand = new Random();

        for(int i = 0; i<length; i++){
            char c = ALPHABET.charAt(rand.nextInt(ALPHABET.length()));
            buffer.append(c);
        }

        return buffer.toString();
    }

/**
 *  Returns a valid Uri for the given private file information.  This is generally used with
 *  the file references stored in InstructionalGraphics.  Because of the way the phone stores
 *  stuff, the references will be graphic_somerandomjunk.  However, this does not represent
 *  the full file path to the image.
 *
 *  Use this function to get the full path.
 *
 *  By the way, this code was way harder to find than it should have been.
 *
 *  @usage
 *  Uri uri = Utils.refToUri(this, graphic.imageRefAt(3));
 *  imageView.setImageUri(uri);
 *
 *  @param context The activity you are in
 *  @param ref "graphic_lettersandjunk"
 *  @return The Uri
 */
    public static Uri refToUri(Context context, String ref) {
        return Uri.fromFile(new File(context.getFilesDir().getAbsolutePath() + "/" + ref));
    }

    public static AlertDialog error(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder .setTitle("An error has occured")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    public static void errorFromWorker(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override public void run() {
                error(activity, message).show();
            }
        });
    }
}

