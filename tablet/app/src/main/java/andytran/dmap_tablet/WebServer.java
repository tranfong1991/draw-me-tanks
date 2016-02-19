package andytran.dmap_tablet;

import android.util.Log;

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
public class WebServer extends NanoHTTPD {
    public static int PORT = 8080;
    private static String TOKEN = "ABC123";

    public WebServer()throws IOException{
        super(PORT);
        start();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        //PUT ROUTES HERE
        switch(uri){
            case "/validate":
                return validateToken(session.getParms().get("token"));
            case "/plain":
                return getMagicNumber();
            case "/json":
                return getJSONNumber();
            default:
                return newFixedLengthResponse("Nothing");
        }
    }

    private Response validateToken(String token){
        String json;
        if(token.equals(TOKEN))
            json = "{\"status\":\"success\"}";
        else json = "{\"status\":\"fail\"}";

        return newFixedLengthResponse(json);
    }

    private Response getMagicNumber(){
        return newFixedLengthResponse("777");
    }

    private Response getJSONNumber(){
        return newFixedLengthResponse("{\"number\": 777}");
    }
}