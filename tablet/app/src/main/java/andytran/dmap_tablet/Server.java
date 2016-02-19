package andytran.dmap_tablet;

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
public class Server extends NanoHTTPD {
    public static int PORT = 8080;
    private static String TOKEN = "ABC123";

    public Server()throws IOException{
        super(PORT);
        start();
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> params = session.getParms();

        //check if token is not present or doesn't match
        if(params.get("token") == null || !params.get("token").equals(TOKEN))
            return newFixedLengthResponse("{\"status\":\"unauthorized\"}");

        String uri = session.getUri();

        //PUT ROUTES HERE
        switch(uri){
            case "/generate":
                return generateToken();
            case "/plain":
                return getMagicNumber();
            case "/json":
                return getJSONNumber();
            default:
                return newFixedLengthResponse("Nothing");
        }
    }

    private Response generateToken(){
        return null;
    }

    private Response getMagicNumber(){
        return newFixedLengthResponse("777");
    }

    private Response getJSONNumber(){
        return newFixedLengthResponse("{\"number\": 777}");
    }
}