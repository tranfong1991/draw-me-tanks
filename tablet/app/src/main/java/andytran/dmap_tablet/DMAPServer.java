package andytran.dmap_tablet;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Andy Tran on 2/18/2016.
 * Basic web server for tablet
 */
public class DMAPServer extends NanoHTTPD {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int TOKEN_LENGTH = 20;

    public static final String PREF_NAME = "DMAP_PREF";
    public static final String PREF_TOKEN = "DMAP_TOKEN";
    public static final String PACKAGE_NAME = "andytran.dmap_tablet";
    public static final String EXTRA_GRAPHIC_NAME = "EXTRA_GRAPHIC_NAME";
    public static final String EXTRA_ACTION = "EXTRA_ACTION";
    public static final int PORT = 8080;
    public static final int HTTP_OK = 200;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_NOT_FOUND = 404;

    private String token = "123";
    private Context context;

    public DMAPServer(Context context)throws IOException{
        super(PORT);

//        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
//        this.token = pref.getString(PREF_TOKEN, null);
        this.context = context;

        start();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Method method = session.getMethod();
        Map<String, String> params = session.getParms();

        //when the phone first connects to the tablet
        if(method == Method.GET && uri.equals("/generate"))
            return generateToken();

        //check if token is not present or doesn't match
        if(params.get("token") == null || !params.get("token").equals(token))
            return newFixedLengthResponse("{\"status\":" + HTTP_UNAUTHORIZED + "}");

        if(method == Method.GET)
            return runGet(uri, params);
        else if(method == Method.POST)
            return runPost(uri, params, session);
        else if(method == Method.DELETE)
            return runDelete(uri, params);

        return newFixedLengthResponse("{\"status\":" + HTTP_NOT_FOUND + "}");
    }

    private Response runGet(String uri, Map<String, String> params){
        switch(uri){
            case "/play":
                return playGraphic(params);
            case "/stop":
                return stopGraphic();
            default:
                return newFixedLengthResponse("{\"status\":" + HTTP_NOT_FOUND + "}");
        }
    }

    private Response runPost(String uri, Map<String, String> params, IHTTPSession session){
        switch(uri){
            case "/graphic":
                return postGraphic(params, session);
            default:
                return newFixedLengthResponse("{\"status\":" + HTTP_NOT_FOUND + "}");
        }
    }

    private Response runDelete(String uri, Map<String, String> params){
        switch(uri){
            case "/graphic":
                return deleteGraphic(params);
            case "/deactivate":
                return deactivateToken();
            default:
                return newFixedLengthResponse("{\"status\":" + HTTP_NOT_FOUND + "}");
        }
    }

    private Response generateToken(){
        //device already has a token
        if(token != null)
            return newFixedLengthResponse("{\"status\":" + HTTP_UNAUTHORIZED + "}");

        StringBuffer buffer = new StringBuffer();
        Random rand = new Random();

        //randomly generate a token
        for(int i = 0; i<TOKEN_LENGTH; i++){
            char c = ALPHABET.charAt(rand.nextInt(ALPHABET.length()));
            buffer.append(c);
        }

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_TOKEN, buffer.toString());
        editor.apply();

        this.token = buffer.toString();

        return newFixedLengthResponse("{\"token\" : \"" + buffer.toString() + "\"}");
    }

    private Response deactivateToken(){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_TOKEN, null);
        editor.apply();

        this.token = null;

        return newFixedLengthResponse("{\"status\" : " + HTTP_OK + "}");
    }

    //this generates an ID for the new graphic and sends the ID back
    private Response postGraphic(Map<String, String> params, IHTTPSession session){
        try {
            Map<String, String> files = new HashMap<>();
            session.parseBody(files);

            String fileName = params.get("name");
            Set<String> keys = files.keySet();
            for(String key: keys){
                saveFile(files.get(key), fileName);

                Intent intent = new Intent(PACKAGE_NAME);
                intent.putExtra(EXTRA_ACTION, "show");
                intent.putExtra(EXTRA_GRAPHIC_NAME, fileName);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        } catch (IOException | ResponseException e) {
            e.printStackTrace();
        }

        return newFixedLengthResponse("{\"status\" : " + HTTP_OK + "}");
    }

    private Response deleteGraphic(Map<String, String> params){
        return null;
    }

    private Response playGraphic(Map<String, String> params){
        String graphicId = params.get("id");

        Intent intent = new Intent(PACKAGE_NAME);
        intent.putExtra(EXTRA_GRAPHIC_NAME, graphicId);
        intent.putExtra(EXTRA_ACTION, "play");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        return newFixedLengthResponse("{\"status\" : " + HTTP_OK + "}");
    }

    private Response stopGraphic(){
        Intent intent = new Intent(PACKAGE_NAME);
        intent.putExtra(EXTRA_ACTION, "stop");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        return newFixedLengthResponse("{\"status\" : " + HTTP_OK + "}");
    }

    private void saveFile(String cacheLocation, String fileName){
        File src = new File(cacheLocation);

        FileInputStream in;
        FileOutputStream out;
        try{
            in = new FileInputStream(src);
            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            byte[] buf = new byte[65536];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}