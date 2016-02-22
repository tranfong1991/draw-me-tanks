package andytran.dmap_tablet;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.Map;

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
    public static final int PORT = 8080;
    public static final int HTTP_OK = 200;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final String PACKAGE_NAME = "andytran.dmap_tablet";
    public static final String EXTRA_GRAPHIC_ID = "EXTRA_GRAPHIC_ID";
    public static final String EXTRA_ACTION = "EXTRA_ACTION";

    private static String TOKEN = "ABC123";

    private Context context;

    public DMAPServer(Context context)throws IOException{
        super(PORT);

        this.context = context;
        start();
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> params = session.getParms();

        //check if token is not present or doesn't match
        if(params.get("token") == null || !params.get("token").equals(TOKEN))
            return newFixedLengthResponse("{\"status\":" + HTTP_UNAUTHORIZED + "}");

        String uri = session.getUri();

        //PUT ROUTES HERE
        switch(uri){
            case "/generate":
                return generateToken();
            case "/play":
                return playGraphic(params);
            case "/stop":
                return stopGraphic();
            default:
                return newFixedLengthResponse("Nothing");
        }
    }

    private Response generateToken(){
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