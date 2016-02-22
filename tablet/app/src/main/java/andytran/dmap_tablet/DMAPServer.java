package andytran.dmap_tablet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Andy Tran on 2/18/2016.
 * Basic webserver for tablet
 *
 * TO ADD A ROUTE:
 * 1. Add a case in the switch statement inside serve()
 * 2. In each case, add the name of the route with forward slash (/) in front
 * 3. For each new route, define a function that returns a Response object.
 * 4. In case the route returns a JSON object, simply add quotes around keys
 */
public class DMAPServer extends NanoHTTPD {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int TOKEN_LENGTH = 10;

    public static final String PREF_NAME = "DMAP_PREF";
    public static final String PREF_TOKEN = "DMAP_TOKEN";
    public static final String PACKAGE_NAME = "andytran.dmap_tablet";
    public static final String EXTRA_GRAPHIC_ID = "EXTRA_GRAPHIC_ID";
    public static final String EXTRA_ACTION = "EXTRA_ACTION";
    public static final int PORT = 8080;
    public static final int HTTP_OK = 200;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_NOT_FOUND = 404;

    private String token;
    private Context context;

    public DMAPServer(Context context)throws IOException{
        super(PORT);

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        this.token = pref.getString(PREF_TOKEN, null);
        this.context = context;

        start();
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> params = session.getParms();
        String uri = session.getUri();
        String method = session.getMethod().name();

        //when the phone first connects to the tablet
        if(method.equals("GET") && uri.equals("/generate"))
            return generateToken();

        //check if token is not present or doesn't match
        if(params.get("token") == null || !params.get("token").equals(token))
            return newFixedLengthResponse("{\"status\":" + HTTP_UNAUTHORIZED + "}");

        switch(method){
            case "GET":{
                switch(uri){
                    case "/play":
                        return playGraphic(params);
                    case "/stop":
                        return stopGraphic();
                }
                break;
            }
            case "POST":{
                switch(uri){
                    case "/graphic":
                        return postGraphic(params);
                }
                break;
            }
            case "DELETE":{
                switch(uri){
                    case "/graphic":
                        return deleteGraphic(params);
                    case "/deactivate":
                        return deactivateToken();
                }
                break;
            }
        }

        return newFixedLengthResponse("{\"status\":" + HTTP_NOT_FOUND + "}");
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
        editor.commit();

        this.token = buffer.toString();

        return newFixedLengthResponse("{\"token\" : \"" + buffer.toString() + "\"}");
    }

    private Response deactivateToken(){
        if(token == null)
            return newFixedLengthResponse("{\"status\":" + HTTP_NOT_FOUND + "}");

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_TOKEN, null);
        editor.commit();

        this.token = null;

        return newFixedLengthResponse("{\"status\" : " + HTTP_OK + "}");
    }

    private Response postGraphic(Map<String, String> params){
        return null;
    }

    private Response deleteGraphic(Map<String, String> params){
        return null;
    }

    private Response playGraphic(Map<String, String> params){
        String graphicId = params.get("id");

        Intent intent = new Intent(PACKAGE_NAME);
        intent.putExtra(EXTRA_GRAPHIC_ID, graphicId);
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
}